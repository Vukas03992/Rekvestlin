package com.prvulovic.rekvestlin_core.module

import com.prvulovic.rekvestlin_core.definition.GroupRequestDefinition
import com.prvulovic.rekvestlin_core.definition.RequestDefinition
import com.prvulovic.rekvestlin_dsl.RequestDefinitionDSL
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class Module {

    internal val groupRequestsMap = ConcurrentHashMap<String, GroupRequestDefinition>()
    val requestDefinitionMap = ConcurrentHashMap<String, RequestDefinition<*>>()

    fun groupRequests(name: String, groupDefinition: GroupRequestDefinition.() -> Unit): GroupRequestDefinition {
        val groupRequestDefinition = GroupRequestDefinition()
        groupRequestDefinition.groupDefinition()
        groupRequestsMap[name] = groupRequestDefinition
        return groupRequestDefinition
    }

    inline fun <reified T: Any> createRequest(
        name: String, modelClass: KClass<*> = T::class, noinline definition: RequestDefinitionDSL<T>.() -> Unit
    ): RequestDefinition<T> {
        val requestDefinitionDSL = RequestDefinitionDSL(T::class, modelClass)
        requestDefinitionDSL.definition()
        val requestDefinition = requestDefinitionDSL.create()
        requestDefinitionMap[name] = requestDefinition
        return requestDefinition
    }

    operator fun plus(module: Module): List<Module> = listOf(this, module)
}

operator fun List<Module>.plus(module: Module): List<Module> = this + listOf(module)