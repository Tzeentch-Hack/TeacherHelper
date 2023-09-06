package com.tzeentch.teacherhelper.repository

import com.tzeentch.teacherhelper.di.MainDbDriverFactory

interface DbRepository {
    fun newUser(name: String, password: String, token: String)
    fun updateToken(token: String)
    fun getUser(): Triple<String?, String?, String?>
}

class DbRepositoryImpl constructor(mainDbDriverFactory: MainDbDriverFactory) : DbRepository {


    private val mainDatabase =
        com.tzeentch.teacherhelper.dto.sqldelight.AppDb.invoke(
            mainDbDriverFactory.createDriver(),
        )
    private val dbQuery = mainDatabase.mainDbQueries

    override fun newUser(name: String, password: String, token: String) {
        dbQuery.transaction {
            dbQuery.newUser(name, password, token)
        }
    }

    override fun updateToken(token: String) {
        dbQuery.transaction {
            dbQuery.updateToken(token)
        }
    }

    override fun getUser(): Triple<String?, String?, String?> {
        val currentUser = dbQuery.getUser().executeAsOne()
        return Triple(currentUser.name, currentUser.password, currentUser.token)
    }

}