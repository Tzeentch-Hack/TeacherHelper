package com.tzeentch.teacherhelper.repository

import io.ktor.client.HttpClient

interface LoginRepository {

}

class LoginRepositoryImpl constructor(private val httpClient: HttpClient) : LoginRepository {

}