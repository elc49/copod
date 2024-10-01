package com.lomolo.copod.repository

import com.lomolo.copod.model.Session
import com.lomolo.copod.network.ICopodRestApi
import com.lomolo.copod.sql.dao.SessionDao
import kotlinx.coroutines.flow.Flow

interface ISession {
    fun get(): Flow<List<Session>>
    suspend fun signIn(phone: String)
    suspend fun signOut()
    suspend fun refreshSession(sessionId: String)
    suspend fun updateSession(session: Session)
}

class SessionRepository(
    private val sessionDao: SessionDao,
    private val copodRestApi: ICopodRestApi,
): ISession {
    override fun get() = sessionDao.get()
    override suspend fun signIn(phone: String) {
        val res = copodRestApi.signIn(phone)
        val newS = Session(
            id = res.userId,
            token = res.token,
            hasFarmingRights = res.hasFarmingRights,
            hasPosterRights = res.hasPosterRights,
        )
        sessionDao.create(newS)
    }

    override suspend fun refreshSession(sessionId: String) {
        val res = copodRestApi.refreshSession(
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

    override suspend fun updateSession(session: Session) {
        val newS = Session(
            id = session.id,
            token = session.token,
            hasFarmingRights = true,
            hasPosterRights = session.hasPosterRights,
        )
        sessionDao.update(newS)
    }

    override suspend fun signOut() {
        sessionDao.delete()
    }
}