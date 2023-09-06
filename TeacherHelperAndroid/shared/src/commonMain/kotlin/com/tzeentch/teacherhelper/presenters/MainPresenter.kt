package com.tzeentch.teacherhelper.presenters

import com.tzeentch.teacherhelper.repository.MainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent

class MainPresenter constructor(
    private val repository: MainRepository
) : KoinComponent {

    private val viewModelScope = CoroutineScope(Dispatchers.Default)
}