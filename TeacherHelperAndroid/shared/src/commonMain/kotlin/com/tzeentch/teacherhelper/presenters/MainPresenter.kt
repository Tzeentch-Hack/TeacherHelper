package com.tzeentch.teacherhelper.presenters

import com.tzeentch.teacherhelper.dto.RequestDto
import com.tzeentch.teacherhelper.repository.DbRepository
import com.tzeentch.teacherhelper.repository.MainRepository
import com.tzeentch.teacherhelper.utils.CameraUiState
import com.tzeentch.teacherhelper.utils.MainUiState
import com.tzeentch.teacherhelper.utils.isLoading
import com.tzeentch.teacherhelper.utils.onFailure
import com.tzeentch.teacherhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

class MainPresenter constructor(
    private val repository: MainRepository,
    private val dbRepository: DbRepository
) : KoinComponent {

    private var ip: String = ""
    private var token: String = ""

    private lateinit var job: Job

    private val _mainState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val mainState = _mainState.asStateFlow()

    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        _mainState.value = MainUiState.Loading
    }

    init {
        val user = dbRepository.getUser()
        ip = user.ip ?: ""
        token = user.token ?: ""
        getAllJobs()
    }


    private fun getAllJobs() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.getAllJobs(ip, token).collect { result ->
                result.isLoading {
                    _mainState.value = MainUiState.Loading
                }.onSuccess {
                    for (request in it.requestList) {
                        if (request.status != "Succeeded") {
                            startRepeatingJob(6000)
                            break
                        }
                    }
                    _mainState.value = MainUiState.ReceiveListOfTask(it.requestList)
                }.onFailure {
                    _mainState.value = MainUiState.Error(it.message.toString())
                }
            }
        }
    }

    private fun startRepeatingJob(timeInterval: Long) {
        viewModelScope.launch {
            while (true) {
                delay(timeInterval)
                updateAllJobs()
            }
        }
    }

    private fun updateAllJobs() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.getAllJobs(ip, token).collect { result ->
                result.onSuccess {
                    _mainState.value = MainUiState.ReceiveListOfTask(it.requestList)
                }
            }
        }
    }
}