package com.prvulovic.rekvestlin_dsl

import com.prvulovic.rekvestlin_core.module.Module

fun requests(action: Module.() -> Unit): Module {
    val module = Module()
    module.action()
    return module
}