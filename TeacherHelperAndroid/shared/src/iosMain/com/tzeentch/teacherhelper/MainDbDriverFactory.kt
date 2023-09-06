package com.borisgames.weatherquake.di

import android.content.Context
import com.borisgames.weatherquake.dto.sqldelight.AppDb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class MainDbDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDb.Schema, context, "app.db")
    }
}