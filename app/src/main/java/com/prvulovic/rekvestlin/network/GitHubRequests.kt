package com.prvulovic.rekvestlin.network

import android.util.Log.e
import com.prvulovic.rekvestlin.model.UserX
import com.prvulovic.rekvestlin_core.BaseUrl
import com.prvulovic.rekvestlin_core.UrlConstantExtension
import com.prvulovic.rekvestlin_dsl.OBJECT
import com.prvulovic.rekvestlin_dsl.PARSE
import com.prvulovic.rekvestlin_dsl.requests

val gitHubRequests = requests {

    createRequest<UserX>("user"){

        SET { BaseUrl } AS "https://api.github.com"
        SET { UrlConstantExtension } AS ""

        whenSuccess {
            PARSE AS OBJECT
            doOnAfterParsing {
                e("USER", toString())
            }
        }

    }

}