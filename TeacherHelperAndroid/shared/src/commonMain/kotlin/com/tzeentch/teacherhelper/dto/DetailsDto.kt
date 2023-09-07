package com.tzeentch.teacherhelper.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetailsDto(
    @SerialName("username") val userName: String,
    @SerialName("request_id") val requestId: String,
    @SerialName("status") val status: String
)