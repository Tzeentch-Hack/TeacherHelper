package com.tzeentch.teacherhelper.repository

import com.tzeentch.teacherhelper.di.MainDbDriverFactory
import com.tzeentch.teacherhelper.dto.UserData

interface DbRepository {
    fun newUser(name: String, password: String, token: String,ip:String)
    fun updateToken(token: String)
    fun getUser(): UserData
}

class DbRepositoryImpl constructor(mainDbDriverFactory: MainDbDriverFactory) : DbRepository {


    private val mainDatabase =
        com.tzeentch.teacherhelper.dto.sqldelight.AppDb.invoke(
            mainDbDriverFactory.createDriver(),
        )
    private val dbQuery = mainDatabase.mainDbQueries

    override fun newUser(name: String, password: String, token: String,ip:String) {
        dbQuery.transaction {
            dbQuery.newUser(name, password, token,ip)
        }
    }

    override fun updateToken(token: String) {
        dbQuery.transaction {
            dbQuery.updateToken(token)
        }
    }

    override fun getUser(): UserData {
        val currentUser = dbQuery.getUser().executeAsOne()
        return UserData(currentUser.name, currentUser.password, currentUser.token,currentUser.ip)
    }

}