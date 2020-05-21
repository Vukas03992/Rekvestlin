package com.prvulovic.rekvestlin_core.instance

import com.google.gson.JsonElement
import com.google.gson.JsonIOException
import com.google.gson.JsonParser
import com.google.gson.stream.JsonToken
import com.prvulovic.rekvestlin_core.context.GlobalContext
import com.prvulovic.rekvestlin_core.definition.RequestDefinition
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class RequestInstance<T: Any>(val definition: RequestDefinition<T>) {

    val rekvestlin = GlobalContext.getRekvestlin()

    var urlPattern: (() -> String)? = null

    lateinit var requestBody: RequestBody

    private fun buildRequest(): Request{

        val requestBuilder = Request.Builder()
            .url("${definition.getFinalBaseUrl()}${definition.getFinalUrlConstantExtension()}${if (urlPattern != null) urlPattern!!.invoke() else definition.urlExtension}")
        definition.getFinalHeaderParams().map {
            requestBuilder.addHeader(it.key, it.value)
        }
        when(definition.method){
            Method.GET -> {requestBuilder.get()}
            Method.POST -> {
                requestBuilder.post(requestBody)
            }
            Method.PUT -> {
                requestBuilder.put(requestBody)
            }
            Method.DELETE -> {
                requestBuilder.delete(null)
            }
        }

        return requestBuilder.build()
    }

    fun urlWithPathParams(urlPattern: (() -> String)?) {
        this.urlPattern = urlPattern
    }

    fun <S> makeRequestBody(instance: S){
        requestBody = definition.requestBodyBuilder!!.invoke(instance as Any)
    }

    fun run(): Resource<T?> {
        try {
            val response = rekvestlin.okHttpClient.newCall(buildRequest()).execute()
            if(!response.isSuccessful){
                val ioException = IOException("Unexpected code $response")
                ioException.printStackTrace()
                try {
                    return onFailure(response, response?.message, ioException)
                }finally {
                    response?.body?.close()
                }
            }
            return try{
                onSuccess(response)
            }catch (e: Exception){
                e.printStackTrace()
                onFailure(response, response?.message, e)
            }finally {
                response?.body?.close()
            }
        }catch (e: IOException){
            e.printStackTrace()
            return onFailure(null, e.message, e)
        }
    }

    fun onFailure(response: Response?, message: String?, e: Exception): Resource<T?> {
        val processedMessage = response?.let {
            try {
                val error = parseToJson(response).asJsonObject
                return@let error["error"].asString
            } catch (e1: Exception) {
                e1.printStackTrace()
                return@let message
            }
        } ?: message
        return Resource.error(null, e, processedMessage)
    }

    fun onSuccess(response: Response): Resource<T?>{
        val data = definition.getOnSuccess(response, rekvestlin.gson)
        return Resource.success(data)
    }

    fun parseToJson(response: Response?): JsonElement {
        val jsonReader = rekvestlin.gson.newJsonReader(response?.body?.charStream())
        val json = JsonParser.parseReader(jsonReader)
        if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
            throw JsonIOException("JSON document was not fully consumed.")
        }
        return json
    }
}

@Suppress("DataClassPrivateConstructor")
data class Resource<T> private constructor(
    val data: T?,
    val status: Status,
    val e: Exception?,
    val msg: String?
) {
    @Suppress("UNCHECKED_CAST")
    companion object {
        @JvmStatic
        fun <T> success(data: T?) = Resource(data, Status.SUCCESS, null, null)

        @JvmStatic
        fun <T> loading(data: T?) = Resource(data, Status.LOADING, null, null)

        @JvmStatic
        fun <T> error(data: T?, e: Exception?, msg: String?) =
            Resource(data, Status.ERROR, e, msg)
    }
}

enum class Status {
    LOADING,
    SUCCESS,
    ERROR
}

enum class Method{
    GET, POST, PUT, DELETE
}