package com.prvulovic.rekvestlin.ui.routes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.prvulovic.rekvestlin.model.Route
import com.prvulovic.rekvestlin_core.RekvestlinComponent
import com.prvulovic.rekvestlin_core.inject
import com.prvulovic.rekvestlin_core.instance.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class RouteListViewModel : ViewModel(), RekvestlinComponent{

    val routeListRequest by inject<List<Route>>("RouteList")

    val routeListLiveData = liveData{
        emit(Resource.loading(null))
        val results = viewModelScope.async(Dispatchers.IO) {
            routeListRequest.run()
        }
        emit(results.await())
    }
}