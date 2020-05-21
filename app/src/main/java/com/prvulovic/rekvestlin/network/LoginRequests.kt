package com.prvulovic.rekvestlin.network

import com.prvulovic.rekvestlin_dsl.*
import com.prvulovic.rekvestlin_core.*
import okhttp3.FormBody

val loginRequests = requests {

    createRequest<String>("Login"){

        SET { Url } AS "/auth/login"

        SET { Method } AS POST

        requestBody<UserCredentials> {
            val formBody = FormBody.Builder()
            formBody.add("email", email)
            formBody.add("password", password)
            formBody.build()
        }

        whenSuccess {
            PARSE AS OBJECT and {
                PARSE("access_token") AS STRING
            }
        }
    }
}

data class UserCredentials(
    val email: String,
    val password: String
)