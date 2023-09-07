package com.tzeentch.teacherhelper.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainDto(
    @SerialName("requests_short_data") val requestList: List<RequestDto>
)
