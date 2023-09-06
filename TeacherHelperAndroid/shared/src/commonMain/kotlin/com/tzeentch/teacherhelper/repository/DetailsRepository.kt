package com.tzeentch.teacherhelper.repository

import io.ktor.client.HttpClient

interface DetailsRepository {

}

class DetailsRepositoryImpl constructor(private val httpClient: HttpClient) : DetailsRepository {

}
