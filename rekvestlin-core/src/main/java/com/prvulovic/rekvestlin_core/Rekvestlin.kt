package com.prvulovic.rekvestlin_core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.prvulovic.rekvestlin_core.configuration.RekvestlinConfig
import com.prvulovic.rekvestlin_core.instance.RequestInstance
import com.prvulovic.rekvestlin_core.module.Module
import com.prvulovic.rekvestlin_core.registry.RequestRegistry
import okhttp3.OkHttpClient
import java.util.concurrent.ConcurrentHashMap

class Rekvestlin {

    private var configured = false

    internal lateinit var baseUrl: String
    internal var urlConstantExtension: String? = null

    internal val okHttpClientBuilder = OkHttpClient.Builder()
    internal val gsonBuilder = GsonBuilder()
    internal lateinit var gson: Gson
    internal lateinit var okHttpClient: OkHttpClient

    val headers = ConcurrentHashMap<String, String>()

    internal val requestRegistry = RequestRegistry()

    fun getRequestRegistry() = requestRegistry

    fun applyConfiguration(configuration: RekvestlinConfig){
        baseUrl = configuration.configuredBaseUrl ?: ""
        urlConstantExtension = configuration.configuredExtensionUrl ?: ""

        headers.putAll(configuration.configuredHeaders)

        okHttpClient = okHttpClientBuilder.build()
        gson = gsonBuilder.create()

        configured = true
    }

    fun isConfigured(): Boolean = configured

    fun loadModules(modules: List<Module>) {
        requestRegistry.loadModules(modules)
    }

    inline fun <reified T: Any> get(name: String): RequestInstance<T> {
        return getRequestRegistry().findByName(name)
    }

    fun <T: Any> inject(name: String): Lazy<RequestInstance<T>> {
        return getRequestRegistry().injectByName(name)
    }
}