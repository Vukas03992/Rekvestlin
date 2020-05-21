package com.prvulovic.rekvestlin_core.parsing

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
annotation class JsonExclude

@Target(AnnotationTarget.PROPERTY)
annotation class JsonKey(val key: String)

interface ValueSerializer<T>{
    fun toJson(value: T): Any?
    fun fromJson(json: Any?): T
}

@Target(AnnotationTarget.PROPERTY)
annotation class DeserializeInterface(val targetClass: KClass<out Any>)

@Target(AnnotationTarget.PROPERTY)
annotation class SerializationStrategy(val serializerClass: KClass<out ValueSerializer<*>>)