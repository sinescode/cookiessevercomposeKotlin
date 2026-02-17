package com.turjaun.instacookieserver.di

import com.turjaun.instacookieserver.repository.ServerMonitorRepository
import com.turjaun.instacookieserver.repository.ServerMonitorRepositoryImpl

object RepositoryProvider {
    private var currentRepository: ServerMonitorRepository? = null
    private var currentBaseUrl: String? = null

    fun getRepository(baseUrl: String): ServerMonitorRepository {
        if (currentRepository == null || currentBaseUrl != baseUrl) {
            currentBaseUrl = baseUrl
            currentRepository = ServerMonitorRepositoryImpl(baseUrl)
        }
        return currentRepository!!
    }

    fun updateRepository(baseUrl: String) {
        currentBaseUrl = baseUrl
        currentRepository = ServerMonitorRepositoryImpl(baseUrl)
    }
}