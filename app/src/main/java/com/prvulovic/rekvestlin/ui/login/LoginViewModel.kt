package com.prvulovic.rekvestlin.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.prvulovic.rekvestlin.network.UserCredentials
import com.prvulovic.rekvestlin_core.RekvestlinComponent
import com.prvulovic.rekvestlin_core.inject
import com.prvulovic.rekvestlin_core.instance.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async


class LoginViewModel : ViewModel(), RekvestlinComponent{

    val loginRequest by inject<String>("Login")

    val loginLiveData = liveData{
        emit(Resource.loading(null))
        val results = viewModelScope.async(Dispatchers.IO) {
            loginRequest.makeRequestBody(UserCredentials("vukasin.prvulovic@bstorm.co", "123123"))
            loginRequest.run()
        }
        emit(results.await())
    }
}