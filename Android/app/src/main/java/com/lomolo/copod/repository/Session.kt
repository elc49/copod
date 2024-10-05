package com.lomolo.copod.repository

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
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
    private suspend fun refreshNotificationTrackingId(userId: String) {
        var token = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FirebaseMessaging", "Fetching token failed", task.exception)
                    return@OnCompleteListener
                }

                // New token
                token = task.result
            })
        // Track notification token generation
        copodRestApi.refreshNotificationId(token, userId)
    }

    override fun get() = sessionDao.get()
    override suspend fun signIn(phone: String) {
        val res = copodRestApi.signIn(phone)
        val newS = Session(
            id = res.userId,
            token = res.token,
            hasFarmingRights = res.hasFarmingRights,
            hasPosterRights = res.hasPosterRights,
        )
       refreshNotificationTrackingId(res.userId)
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
        refreshNotificationTrackingId(res.userId)
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