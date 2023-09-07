package com.tzeentch.teacherhelper.presenters

import com.tzeentch.teacherhelper.repository.AuthRepository
import com.tzeentch.teacherhelper.repository.DbRepository
import com.tzeentch.teacherhelper.utils.AuthUiState
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

class AuthPresenter constructor(
    private val authRepository: AuthRepository,
    private val dbRepository: DbRepository
) : KoinComponent {
    private val viewModelScope = CoroutineScope(Dispatchers.Default)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->

    }

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val authState = _authState.asStateFlow()

    init {
        val user = dbRepository.getUser()
        if (user.name.isNullOrEmpty() || user.password.isNullOrEmpty() || user.ip.isNullOrEmpty()) {
            _authState.value = AuthUiState.Init
        } else {
            loginUser(user.ip, user.name, user.password)
        }
    }

    fun loginUser(ip: String, name: String, password: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            authRepository.loginUser(ip, name, password).collect { result ->
                result.isLoading {
                    _authState.value = AuthUiState.Loading
                }.onSuccess { res ->
                    dbRepository.newUser(name, password, res.token, ip)
                    _authState.value = AuthUiState.ToOptionalScreen
                }.onFailure { error ->
                    _authState.value = AuthUiState.Init
                }
            }
        }
    }

    fun registerUser(ip: String, name: String, password: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            authRepository.registerUser(ip, name, password).collect { result ->
                result.isLoading {
                    _authState.value = AuthUiState.Loading
                }.onSuccess { res ->
                    dbRepository.newUser(name, password, res.token, ip)
                    _authState.value = AuthUiState.ToOptionalScreen
                }.onFailure { error ->
                    _authState.value = AuthUiState.Init
                }
            }
        }
    }
}