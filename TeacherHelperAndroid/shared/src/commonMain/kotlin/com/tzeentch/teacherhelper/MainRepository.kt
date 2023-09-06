package com.tzeentch.teacherhelper

import io.ktor.client.HttpClient

interface MainRepository {
    
}

class MainRepositoryImpl constructor(private val httpClient: HttpClient) : MainRepository {

}