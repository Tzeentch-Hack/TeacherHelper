package com.tzeentch.teacherhelper.repository

import com.tzeentch.teacherhelper.dto.PhotoUploadResult
import com.tzeentch.teacherhelper.utils.Constants
import com.tzeentch.teacherhelper.utils.NetworkResultState
import com.tzeentch.teacherhelper.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface CameraRepository {
    suspend fun sendPhoto(ip: String, token: String, files: List<ByteArray>):Flow<NetworkResultState<PhotoUploadResult>>
}

class CameraRepositoryImpl constructor(private val httpClient: HttpClient) : CameraRepository {
    override suspend fun sendPhoto(
        ip: String,
        token: String,
        files: List<ByteArray>
    ): Flow<NetworkResultState<PhotoUploadResult>> {
        return flowOf(
            safeApiCall {
            httpClient.post(urlString = "http://$ip${Constants.PHOTO_UPLOAD}") {
                header("Authorization", "Bearer $token")
                setBody(MultiPartFormDataContent(
                    formData {
                        for (i in files.indices) {
                            append("files", files[i], Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=image$i.jpg")
                            })
                        }
                    }
                ))
            }.body()
        })
    }
}