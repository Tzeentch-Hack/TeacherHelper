package com.tzeentch.teacherhelper.di

import com.squareup.sqldelight.db.SqlDriver

expect class MainDbDriverFactory {
    fun createDriver(): SqlDriver
}