package com.tzeentch.teacherhelper.di

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import com.tzeentch.teacherhelper.dto.sqldelight.AppDb

actual class MainDbDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDb.Schema, context, "app.db")
    }
}