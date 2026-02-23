package com.turjaun.serverstatuscookies.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>
    
    @Query("SELECT * FROM notifications WHERE isRead = 0 ORDER BY timestamp DESC")
    fun getUnreadNotifications(): Flow<List<NotificationEntity>>
    
    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>
    
    @Insert
    suspend fun insertNotification(notification: NotificationEntity): Long
    
    @Update
    suspend fun updateNotification(notification: NotificationEntity)
    
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)
    
    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
    
    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)
    
    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM notifications")
    suspend fun clearAll()
    
    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getNotificationById(id: Long): NotificationEntity?
}