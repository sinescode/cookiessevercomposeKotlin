package com.turjaun.instacookieserver.repository

import com.turjaun.instacookieserver.ApiService
import com.turjaun.instacookieserver.RetrofitClient
import com.turjaun.instacookieserver.data.RegisterRequest
import com.turjaun.instacookieserver.data.StatusResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServerMonitorRepositoryImpl(private val baseUrl: String) : ServerMonitorRepository {
    private val api: ApiService by lazy { RetrofitClient.create(baseUrl) }

    override suspend fun getStatus(): Result<StatusResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getStatus()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun registerToken(token: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.registerDevice(RegisterRequest(token))
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Registration failed: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}