package com.prvulovic.rekvestlin

import android.app.Application
import com.prvulovic.rekvestlin.network.rekvestlinConfiguration

class RekvestlinApp : Application(){

    override fun onCreate() {
        super.onCreate()

        rekvestlinConfiguration()
    }

}