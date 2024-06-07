package com.lomolo.giggy.repository

import com.lomolo.giggy.model.Session
import com.lomolo.giggy.network.IGiggyRestApi
import com.lomolo.giggy.sql.dao.SessionDao
import kotlinx.coroutines.flow.Flow

interface ISession {
    fun get(): Flow<List<Session>>
    suspend fun signIn(phone: String)
}

class SessionRepository(
    private val sessionDao: SessionDao,
    private val giggyRestApi: IGiggyRestApi,
): ISession {
    override fun get() = sessionDao.get()
    override suspend fun signIn(phone: String) {
        val res = giggyRestApi.signIn(phone)
        val newS = Session(
            token = res.token,
        )
        sessionDao.create(newS)
    }
}