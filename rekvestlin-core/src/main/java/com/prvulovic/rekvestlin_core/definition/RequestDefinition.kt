package com.prvulovic.rekvestlin_core.definition

import android.util.Log.e
import com.google.gson.*
import com.google.gson.stream.JsonToken
import com.prvulovic.rekvestlin_core.*
import com.prvulovic.rekvestlin_core.context.GlobalContext
import com.prvulovic.rekvestlin_core.instance.Method
import com.prvulovic.rekvestlin_core.instance.Method.GET
import com.prvulovic.rekvestlin_core.instance.RequestInstance
import com.prvulovic.rekvestlin_core.instance.Resource
import com.prvulovic.rekvestlin_core.parsing.deserialize
import com.prvulovic.rekvestlin_dsl.ParsingElement
import okhttp3.RequestBody
import okhttp3.Response
import java.io.StringReader
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class RequestDefinition<T: Any>(val modelClass: KClass<T>, val typeClass: KClass<*>) {

    val rekvestlin = GlobalContext.getRekvestlin()

    var cached: Boolean = false
    var groupName: String? = null

    var baseUrl: String? = null
    fun getFinalBaseUrl(): String {
        if (baseUrl != null) return baseUrl!!
        if (groupName != null){
            val groupRequest = rekvestlin.requestRegistry.getGroupByName(groupName!!)
            if (groupRequest.configuredBaseUrl != null) return groupRequest.configuredBaseUrl!!
        }
        return rekvestlin.baseUrl
    }

    var urlConstantExtension: String? = null
    fun getFinalUrlConstantExtension(): String {
        if (urlConstantExtension != null) return urlConstantExtension!!
        if (groupName != null){
            val groupRequest = rekvestlin.requestRegistry.getGroupByName(groupName!!)
            if (groupRequest.configuredExtensionUrl != null) return groupRequest.configuredExtensionUrl!!
        }
        if (rekvestlin.urlConstantExtension != null) return rekvestlin.urlConstantExtension!!
        return ""
    }

    var urlExtension: String? = null

    var method: Method = GET
    var requestBodyBuilder: (Any.() -> RequestBody)? = null
    val headerParams =  ConcurrentHashMap<String, String>()
    fun getFinalHeaderParams(): ConcurrentHashMap<String, String> {
        val rekvestlinHeaderParams = rekvestlin.headers
        if (groupName != null){
            val groupRequest = rekvestlin.requestRegistry.getGroupByName(groupName!!)
            rekvestlinHeaderParams.putAll(groupRequest.headersMap)
        }
        rekvestlinHeaderParams.putAll(headerParams)
        return rekvestlinHeaderParams
    }

    var doOnBefore: (Gson.() -> Unit)? = null
    var parsingElement: ParsingElement<T>? = null
    var doOnAfter: (T.() -> Unit)? = null

    var whenError: (Response.() -> Resource<T?>)? = null

    private var instance: RequestInstance<T>? = null

    fun create() {
        if (cached) {
                instance = RequestInstance(this)
        }
    }

    fun getRequestInstance(): RequestInstance<T> {
        return if (cached) {
            if (instance != null) instance!! else {
                create()
                instance!!
            }
        } else {
            RequestInstance(this)
        }
    }

    fun getOnSuccess(response: Response, gson: Gson): T? {
        doOnBefore?.let { gson.it() }
        val instance = parsingAlgorythm(response, gson, parsingElement!!)
        doOnAfter?.let { instance?.it() }
        return instance
    }

    private fun parsingAlgorythm(response: Response, gson: Gson, parsingElement: ParsingElement<T>, jsonElement: JsonElement? = null): T? {

        if (parsingElement.parseAsType != null){
            val json = if (parsingElement.root) parseToJson(response, gson) else jsonElement!!
            return when(parsingElement.parseAsType){
                STRING -> json.asJsonObject[parsingElement.key].asString as T
                INT -> json.asInt as T
                LONG -> json.asLong as T
                DOUBLE -> json.asDouble as T
                FLOAT -> json.asFloat as T
                else -> null
            }
        }

        val json = if (parsingElement.root) {
            parseToJson(response, gson)
        }
        else {
            val data = jsonElement!!.asJsonObject[parsingElement.key]
            data
        }

        return if (parsingElement.nestedParsingElement == null){
            if (parsingElement.withDeserializer){
                deserialize(StringReader(json.toString()),  modelClass, typeClass)
            }else{
                parseWithReflection(parsingElement.parsingPairs, json.asJsonObject)
            }
        }else{
            parsingAlgorythm(response, gson, parsingElement.nestedParsingElement!!, json)
        }
    }

    private fun parseToJson(response: Response, gson: Gson): JsonElement {
        val jsonReader = gson.newJsonReader(response.body?.charStream())
        val json = JsonParser.parseReader(jsonReader)
        if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
            throw JsonIOException("JSON document was not fully consumed.")
        }
        return json
    }

    private fun parseWithReflection(properties: Map<String, String>, json: JsonObject): T? {
        val instance = modelClass.objectInstance as T
        modelClass.members.forEach {
            if (properties.containsKey(it.name)){
                it.call(json[properties[it.name]])
            }
        }
        return instance
    }
}