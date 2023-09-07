package com.tzeentch.teacherhelper.presenters

import com.tzeentch.teacherhelper.repository.DbRepository
import com.tzeentch.teacherhelper.repository.DetailsRepository
import com.tzeentch.teacherhelper.utils.DetailsUiState
import com.tzeentch.teacherhelper.utils.MainUiState
import com.tzeentch.teacherhelper.utils.isLoading
import com.tzeentch.teacherhelper.utils.onFailure
import com.tzeentch.teacherhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class DetailsPresenter constructor(
    private val repository: DetailsRepository,
    private val dbRepository: DbRepository
) : KoinComponent {

    private var ip: String = ""
    private var token: String = ""

    private val _detailsState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val detailsState = _detailsState.asStateFlow()

    private val viewModelScope = CoroutineScope(Dispatchers.Default)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        _detailsState.value = DetailsUiState.Loading
    }

    init {
        val user = dbRepository.getUser()
        ip = user.ip ?: ""
        token = user.token ?: ""
    }

    fun getRequests(id: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.getRequests(token = token, id = id,ip).collect { result ->
                result.isLoading {
                    _detailsState.value = DetailsUiState.Loading
                }.onSuccess {
                    _detailsState.value = DetailsUiState.ReceiveTask(detailsDto = it)
                }.onFailure {
                    _detailsState.value = DetailsUiState.Error(it.message.toString())
                }
            }
        }
    }
}