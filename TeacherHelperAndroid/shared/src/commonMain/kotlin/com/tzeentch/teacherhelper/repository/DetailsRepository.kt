package com.tzeentch.teacherhelper.repository

import com.tzeentch.teacherhelper.dto.DetailsDto
import com.tzeentch.teacherhelper.utils.Constants
import com.tzeentch.teacherhelper.utils.NetworkResultState
import com.tzeentch.teacherhelper.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface DetailsRepository {
    suspend fun getRequests(
        token: String,
        id: String,
        ip: String
    ): Flow<NetworkResultState<DetailsDto>>
}

class DetailsRepositoryImpl constructor(private val httpClient: HttpClient) : DetailsRepository {
    override suspend fun getRequests(
        token: String,
        id: String,
        ip: String
    ): Flow<NetworkResultState<DetailsDto>> {
        return flowOf(
            safeApiCall {
                httpClient.get(urlString = "http://${ip}${Constants.GET_REQUEST}") {
                    header("Authorization", "Bearer $token")
                    parameter("request_id", id)
                }.body()
            })
    }
}
