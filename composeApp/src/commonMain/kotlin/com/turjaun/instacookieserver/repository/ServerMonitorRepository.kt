package com.turjaun.instacookieserver.repository

import com.turjaun.instacookieserver.data.StatusResponse

interface ServerMonitorRepository {
    suspend fun getStatus(): Result<StatusResponse>
    suspend fun registerToken(token: String): Result<Unit>
}