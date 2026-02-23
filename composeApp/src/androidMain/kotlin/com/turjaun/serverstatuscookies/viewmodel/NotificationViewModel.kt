package com.turjaun.serverstatuscookies.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.turjaun.serverstatuscookies.data.AppDatabase
import com.turjaun.serverstatuscookies.data.NotificationEntity
import com.turjaun.serverstatuscookies.repository.NotificationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: NotificationRepository
    
    val allNotifications: StateFlow<List<NotificationEntity>>
    val unreadCount: StateFlow<Int>
    
    private val _selectedNotification = MutableStateFlow<NotificationEntity?>(null)
    val selectedNotification: StateFlow<NotificationEntity?> = _selectedNotification.asStateFlow()
    
    init {
        val notificationDao = AppDatabase.getDatabase(application).notificationDao()
        repository = NotificationRepository(notificationDao)
        
        allNotifications = repository.allNotifications
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
        unreadCount = repository.unreadCount
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    }
    
    fun addNotification(title: String, body: String, data: String? = null, priority: String = "high") {
        viewModelScope.launch {
            repository.addNotification(title, body, data, priority)
        }
    }
    
    fun markAsRead(id: Long) {
        viewModelScope.launch {
            repository.markAsRead(id)
        }
    }
    
    fun markAllAsRead() {
        viewModelScope.launch {
            repository.markAllAsRead()
        }
    }
    
    fun deleteNotification(notification: NotificationEntity) {
        viewModelScope.launch {
            repository.deleteNotification(notification)
        }
    }
    
    fun deleteById(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }
    
    fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
    
    fun selectNotification(notification: NotificationEntity?) {
        _selectedNotification.value = notification
    }
}