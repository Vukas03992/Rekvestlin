package com.prvulovic.rekvestlin_dsl

import com.google.gson.Gson
import com.prvulovic.rekvestlin_core.*
import kotlin.reflect.KClass

class OnSuccessBuilder<T>(val modelClass: KClass<*>){

    lateinit var parsingElement: ParsingElement<T>
    internal var doOnBeforeParsing: (Gson.() -> Unit)? = null
    internal var doOnAfterParsing: (T.() -> Unit)? = null

    fun doOnBeforeParsing(action: Gson.() -> Unit){
        doOnBeforeParsing = action
    }

    fun doOnAfterParsing(action: T.() -> Unit){
        doOnAfterParsing = action
    }

    infix fun PARSE.AS(element: Element): Element{
        parsingElement = ParsingElement<T>(modelClass).apply {
            root = true
            array = when(element){
                ARRAY -> true
                OBJECT -> false
            }
            withDeserializer = true
        }
        return element
    }

    infix fun Element.and(action: ParsingElement<T>.() -> Unit) {
        parsingElement.apply(action)
    }
}

object PARSE
object ARRAY: Element()
object OBJECT: Element()
sealed class Element

class ParsingElement<T>(val modelClass: KClass<*>){

    var withDeserializer = false
    var key: String = ""
    var array: Boolean = false
    var nestedParsingElement: ParsingElement<T>? = null
    var root: Boolean = false
    val parsingPairs = mutableMapOf<String, String>()
    var parseAsType: String? = null

    infix fun String.then(action: ParsingElement<T>.() -> Unit){
        val keyObject = this
        val parsingElement = ParsingElement<T>(modelClass).apply {
            key = keyObject
            root = false
        }
        parsingElement.action()
        nestedParsingElement = parsingElement
    }

    infix fun String.AS(type: String){
        when(type){
            STRING, INT, FLOAT, DOUBLE, LONG -> {}
            else -> throw java.lang.IllegalArgumentException("fjkdjf")
        }
        val keyObject = this
        val parsingElement = ParsingElement<T>(modelClass).apply {
            key = keyObject
            root = false
            parseAsType = type
        }
        nestedParsingElement = parsingElement
    }

    infix fun PARSE.AS(element: Element): Element{
        array = when(element){
            ARRAY -> true
            OBJECT -> false
        }
        withDeserializer = true
        return element
    }

    infix fun Element.and(action: ParsingElement<T>.() -> Unit) {
        this@ParsingElement.action()
    }

    fun take(key: String): String = key

    fun PARSE(key: String): String = key
}
