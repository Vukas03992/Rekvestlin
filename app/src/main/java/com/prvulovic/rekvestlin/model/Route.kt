package com.prvulovic.rekvestlin.model

import com.prvulovic.rekvestlin_core.parsing.JsonKey

data class Route(
    val id: Int,
    val origin: Airport,
    val destination: Airport,
    @JsonKey("is_active") val isActive: Boolean,
    @JsonKey("is_member") val isMember: Boolean
)

data class Airport(
    val id: Int,
    val code: String,
    val country: String?,
    val name: String
)