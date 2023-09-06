package com.tzeentch.teacherhelper.repository

import io.ktor.client.HttpClient

interface CameraRepository {
    fun sendText()
}

class CameraRepositoryImpl constructor(private val httpClient: HttpClient): CameraRepository {
    override fun sendText() {

    }
}
