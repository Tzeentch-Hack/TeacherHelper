package com.tzeentch.teacherhelper.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResultDto(
    @SerialName("access_token") val token: String
)
