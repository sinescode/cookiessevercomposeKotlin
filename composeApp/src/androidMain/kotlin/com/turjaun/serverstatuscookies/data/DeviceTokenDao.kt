package com.turjaun.serverstatuscookies.data

import androidx.room.*

@Dao
interface DeviceTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: DeviceToken)

    @Query("SELECT * FROM device_tokens ORDER BY addedAt DESC")
    fun getAllTokens(): List<DeviceToken>

    @Query("SELECT * FROM device_tokens WHERE token = :token")
    suspend fun getToken(token: String): DeviceToken?

    @Delete
    suspend fun deleteToken(token: DeviceToken)

    @Query("DELETE FROM device_tokens WHERE token = :token")
    suspend fun deleteByToken(token: String)
}