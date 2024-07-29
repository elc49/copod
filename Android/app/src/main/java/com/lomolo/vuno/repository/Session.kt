package com.lomolo.vuno.repository

import com.lomolo.vuno.model.Session
import com.lomolo.vuno.network.IVunoRestApi
import com.lomolo.vuno.sql.dao.SessionDao
import kotlinx.coroutines.flow.Flow

interface ISession {
    fun get(): Flow<List<Session>>
    suspend fun signIn(phone: String)
    suspend fun signOut()
    suspend fun refreshSession(sessionId: String)
}

class SessionRepository(
    private val sessionDao: SessionDao,
    private val giggyRestApi: IVunoRestApi,
): ISession {
    override fun get() = sessionDao.get()
    override suspend fun signIn(phone: String) {
        val res = giggyRestApi.signIn(phone)
        val newS = Session(
            id = res.userId,
            token = res.token,
            hasFarmingRights = res.hasFarmingRights,
            hasPosterRights = res.hasPosterRights,
        )
        sessionDao.create(newS)
    }

    override suspend fun refreshSession(sessionId: String) {
        val res = giggyRestApi.refreshSession(
            mapOf(
                "Refresh-Token" to sessionId,
            )
        )
        val newS = Session(
            id = res.userId,
            token = res.token,
            hasFarmingRights = res.hasFarmingRights,
            hasPosterRights = res.hasPosterRights,
        )
        sessionDao.update(newS)
    }

    override suspend fun signOut() {
        sessionDao.delete()
    }
}