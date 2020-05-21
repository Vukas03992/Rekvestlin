package com.prvulovic.rekvestlin_core.definition

import com.prvulovic.rekvestlin_core.BaseUrl
import com.prvulovic.rekvestlin_core.UrlConstantExtension
import java.util.concurrent.ConcurrentHashMap

class GroupRequestDefinition {

    var configuredBaseUrl: String? = null
    var configuredExtensionUrl: String? = null

    val headersMap = ConcurrentHashMap<String, String>()

    fun headers(vararg pairs: Pair<String, String>){
        pairs.forEach {
            headersMap[it.first] = it.second
        }
    }

    fun SET(value: () -> String): String = value()

    infix fun String.AS(property: String){
        when(this){
            BaseUrl -> {configuredBaseUrl = property}
            UrlConstantExtension -> {configuredExtensionUrl = property}
        }
    }
}