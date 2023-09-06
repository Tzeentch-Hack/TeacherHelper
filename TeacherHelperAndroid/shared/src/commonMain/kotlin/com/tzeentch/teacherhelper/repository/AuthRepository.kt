package com.tzeentch.teacherhelper.repository

import io.ktor.client.HttpClient

interface AuthRepository {

}

class AuthRepositoryImpl constructor(private val httpClient: HttpClient) : AuthRepository {
}