package com.tzeentch.teacherhelper.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestDto(
    @SerialName("request_id") val id: String,
    @SerialName("status") val status: String
)
