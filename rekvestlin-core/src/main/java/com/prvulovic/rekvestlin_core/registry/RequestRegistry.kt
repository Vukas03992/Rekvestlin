package com.prvulovic.rekvestlin_core.registry

import com.prvulovic.rekvestlin_core.definition.GroupRequestDefinition
import com.prvulovic.rekvestlin_core.definition.RequestDefinition
import com.prvulovic.rekvestlin_core.instance.RequestInstance
import com.prvulovic.rekvestlin_core.module.Module
import java.util.concurrent.ConcurrentHashMap

class RequestRegistry {

    private val requestDefinitionsWithNames = ConcurrentHashMap<String, RequestDefinition<*>>()
    private val groupDefinitionsWithNames = ConcurrentHashMap<String, GroupRequestDefinition>()

    fun loadModules(modules: List<Module>) {
        modules.forEach {
            requestDefinitionsWithNames.putAll(it.requestDefinitionMap)
            groupDefinitionsWithNames.putAll(it.groupRequestsMap)
        }
    }

    fun <T: Any> findByName(name: String): RequestInstance<T> {
        val requestDefinition = requestDefinitionsWithNames[name]
        return requestDefinition?.getRequestInstance() as RequestInstance<T>
    }

    fun <T: Any> injectByName(name: String): Lazy<RequestInstance<T>> = lazy {
        findByName<T>(name)
    }

    fun getGroupByName(groupName: String): GroupRequestDefinition {
        return groupDefinitionsWithNames[groupName]!!
    }
}