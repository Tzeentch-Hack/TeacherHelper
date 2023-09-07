package com.tzeentch.teacherhelper.repository

import com.tzeentch.teacherhelper.dto.AuthResultDto
import com.tzeentch.teacherhelper.dto.RegDto
import com.tzeentch.teacherhelper.utils.NetworkResultState
import com.tzeentch.teacherhelper.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface AuthRepository {
    suspend fun registerUser(
        name: String,
        password: String
    ): Flow<NetworkResultState<AuthResultDto>>

    suspend fun loginUser(name: String, password: String): Flow<NetworkResultState<AuthResultDto>>
}

class AuthRepositoryImpl constructor(private val httpClient: HttpClient) : AuthRepository {
    override suspend fun registerUser(
        name: String,
        password: String
    ): Flow<NetworkResultState<AuthResultDto>> {
        return flowOf(
            safeApiCall {
                httpClient.post(urlString = "") {
                    url {
                        setBody(RegDto(name, password))
                    }
                }.body()
            }
        )
    }

    override suspend fun loginUser(
        name: String,
        password: String
    ): Flow<NetworkResultState<AuthResultDto>> {
        return flowOf(
            safeApiCall {
                httpClient.post(urlString = "") {
                    url {
                        setBody(RegDto(name, password))
                    }
                }.body()
            }
        )
    }
}