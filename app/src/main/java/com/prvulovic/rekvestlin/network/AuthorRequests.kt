package com.prvulovic.rekvestlin.network

import com.prvulovic.rekvestlin.model.Author
import com.prvulovic.rekvestlin_dsl.*
import com.prvulovic.rekvestlin_core.BaseUrl
import com.prvulovic.rekvestlin_core.Url
import com.prvulovic.rekvestlin_core.UrlConstantExtension

val authorRequests = requests{

    createRequest<Author>("getAuthor"){

        SET { BaseUrl } AS "www.google.com"
        SET { UrlConstantExtension } AS "/api/v1"
        SET { Url } AS "/login"

        //________________ OnSuccess _______________

        //CASE 1
        whenSuccess {
            doOnBeforeParsing {

            }

            doOnAfterParsing {

            }
        }

        //CASE 2
        whenSuccess {

        }

        //CASE 3
        whenSuccess {

        }

        //_____________________________________________

        //__________________ OnError __________________

        whenError {

        }

        //_____________________________________________
    }
}