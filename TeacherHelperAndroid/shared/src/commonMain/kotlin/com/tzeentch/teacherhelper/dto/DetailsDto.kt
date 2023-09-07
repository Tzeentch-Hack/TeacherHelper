package com.tzeentch.teacherhelper.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetailsDto(
    @SerialName("username") val userName: String,
    @SerialName("request_id") val requestId: String,
    @SerialName("task_id") val taskId: String,
    @SerialName("status") val status: String,
    @SerialName("pptx_url") val pttxUrl: String,
    @SerialName("images_url") val images: List<String>,
    @SerialName("short_text") val shortText: String,
    @SerialName("teaching_recommendations") val teachingRecommendations: List<String>,
    @SerialName("lesson_estimates") val lessonEstimates: List<String>,
    @SerialName("possible_questions") val possibleQuestions: List<String>,
)