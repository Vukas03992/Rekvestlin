package com.prvulovic.rekvestlin.network

import com.prvulovic.rekvestlin_core.Url
import com.prvulovic.rekvestlin_dsl.*

val booksRequests = requests {

    createRequest<List<String>>("getAllBooks"){

        SET {"/books"} AS Url

        whenSuccess {

        }
    }

    createRequest<List<String>>("getMyBooks"){

        grouped by {"authorization"}

        SET {"/books"} AS Url

        whenSuccess {

        }
    }
}