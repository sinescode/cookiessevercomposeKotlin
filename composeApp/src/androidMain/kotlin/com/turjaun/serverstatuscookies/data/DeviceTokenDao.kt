package com.turjaun.serverstatuscookies.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceTokenDao {

    @Query("SELECT * FROM device_tokens ORDER BY addedAt DESC")
    fun getAllTokens(): Flow<List<DeviceToken>>

    @Query("SELECT * FROM device_tokens WHERE token = :token LIMIT 1")
    suspend fun getToken(token: String): DeviceToken?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(deviceToken: DeviceToken)

    @Delete
    suspend fun deleteToken(deviceToken: DeviceToken)

    @Query("DELETE FROM device_tokens WHERE token = :token")
    suspend fun deleteByToken(token: String)

    @Query("DELETE FROM device_tokens")
    suspend fun clearAll()
}