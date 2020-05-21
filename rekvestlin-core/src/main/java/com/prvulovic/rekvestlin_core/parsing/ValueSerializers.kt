package com.prvulovic.rekvestlin_core.parsing

import java.lang.reflect.Type

fun serializerForBasicType(type: Type): ValueSerializer<out Any?>{
    assert(type.isPrimitiveOrString()){"Expected primitive type or String: ${type}"}
    return serializerForType(type)!!
}

fun Type.isPrimitiveOrString(): Boolean {
    val cls = this as? Class<Any> ?: return false
    return cls.kotlin.javaPrimitiveType != null || cls == String::class.java
}

fun serializerForType(type: Type): ValueSerializer<out Any?>? =
    when(type){
        Byte::class.java, Byte::class.javaObjectType -> ByteSerialiser
        Short::class.java, Short::class.javaObjectType -> ShortSerializer
        Int::class.java, Int::class.javaObjectType -> IntSerializer
        Long::class.java, Long::class.javaObjectType -> LongSerializer
        Float::class.java, Float::class.javaObjectType -> FloatSerializer
        Double::class.java, Double::class.javaObjectType -> DoubleSerializer
        Boolean::class.java, Boolean::class.javaObjectType -> BooleanSerializer
        String::class.java, String::class.javaObjectType -> StringSerializer
        else -> null
    }

private fun Any?.expectNumber(): Number {
    if (this !is Number) throw Exception("Expected number, was: $this")
    return this
}

object ByteSerialiser: ValueSerializer<Byte>{
    override fun toJson(value: Byte): Any? = value
    override fun fromJson(json: Any?): Byte = json.expectNumber().toByte()
}

object ShortSerializer: ValueSerializer<Short>{
    override fun toJson(value: Short): Any? = value
    override fun fromJson(json: Any?): Short = json.expectNumber().toShort()
}

object IntSerializer: ValueSerializer<Int>{
    override fun toJson(value: Int): Any? = value
    override fun fromJson(json: Any?): Int = json.expectNumber().toInt()
}

object LongSerializer: ValueSerializer<Long>{
    override fun toJson(value: Long): Any? = value
    override fun fromJson(json: Any?): Long = json.expectNumber().toLong()
}

object FloatSerializer: ValueSerializer<Float>{
    override fun toJson(value: Float): Any? = value
    override fun fromJson(json: Any?): Float = json.expectNumber().toFloat()
}

object DoubleSerializer: ValueSerializer<Double>{
    override fun toJson(value: Double): Any? = value
    override fun fromJson(json: Any?): Double = json.expectNumber().toDouble()
}

object BooleanSerializer: ValueSerializer<Boolean>{
    override fun toJson(value: Boolean): Any? = value
    override fun fromJson(json: Any?): Boolean {
        if (json !is Boolean) throw Exception("Expected boolean, was: $json")
        return json
    }
}

object StringSerializer: ValueSerializer<String?>{
    override fun toJson(value: String?): Any? = value
    override fun fromJson(json: Any?): String? {
        if (json !is String?) throw Exception("Expected string, was: $json")
        return json
    }
}