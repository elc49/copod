package com.lomolo.giggy.repository

import com.lomolo.giggy.model.Session
import com.lomolo.giggy.network.IGiggyRestApi
import com.lomolo.giggy.sql.dao.SessionDao
import kotlinx.coroutines.flow.Flow

interface ISession {
    fun get(): Flow<List<Session>>
    suspend fun signIn(phone: String)
    suspend fun signOut()
    suspend fun refreshSession(session: Session)
}

class SessionRepository(
    private val sessionDao: SessionDao,
    private val giggyRestApi: IGiggyRestApi,
): ISession {
    override fun get() = sessionDao.get()
    override suspend fun signIn(phone: String) {
        val res = giggyRestApi.signIn(phone)
        val newS = Session(
            id = res.userId,
            token = res.token,
        )
        sessionDao.create(newS)
    }

    override suspend fun refreshSession(session: Session) {
        val res = giggyRestApi.refreshSession(
            mapOf(
                "Refresh-Token" to session.id,
                "Authorization" to session.token,
            )
        )
        val newS = Session(
            id = res.userId,
            token = res.token,
        )
        sessionDao.update(newS)
    }

    override suspend fun signOut() {
        sessionDao.delete()
    }
}