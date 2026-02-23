package com.turjaun.serverstatuscookies.repository

import com.turjaun.serverstatuscookies.data.NotificationDao
import com.turjaun.serverstatuscookies.data.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

class NotificationRepository(private val notificationDao: NotificationDao) {
    
    val allNotifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()
    val unreadNotifications: Flow<List<NotificationEntity>> = notificationDao.getUnreadNotifications()
    val unreadCount: Flow<Int> = notificationDao.getUnreadCount()
    
    suspend fun addNotification(title: String, body: String, data: String? = null, priority: String = "high"): Long {
        val notification = NotificationEntity(
            title = title,
            body = body,
            data = data,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            priority = priority,
            isRead = false
        )
        return notificationDao.insertNotification(notification)
    }
    
    suspend fun markAsRead(id: Long) {
        notificationDao.markAsRead(id)
    }
    
    suspend fun markAllAsRead() {
        notificationDao.markAllAsRead()
    }
    
    suspend fun deleteNotification(notification: NotificationEntity) {
        notificationDao.deleteNotification(notification)
    }
    
    suspend fun deleteById(id: Long) {
        notificationDao.deleteById(id)
    }
    
    suspend fun clearAll() {
        notificationDao.clearAll()
    }
    
    suspend fun getNotificationById(id: Long): NotificationEntity? {
        return notificationDao.getNotificationById(id)
    }
}