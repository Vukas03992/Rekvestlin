package com.prvulovic.rekvestlin.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.prvulovic.rekvestlin.R
import com.prvulovic.rekvestlin.model.UserX
import com.prvulovic.rekvestlin_android.inject
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment: Fragment(R.layout.fragment_login){

    val loginViewModel by lazy { ViewModelProviders.of(this)[LoginViewModel::class.java] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel.loginLiveData.observe(this, Observer{
            login_response.text = it.data ?: "no data"
        })
        routes_text.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_routeListFragment)
        }
    }
}