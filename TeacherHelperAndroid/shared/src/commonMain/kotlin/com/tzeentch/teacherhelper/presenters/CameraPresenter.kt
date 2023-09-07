package com.tzeentch.teacherhelper.presenters

import com.tzeentch.teacherhelper.repository.CameraRepository
import com.tzeentch.teacherhelper.repository.DbRepository
import com.tzeentch.teacherhelper.utils.AuthUiState
import com.tzeentch.teacherhelper.utils.CameraUiState
import com.tzeentch.teacherhelper.utils.isLoading
import com.tzeentch.teacherhelper.utils.onFailure
import com.tzeentch.teacherhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class CameraPresenter constructor(
    private val repository: CameraRepository,
    private val dbRepository: DbRepository
) : KoinComponent {
    private val viewModelScope = CoroutineScope(Dispatchers.Default)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->

    }

    private val _cameraState = MutableStateFlow<CameraUiState>(CameraUiState.Initial)
    val cameraState = _cameraState.asStateFlow()

    fun sendPhoto(files: List<ByteArray>) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val user = dbRepository.getUser()
            if (user.ip != null && user.token != null) {
                repository.sendPhoto(ip = user.ip, token = user.token, files).collect { result ->
                    result.onFailure {

                    }.onSuccess {
                        _cameraState.value = CameraUiState.GotoOptionalScreen
                    }.isLoading {
                        _cameraState.value = CameraUiState.Loading
                    }
                }
            }
        }
    }

}