package com.prvulovic.rekvestlin_core

import com.prvulovic.rekvestlin_core.configuration.RekvestlinConfig
import com.prvulovic.rekvestlin_core.module.Module

class RekvestlinBuilder {

    private val rekvestlin = Rekvestlin()
    val instance: Rekvestlin
        get() {
            if (rekvestlin.isConfigured()) return rekvestlin
            throw IllegalStateException("Rekvestlin is not initialized!")
        }

    fun modules(modules: Module): RekvestlinBuilder {
        modules(listOf(modules))
        return this
    }

    fun modules(modules: List<Module>): RekvestlinBuilder {
        loadModules(modules)
        return this
    }

    fun configure(configurationAction: RekvestlinConfig.() -> Unit): RekvestlinBuilder {
        val configuration =
            RekvestlinConfig(rekvestlin.okHttpClientBuilder, rekvestlin.gsonBuilder)
        configuration.configurationAction()
        rekvestlin.applyConfiguration(configuration)
        return this
    }

    private fun loadModules(modules: List<Module>) {
        rekvestlin.loadModules(modules)
    }
}