package com.prvulovic.rekvestlin_core

import okhttp3.MediaType.Companion.toMediaTypeOrNull

const val BaseUrl = "base_url"
const val UrlConstantExtension = "url_constant_extension"
const val Url = "url"
const val Method = "method"

const val GET = "get"
const val POST = "post"
const val PUT = "put"
const val DELETE = "delete"

const val STRING = "STRING"
const val INT = "Int"
const val LONG = "Long"
const val DOUBLE = "Double"
const val FLOAT = "Float"

val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
val MEDIA_TYPE_PNG = "image/png".toMediaTypeOrNull()
val MEDIA_TYPE_JPEG = "image/jpeg".toMediaTypeOrNull()
val MEDIA_TYPE_WEBP = "image/webp".toMediaTypeOrNull()