package com.tzeentch.teacherhelper.repository

import com.tzeentch.teacherhelper.dto.AuthResultDto
import com.tzeentch.teacherhelper.utils.Constants
import com.tzeentch.teacherhelper.utils.NetworkResultState
import com.tzeentch.teacherhelper.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface AuthRepository {
    suspend fun registerUser(
        ip: String,
        name: String,
        password: String
    ): Flow<NetworkResultState<AuthResultDto>>

    suspend fun loginUser(
        ip: String,
        name: String,
        password: String
    ): Flow<NetworkResultState<AuthResultDto>>
}

class AuthRepositoryImpl constructor(private val httpClient: HttpClient) : AuthRepository {
    override suspend fun registerUser(
        ip: String,
        name: String,
        password: String
    ): Flow<NetworkResultState<AuthResultDto>> {
        return flowOf(
            safeApiCall {
                httpClient.post(urlString = "http://$ip${Constants.REGISTRATION_URL}") {
                    setBody(
                        FormDataContent(Parameters.build {
                            append("username", name)
                            append("password", password)
                        })
                    )
                }.body()
            }
        )
    }

    override suspend fun loginUser(
        ip: String,
        name: String,
        password: String
    ): Flow<NetworkResultState<AuthResultDto>> {
        return flowOf(
            safeApiCall {
                httpClient.post(urlString = "http://$ip${Constants.LOGIN_URL}") {
                    setBody(
                        FormDataContent(Parameters.build {
                            append("username", name)
                            append("password", password)
                        })
                    )
                }.body()
            }
        )
    }
}