package com.tzeentch.teacherhelper.presenters

import com.tzeentch.teacherhelper.repository.CameraRepository
import com.tzeentch.teacherhelper.repository.DetailsRepository
import org.koin.core.component.KoinComponent

class DetailsPresenter constructor(
    private val repository: DetailsRepository
) : KoinComponent {
}