package com.prvulovic.rekvestlin_core.parsing

import java.io.Reader

class Parser(reader: Reader, val rootObject: JsonObject) {
    private val lexer = Lexer(reader)

    fun parse() {
        val token = lexer.peek()!!
        if (token == Token.LBRACE) {
            expect(Token.LBRACE)
            parseObjectBody(rootObject)
            require(lexer.nextToken() == null) { "Too many tokens" }
        } else {
            expect(Token.LBRACKET)
            parseArrayBody(rootObject)
        }
    }

    private fun parseObjectBody(jsonObject: JsonObject) {
        parseCommaSeparated(Token.RBRACE) {
            if (it !is Token.StringValue) {
                throw MalformedJSONException("Unexpected token $it")
            }
            val propName = it.value
            expect(Token.COLON)
            if (jsonObject.checkForProperty(it.value)) {
                parsePropertyValue(jsonObject, propName, nextToken())
            }else{
                val token = nextToken()
                when(token){
                    is Token.ValueToken -> {
                        nextToken()
                    }
                    Token.COLON -> {
                        nextToken()
                    }
                    Token.LBRACE -> {
                        skipObject()
                    }
                    Token.LBRACKET -> {
                        skipArray()
                    }
                }
            }
        }
    }

    private fun skipObject(){
        loop@ do{
            val token = nextToken()
            when(token){
                is Token.ValueToken -> { continue@loop }
                Token.COLON -> continue@loop
                Token.LBRACE -> skipObject()
                Token.LBRACKET -> skipArray()
            }
        }while (token != Token.RBRACE)
    }

    private fun skipArray(){
        loop@ do{
            val token = nextToken()
            when(token){
                is Token.ValueToken -> continue@loop
                Token.COLON -> continue@loop
                Token.COMMA -> continue@loop
                Token.LBRACE -> skipObject()
                Token.LBRACKET -> skipArray()
            }
        }while (token != Token.RBRACKET)
    }

    private fun parseArrayBody(currentObject: JsonObject, propName: String) {
        parseCommaSeparated(Token.RBRACKET) {
            parsePropertyValue(currentObject, propName, it)
        }
    }

    private fun parseArrayBody(currentObject: JsonObject) {
        parseCommaSeparated(Token.RBRACKET) {
            when (it) {
                is Token.LBRACE -> {
                    val childObject = currentObject.createObject()
                    if (childObject != null) {
                        parseObjectBody(childObject)
                    }
                }
            }
        }
    }

    private fun parseCommaSeparated(stopToken: Token, body: (Token) -> Unit) {
        var expectComma = false
        while (true) {
            var token = nextToken()
            if (token == stopToken) return
            if (expectComma) {
                if (token != Token.COMMA) throw MalformedJSONException("Expected comma")
                token = nextToken()
            }
            body(token)
            expectComma = true
        }
    }

    private fun parsePropertyValue(currentObject: JsonObject, propName: String, token: Token) {
        when (token) {
            is Token.ValueToken -> currentObject.setSimpleProperty(propName, token.value)
            Token.LBRACE -> {
                val childObject = currentObject.createObject(propName)
                if (childObject != null)
                    parseObjectBody(childObject)
            }
            Token.LBRACKET -> {
                val childObject = currentObject.createArray(propName)
                if (childObject != null)
                    parseArrayBody(childObject, propName)
            }
            else -> throw MalformedJSONException("Unexpected token $token")
        }
    }

    private fun expect(token: Token) {
        require(lexer.nextToken() == token) { "$token expected" }
    }

    private fun nextToken(): Token = lexer.nextToken() ?: throw IllegalArgumentException("Premature end of data")
}