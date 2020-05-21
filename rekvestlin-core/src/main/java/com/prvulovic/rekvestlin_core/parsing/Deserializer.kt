package com.prvulovic.rekvestlin_core.parsing

import android.util.Log.e
import java.io.Reader
import java.io.StringReader
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

inline fun <reified T: Any> deserialize(json: String): T{
    return deserialize(StringReader(json))
}

inline fun <reified T: Any> deserialize(json: Reader): T{
    return deserialize(json, T::class, T::class)
}

fun <T: Any> deserialize(json: Reader, targetClass: KClass<T>, typeArgumentClass: KClass<*>): T{
    val seed = if (List::class.java.isAssignableFrom(targetClass.java)){
        ObjectListSeed(typeArgumentClass.java as Type, ClassInfoCache())
    }else{
        ObjectSeed(targetClass, ClassInfoCache())
    }
    Parser(json, seed).parse()
    return seed.spawn() as T
}

interface JsonObject{
    fun checkForProperty(propertyName: String): Boolean = false
    fun setSimpleProperty(propertyName: String, value: Any?)
    fun createObject(propertyName: String = ""): JsonObject?
    fun createArray(propertyName: String = ""): JsonObject?
}

interface Seed: JsonObject{
    val classInfoCache: ClassInfoCache
    fun spawn(): Any?
    fun createCompositeProperty(propertyName: String = "", isList: Boolean): JsonObject?
    override fun createObject(propertyName: String): JsonObject? = createCompositeProperty(propertyName, false)
    override fun createArray(propertyName: String): JsonObject? = createCompositeProperty(propertyName, true)
}

fun Seed.createSeedForType(paramType: Type, isList: Boolean): Seed {
    val paramClass = paramType.asJavaClass()
    if (List::class.java.isAssignableFrom(paramClass)){
        if (!isList) throw Exception("An array expected, not a composite object")
        val parameterizedType = paramType as? ParameterizedType ?: throw UnsupportedOperationException("Unsupported parameter type $this")
        val elementType = parameterizedType.actualTypeArguments.single()
        if (elementType.isPrimitiveOrString()){
            return ValueListSeed(elementType, classInfoCache)
        }
        return ObjectListSeed(elementType, classInfoCache)
    }
    if(isList) throw Exception("Object of the type ${paramType} expected, not an array")
    return ObjectSeed(paramClass.kotlin, classInfoCache)
}

fun Type.asJavaClass(): Class<Any> = when (this) {
    is Class<*> -> this as Class<Any>
    is ParameterizedType -> rawType as? Class<Any>
        ?: throw UnsupportedOperationException("Unknown type $this")
    else -> throw UnsupportedOperationException("Unknown type $this")
}

class ObjectSeed<out T: Any>(targetClass: KClass<T>, override val classInfoCache: ClassInfoCache): Seed{

    private val classInfo: ClassInfo<T> = classInfoCache[targetClass]

    private val valueArguments = mutableMapOf<KParameter, Any?>()
    private val seedArguments = mutableMapOf<KParameter, Seed>()

    private val arguments: Map<KParameter, Any?>
    get() = valueArguments + seedArguments.mapValues { it.value.spawn() }

    override fun setSimpleProperty(propertyName: String, value: Any?) {
        val param = classInfo.getConstructorParameter(propertyName)
        if (param != null)
        valueArguments[param] = classInfo.deserialiConstructorArgument(param, value)
    }

    override fun spawn(): T = classInfo.createInstance(arguments)

    override fun createCompositeProperty(propertyName: String, isList: Boolean): JsonObject? {
        val param = classInfo.getConstructorParameter(propertyName)
        if (param != null) {
            val deserializeAs = classInfo.getDeserializeClass(propertyName)
            val seed = createSeedForType(deserializeAs ?: param.type.javaType, isList)
            return seed.apply { seedArguments[param] = this }
        }
        return null
    }

    override fun checkForProperty(propertyName: String) = classInfo.getConstructorParameter(propertyName) != null
}

class ObjectListSeed(val elementType: Type, override val classInfoCache: ClassInfoCache): Seed {

    private val elements = mutableListOf<Seed>()

    override fun setSimpleProperty(propertyName: String, value: Any?) {
        throw Exception("Found primitive value in collection of object types")
    }

    override fun spawn(): List<*> = elements.map { it.spawn() }

    override fun createCompositeProperty(propertyName: String, isList: Boolean) = createSeedForType(elementType, isList).apply { elements.add(this) }
}

class ValueListSeed(elementType: Type, override val classInfoCache: ClassInfoCache): Seed{

    private val elements = mutableListOf<Any?>()
    private val serializerForType = serializerForBasicType(elementType)

    override fun setSimpleProperty(propertyName: String, value: Any?) {
        elements.add(serializerForType.fromJson(value))
    }

    override fun spawn(): Any? = elements

    override fun createCompositeProperty(propertyName: String, isList: Boolean): JsonObject {
        throw Exception("Found object value in collection of primitive types")
    }

}