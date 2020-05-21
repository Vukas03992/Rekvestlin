package com.prvulovic.rekvestlin.network

import com.prvulovic.rekvestlin_core.BaseUrl
import com.prvulovic.rekvestlin_core.UrlConstantExtension
import com.prvulovic.rekvestlin_core.context.configureRekvestlin
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

val rekvestlinConfiguration = configureRekvestlin {

    configure {

        SET { BaseUrl } AS "https://staging.reijets.bstorm.app"
        SET { UrlConstantExtension } AS "/api/v1"

        logger {
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

        headers {
            put("Accept", "application/json")
        }

        gsonConfiguration {

        }

        more {
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
        }

    }

    modules(loginRequests + routeRequests + gitHubRequests)
}