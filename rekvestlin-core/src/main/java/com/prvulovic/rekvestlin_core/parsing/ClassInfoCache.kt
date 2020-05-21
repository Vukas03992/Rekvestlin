package com.prvulovic.rekvestlin_core.parsing

import java.lang.Exception
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType

class ClassInfoCache {
    private val cacheData = mutableMapOf<KClass<*>, ClassInfo<*>>()

    @Suppress("UNCHECKED_CAST")
    operator fun <T: Any> get(kClass: KClass<T>): ClassInfo<T> =
        cacheData.getOrPut(kClass){ClassInfo(kClass)} as ClassInfo<T>
}

class ClassInfo<T: Any>(kClass: KClass<T>){
    val className = kClass.qualifiedName
    private val constructor = kClass.primaryConstructor ?: throw Exception("Class ${kClass.qualifiedName} doesn't have a primary constructor")

    private val jsonKeyToParameterMap = hashMapOf<String, KParameter>()
    private val parameterToSerializerMap = hashMapOf<KParameter, ValueSerializer<out Any?>>()
    private val jsonKeyToDeserializeClassMap = hashMapOf<String, Class<out Any>?>()

    init {
        constructor.parameters.forEach { cacheDataForParameter(kClass, it) }
    }

    private fun cacheDataForParameter(kClass: KClass<*>, parameter: KParameter){
        val parameterName = parameter.name ?: throw Exception("Class ${kClass.qualifiedName} has constructor parameter without name")
        val property = kClass.declaredMemberProperties.find { it.name == parameterName } ?: return
        val name = property.findAnnotation<JsonKey>()?.key ?: parameterName
        jsonKeyToParameterMap[name] = parameter

        val deserializeClass = property.findAnnotation<DeserializeInterface>()?.targetClass?.java
        jsonKeyToDeserializeClassMap[name] = deserializeClass

        val valueSerializer = property.getSerializer() ?: serializerForType(parameter.type.javaType) ?: return
        parameterToSerializerMap[parameter] = valueSerializer
    }

    fun getConstructorParameter(propertyName: String): KParameter? = jsonKeyToParameterMap[propertyName]

    fun getDeserializeClass(propertyName: String) = jsonKeyToDeserializeClassMap[propertyName]

    fun deserialiConstructorArgument(parameter: KParameter, value: Any?): Any? {
        val serializer = parameterToSerializerMap[parameter]
        if (serializer != null) return serializer.fromJson(value)
        validateArgumentType(parameter, value)
        return value
    }

    fun createInstance(arguments: Map<KParameter, Any?>): T{
        ensureAllParametersPresent(arguments)
        return constructor.callBy(arguments)
    }

    private fun ensureAllParametersPresent(arguments: Map<KParameter, Any?>){
        constructor.parameters.forEach {
            if (arguments[it] == null && !it.isOptional && !it.type.isMarkedNullable){
                throw Exception("Missing value for parameter ${it.name}")
            }
        }
    }

    private fun validateArgumentType(parameter: KParameter, value: Any?){
        if (value == null && !parameter.type.isMarkedNullable){
            throw Exception("Received null value for non-null parameter ${parameter.name}")
        }
        if (value != null && value.javaClass != parameter.type.javaType){
            throw Exception("Type mismatch for parameter ${parameter.name}: expected ${parameter.type.javaType}, found ${value.javaClass}")
        }
    }
}