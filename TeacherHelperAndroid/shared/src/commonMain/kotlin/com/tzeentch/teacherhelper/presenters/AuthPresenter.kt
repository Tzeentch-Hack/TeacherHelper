package com.tzeentch.teacherhelper.presenters

import com.tzeentch.teacherhelper.repository.AuthRepository
import com.tzeentch.teacherhelper.repository.DbRepository
import com.tzeentch.teacherhelper.utils.isLoading
import com.tzeentch.teacherhelper.utils.onFailure
import com.tzeentch.teacherhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class AuthPresenter constructor(
    private val authRepository: AuthRepository,
    private val dbRepository: DbRepository
) : KoinComponent {
    private val viewModelScope = CoroutineScope(Dispatchers.Default)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->

    }

    init {
        val user = dbRepository.getUser()
        if (user.name.isNullOrEmpty() || user.password.isNullOrEmpty() || user.ip.isNullOrEmpty()) {

        } else {
            loginUser(user.name, user.password, user.ip)
        }
    }

    fun loginUser(ip: String, name: String, password: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            authRepository.loginUser(ip, name, password).collect { result ->
                result.isLoading {

                }.onSuccess { res ->
                    dbRepository.updateToken(res.token)
                }.onFailure { error ->

                }
            }
        }
    }

    fun registerUser(ip: String, name: String, password: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            authRepository.loginUser(ip, name, password).collect { result ->
                result.isLoading {

                }.onSuccess { res ->
                    dbRepository.newUser(name, password, res.token, ip)
                }.onFailure { error ->

                }
            }
        }
    }
}