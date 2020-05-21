package com.prvulovic.rekvestlin_android

import android.content.ComponentCallbacks
import com.prvulovic.rekvestlin_core.context.GlobalContext
import com.prvulovic.rekvestlin_core.instance.RequestInstance

fun ComponentCallbacks.getRekvestlin() = GlobalContext.getRekvestlin()

inline fun <reified T: Any> ComponentCallbacks.get(name: String): RequestInstance<T> = getRekvestlin().get(name)

inline fun <reified T: Any> ComponentCallbacks.inject(name: String): Lazy<RequestInstance<T>> = getRekvestlin().inject(name)