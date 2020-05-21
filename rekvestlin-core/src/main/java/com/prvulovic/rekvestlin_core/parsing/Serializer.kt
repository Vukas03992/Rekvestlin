package com.prvulovic.rekvestlin_core.parsing

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

fun serialize(any: Any): String = buildString { serializeObject(any) }

private fun StringBuilder.serializeObjectWithoutAnnotation(any: Any){
    val kClass = any.javaClass.kotlin
    val properties = kClass.memberProperties

    append("{")
    properties.forEachIndexed { index, it ->
        serializeString(it.name)
        append(": ")
        serializePropertyValue(it.get(any))
        if (index < properties.size-1) append(",")
    }
    append("}")
}

private fun StringBuilder.serializeObject(any: Any){
    val propertiesToSerialize = any.javaClass.kotlin.memberProperties
        .filter { it.findAnnotation<JsonExclude>() == null }
    append("{")
    propertiesToSerialize.forEachIndexed { index, it ->
        serializeProperty(it, any)
        if (index < propertiesToSerialize.size-1) append(",")
    }
    append("}")
}

private fun StringBuilder.serializeProperty(property: KProperty1<Any, *>, any: Any){
    val jsonKeyAnnotation = property.findAnnotation<JsonKey>()
    val propertyName = jsonKeyAnnotation?.key ?: property.name
    serializeString(propertyName)
    append(": ")
    val value = property.get(any)
    val jsonValue = property.getSerializer()?.toJson(value) ?: value
    serializePropertyValue(jsonValue)
}

fun KProperty<*>.getSerializer(): ValueSerializer<Any?>? {
    val customSerializerAnnotation = findAnnotation<SerializationStrategy>() ?: return null
    val serializerClass = customSerializerAnnotation.serializerClass
    val valueSerializer = serializerClass.objectInstance ?: serializerClass.createInstance()
    @Suppress("UNCHECKED_CAST")
    return valueSerializer as ValueSerializer<Any?>
}

private fun StringBuilder.serializePropertyValue(value: Any?){
    when(value){
        null -> append("null")
        is String -> serializeString(value)
        is Number, is Boolean -> append(value.toString())
        is List<*> -> serializeList(value)
        else -> serializeObjectWithoutAnnotation(value)
    }
}

private fun StringBuilder.serializeList(data: List<Any?>){
    append("[")
    data.forEachIndexed { index, any ->
        serializePropertyValue(any)
        if (index < data.size-1) append(",")
    }
    append("]")
}

private fun StringBuilder.serializeString(string: String){
    append('\"')
    string.forEach { append(it.escape()) }
    append('\"')
}

private fun Char.escape():Any =
    when(this){
        '\\' -> "\\\\"
        '\"' -> "\\\""
        '\b' -> "\\b"
        '\u000C' -> "\\f"
        '\n' -> "\\n"
        '\r' -> "\\r"
        '\t' -> "\\t"
        else -> this
    }