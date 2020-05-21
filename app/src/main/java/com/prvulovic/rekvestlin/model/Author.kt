package com.prvulovic.rekvestlin.model

data class Author(
    val firstName: String,
    val lastName: String,
    val book: String
)

data class Book(val name: String)