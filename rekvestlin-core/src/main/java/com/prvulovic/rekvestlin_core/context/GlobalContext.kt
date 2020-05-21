package com.prvulovic.rekvestlin_core.context

import com.prvulovic.rekvestlin_core.Rekvestlin
import com.prvulovic.rekvestlin_core.RekvestlinBuilder

object GlobalContext {
    internal var rekvestlinBuilder: RekvestlinBuilder? = null
    internal fun getRekvestlin(): Rekvestlin {
        if (rekvestlinBuilder != null){
            return rekvestlinBuilder!!.instance
        } else throw IllegalStateException("Rekvestlin is not initialized!")
    }
}

fun startRekvestlin(init: RekvestlinBuilder.() -> Unit) {
    val rekvestlinBuilder = RekvestlinBuilder()
    GlobalContext.rekvestlinBuilder = rekvestlinBuilder
    rekvestlinBuilder.init()
}

fun configureRekvestlin(init: RekvestlinBuilder.() -> Unit): () -> Unit = {
    val rekvestlinBuilder = RekvestlinBuilder()
    GlobalContext.rekvestlinBuilder = rekvestlinBuilder
    rekvestlinBuilder.init()
}