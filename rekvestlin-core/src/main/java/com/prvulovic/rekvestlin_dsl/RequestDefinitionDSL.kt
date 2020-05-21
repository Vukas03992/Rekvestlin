package com.prvulovic.rekvestlin_dsl

import com.prvulovic.rekvestlin_core.*
import com.prvulovic.rekvestlin_core.definition.RequestDefinition
import com.prvulovic.rekvestlin_core.instance.Method
import okhttp3.RequestBody
import okhttp3.Response
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class RequestDefinitionDSL<T: Any>(val modelClass: KClass<T>,val typeClass: KClass<*>) {

    private var groupName: String? = null

    private var baseUrl: String? = null
    private var extensionUrl: String? = null
    private var url: String? = null
    private val headersMap = ConcurrentHashMap<String, String>()

    private var method: Method? = null
    private var requestBodyBuilder: (Any.() -> RequestBody)? = null

    private lateinit var onSuccessBuilder: OnSuccessBuilder<T>

    fun headers(vararg pairs: Pair<String, String>){
        pairs.forEach {
            headersMap[it.first] = it.second
        }
    }

    fun <S> requestBody(requestBodyBuilder: S.() -> RequestBody){
        this.requestBodyBuilder = requestBodyBuilder as (Any.() -> RequestBody)
    }

    fun whenSuccess(successAction: OnSuccessBuilder<T>.() -> Unit){
        onSuccessBuilder = OnSuccessBuilder(modelClass)
        onSuccessBuilder.successAction()
    }

    fun whenError(errorAction: Response.() -> Unit) {

    }

    fun SET(value: () -> String): String = value()

    infix fun String.AS(property: String){
        when(this){
            BaseUrl -> {baseUrl = property}
            UrlConstantExtension -> {extensionUrl = property}
            Url -> {url = property}
            Method -> {method = when(property){
                GET -> com.prvulovic.rekvestlin_core.instance.Method.GET
                POST -> com.prvulovic.rekvestlin_core.instance.Method.POST
                PUT -> com.prvulovic.rekvestlin_core.instance.Method.PUT
                DELETE -> com.prvulovic.rekvestlin_core.instance.Method.DELETE
                else -> null
            }}
        }
    }

    infix fun grouped.by(value: ()->String){
        groupName = value()
    }

    fun create(): RequestDefinition<T> {
        val requestDefinition = RequestDefinition(modelClass, typeClass)
        requestDefinition.apply {
            groupName = this@RequestDefinitionDSL.groupName

            this@RequestDefinitionDSL.baseUrl?.let { baseUrl = it }
            extensionUrl?.let { urlConstantExtension = it }
            url?.let { urlExtension = it }

            headersMap.let{ headerParams?.putAll(headersMap)}

            this@RequestDefinitionDSL.method?.let {  method = it  }
            if (method != com.prvulovic.rekvestlin_core.instance.Method.GET){
                requestBodyBuilder = this@RequestDefinitionDSL.requestBodyBuilder
            }

            doOnBefore = onSuccessBuilder.doOnBeforeParsing
            parsingElement = onSuccessBuilder.parsingElement
            doOnAfter = onSuccessBuilder.doOnAfterParsing
        }
        return requestDefinition
    }
}

object grouped