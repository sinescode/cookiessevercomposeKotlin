package com.turjaun.instacookieserver.data

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val token: String
)