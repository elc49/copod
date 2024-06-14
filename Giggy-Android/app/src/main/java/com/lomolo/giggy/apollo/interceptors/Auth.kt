package com.lomolo.giggy.apollo.interceptors

import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.lomolo.giggy.repository.ISession
import com.lomolo.giggy.sql.dao.SessionDao

class AuthInterceptor(
    private val sessionDao: SessionDao,
    private val sessionRepository: ISession
): HttpInterceptor {
    private val mutex = Mutex()

    override suspend fun intercept(
        request: HttpRequest,
        chain: HttpInterceptorChain
    ): HttpResponse {
        var session = mutex.withLock {
            sessionDao
                .get()
                .firstOrNull()
        }

        val response = chain.proceed(
            request.newBuilder().addHeader("Authorization", "Bearer ${session!!.first().token}").build()
        )

        return if (response.statusCode == 401) {
            session = mutex.withLock {
                sessionRepository.refreshSession(session!!.first())
                sessionDao
                    .get()
                    .firstOrNull()
            }

            chain.proceed(
                request.newBuilder().addHeader("Authorization", "Bearer ${session!!.first().token}").build()
            )
        } else {
            return response
        }
    }
}