package com.prvulovic.rekvestlin_core.configuration

import com.google.gson.GsonBuilder
import com.prvulovic.rekvestlin_core.BaseUrl
import com.prvulovic.rekvestlin_core.UrlConstantExtension
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.ConcurrentHashMap

class RekvestlinConfig(

    private val okHttpClientBuilder: OkHttpClient.Builder,
    private val gsonBuilder: GsonBuilder) {

    var configuredBaseUrl: String? = null
    var configuredExtensionUrl: String? = null

    var configuredHeaders = ConcurrentHashMap<String, String>()

    fun logger(setLogger: () -> Interceptor){
        okHttpClientBuilder.addInterceptor(setLogger())
    }

    fun more(configureBuilder: OkHttpClient.Builder.() -> Unit){
        okHttpClientBuilder.configureBuilder()
    }

    fun headers(addHeaders: ConcurrentHashMap<String, String>.() -> Unit){
        configuredHeaders.addHeaders()
    }

    fun gsonConfiguration(configureGson: GsonBuilder.() -> Unit){
        gsonBuilder.configureGson()
    }

    fun SET(value: () -> String): String = value()

    infix fun String.AS(property: String){
        when(this){
            BaseUrl -> {configuredBaseUrl = property}
            UrlConstantExtension -> {configuredExtensionUrl = property}
        }
    }
}