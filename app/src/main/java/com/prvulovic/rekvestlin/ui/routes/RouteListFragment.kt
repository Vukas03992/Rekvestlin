package com.prvulovic.rekvestlin.ui.routes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.prvulovic.rekvestlin.R
import kotlinx.android.synthetic.main.fragment_routes.*

class RouteListFragment : Fragment(R.layout.fragment_routes){

    private val routeListViewModel: RouteListViewModel by viewModels { ViewModelProvider.NewInstanceFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        routeListViewModel.routeListLiveData.observe(this, Observer {
            val routes = buildString {
                it.data?.let {list ->
                    list.forEach {
                        append(it)
                        append("\n\n\n")
                    }
                }
            }
            routes_text.text = routes
        })
    }
}