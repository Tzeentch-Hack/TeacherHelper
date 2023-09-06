package com.tzeentch.teacherhelper.di

import com.tzeentch.teacherhelper.presenters.AuthPresenter
import com.tzeentch.teacherhelper.presenters.CameraPresenter
import com.tzeentch.teacherhelper.presenters.DetailsPresenter
import com.tzeentch.teacherhelper.presenters.LoginPresenter
import com.tzeentch.teacherhelper.repository.AuthRepository
import com.tzeentch.teacherhelper.repository.AuthRepositoryImpl
import com.tzeentch.teacherhelper.presenters.MainPresenter
import com.tzeentch.teacherhelper.repository.CameraRepository
import com.tzeentch.teacherhelper.repository.CameraRepositoryImpl
import com.tzeentch.teacherhelper.repository.DbRepository
import com.tzeentch.teacherhelper.repository.DbRepositoryImpl
import com.tzeentch.teacherhelper.repository.DetailsRepository
import com.tzeentch.teacherhelper.repository.DetailsRepositoryImpl
import com.tzeentch.teacherhelper.repository.LoginRepository
import com.tzeentch.teacherhelper.repository.LoginRepositoryImpl
import com.tzeentch.teacherhelper.repository.MainRepository
import com.tzeentch.teacherhelper.repository.MainRepositoryImpl
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.addDefaultResponseValidation
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module


fun injectionModule(enableNetworkLogs: Boolean = false) = module {
    single {
        HttpClient(engineFactory = CIO) {
            expectSuccess = true
            addDefaultResponseValidation()

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTP
                }
            }

            if (enableNetworkLogs) {
                install(Logging) {
                    level = LogLevel.ALL
                    logger = object : Logger {
                        override fun log(message: String) {
                            Napier.i(tag = "Http Client", message = message)
                        }
                    }
                }.also {
                    Napier.base(DebugAntilog())
                }
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
        }
    }

    single<MainRepository> { MainRepositoryImpl(httpClient = get()) }
    factoryOf(::MainPresenter)

    single<AuthRepository> { AuthRepositoryImpl(httpClient = get()) }
    factoryOf(::AuthPresenter)

    single<CameraRepository> { CameraRepositoryImpl(httpClient = get()) }
    factoryOf(::CameraPresenter)

    single<LoginRepository> { LoginRepositoryImpl(httpClient = get()) }
    factoryOf(::LoginPresenter)

    single<DetailsRepository> { DetailsRepositoryImpl(httpClient = get()) }
    factoryOf(::DetailsPresenter)

    single<DbRepository> {
        DbRepositoryImpl(
            mainDbDriverFactory = get()
        )
    }
}