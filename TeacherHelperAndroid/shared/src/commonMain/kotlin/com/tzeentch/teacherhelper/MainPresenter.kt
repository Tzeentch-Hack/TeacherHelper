package com.tzeentch.teacherhelper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent

class MainPresenter constructor(
    private val repository: MainRepository
) : KoinComponent {

    private val viewModelScope = CoroutineScope(Dispatchers.Default)
}