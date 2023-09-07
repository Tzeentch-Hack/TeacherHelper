package com.tzeentch.teacherhelper.repository

import com.tzeentch.teacherhelper.dto.DetailsDto
import com.tzeentch.teacherhelper.utils.NetworkResultState
import com.tzeentch.teacherhelper.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface DetailsRepository {
    suspend fun getRequests(token: String, id: String) : Flow<NetworkResultState<DetailsDto>>
}

class DetailsRepositoryImpl constructor(private val httpClient: HttpClient) : DetailsRepository {
    override suspend fun getRequests(token: String, id: String): Flow<NetworkResultState<DetailsDto>> {
        return flowOf(
            safeApiCall {
                httpClient.post(urlString = "http://${id}") {
                    header("Authorization", "Bearer $token")
                }.body()
            })
    }
}
