package com.tzeentch.teacherhelper.presenters

import com.tzeentch.teacherhelper.repository.CameraRepository
import com.tzeentch.teacherhelper.repository.DbRepository
import com.tzeentch.teacherhelper.repository.DetailsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent

class DetailsPresenter constructor(
    private val repository: DetailsRepository,
    private val dbRepository: DbRepository
) : KoinComponent {

    private val viewModelScope = CoroutineScope(Dispatchers.Default)
}