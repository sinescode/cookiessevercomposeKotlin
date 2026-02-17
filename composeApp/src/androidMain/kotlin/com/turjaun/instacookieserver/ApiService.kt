package com.turjaun.instacookieserver

import com.turjaun.instacookieserver.data.RegisterRequest
import com.turjaun.instacookieserver.data.StatusResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("status")
    suspend fun getStatus(): Response<StatusResponse>

    @POST("register")
    suspend fun registerDevice(@Body request: RegisterRequest): Response<Unit>
}