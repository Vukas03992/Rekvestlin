package com.prvulovic.rekvestlin_core

import com.prvulovic.rekvestlin_core.context.GlobalContext
import com.prvulovic.rekvestlin_core.instance.RequestInstance

interface RekvestlinComponent{
   fun getRekvestlin(): Rekvestlin = GlobalContext.getRekvestlin()
}

inline fun <reified T: Any> RekvestlinComponent.get(name: String): RequestInstance<T>{
   return getRekvestlin().get(name)
}

inline fun <reified T: Any> RekvestlinComponent.inject(name: String): Lazy<RequestInstance<T>>{
   return getRekvestlin().inject(name)
}