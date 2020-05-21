package com.prvulovic.rekvestlin.network

import com.prvulovic.rekvestlin_dsl.requests

val groupedRequests = requests {

    groupRequests("authenticated"){
        headers(
            "Authentication" to "access Token"
        )
    }
}