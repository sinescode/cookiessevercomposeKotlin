package com.turjaun.instacookieserver.data

import kotlinx.serialization.Serializable

@Serializable
data class StatusResponse(
    val status: String,
    val last_checked: String
)