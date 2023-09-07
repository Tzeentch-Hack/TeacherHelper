package com.tzeentch.teacherhelper.repository

import io.ktor.client.HttpClient

interface CameraRepository {
    fun sendPhoto()
}

class CameraRepositoryImpl constructor(private val httpClient: HttpClient): CameraRepository {
    override fun sendPhoto() {

    }
}
