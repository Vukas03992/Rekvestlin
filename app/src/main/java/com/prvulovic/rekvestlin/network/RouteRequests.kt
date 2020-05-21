package com.prvulovic.rekvestlin.network

import com.prvulovic.rekvestlin.model.Route
import com.prvulovic.rekvestlin_core.Url
import com.prvulovic.rekvestlin_dsl.*

val routeRequests = requests {

    createRequest<List<Route>>("RouteList", Route::class){

        SET { Url } AS "/routes"

        whenSuccess {
            PARSE AS OBJECT and {
                take("data") then {
                    PARSE AS ARRAY
                }
            }
        }
    }

}