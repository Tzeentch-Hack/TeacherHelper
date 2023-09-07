package com.tzeentch.teacherhelper.utils

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

suspend fun <T : Any?> safeApiCall(apiCall: suspend () -> T): NetworkResultState<T> {
    return try {
        NetworkResultState.Loading
        val result = apiCall.invoke()
        NetworkResultState.Success(result)
    } catch (e: Exception) {
        NetworkResultState.Failure(e)
    }
}

