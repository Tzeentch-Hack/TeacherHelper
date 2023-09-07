package com.tzeentch.teacherhelper.presenters

import com.tzeentch.teacherhelper.repository.CameraRepository
import com.tzeentch.teacherhelper.utils.LoginUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.component.KoinComponent

class LoginPresenter constructor(
    private val repository: CameraRepository
) : KoinComponent  {

    private val _navEvent = MutableSharedFlow<LoginUiState>()
    val navEvent: SharedFlow<LoginUiState> get() = _navEvent.asSharedFlow()

}