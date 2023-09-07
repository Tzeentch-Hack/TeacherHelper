package com.tzeentch.teacherhelper.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegDto(
    @SerialName("username") val name: String,
    @SerialName("password") val password: String
)
