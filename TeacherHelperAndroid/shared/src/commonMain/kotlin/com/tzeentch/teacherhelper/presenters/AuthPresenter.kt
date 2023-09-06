package com.tzeentch.teacherhelper.presenters

import com.tzeentch.teacherhelper.repository.AuthRepository
import org.koin.core.component.KoinComponent

class AuthPresenter constructor(private val authRepository: AuthRepository) : KoinComponent {
}