package com.tzeentch.teacherhelper.repository

import com.tzeentch.teacherhelper.dto.MainDto
import com.tzeentch.teacherhelper.utils.Constants
import com.tzeentch.teacherhelper.utils.NetworkResultState
import com.tzeentch.teacherhelper.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface MainRepository {
    suspend fun getAllJobs(ip: String, token: String): Flow<NetworkResultState<MainDto>>
}

class MainRepositoryImpl constructor(private val httpClient: HttpClient) : MainRepository {
    override suspend fun getAllJobs(ip: String, token: String): Flow<NetworkResultState<MainDto>> {
        return flowOf(
            safeApiCall {
                httpClient.post(urlString = "http://$ip${Constants.PHOTO_UPLOAD}") {
                    header("Authorization", "Bearer $token")
                }.body()
            })
    }

}