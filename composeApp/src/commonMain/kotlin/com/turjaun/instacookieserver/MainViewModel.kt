package com.turjaun.instacookieserver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turjaun.instacookieserver.data.StatusResponse
import com.turjaun.instacookieserver.repository.ServerMonitorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ServerMonitorRepository,
    private val onServerIpChange: (String) -> Unit
) : ViewModel() {

    private val _status = MutableStateFlow<StatusResponse?>(null)
    val status: StateFlow<StatusResponse?> = _status.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _serverIp = MutableStateFlow("")
    val serverIp: StateFlow<String> = _serverIp.asStateFlow()

    fun setServerIp(ip: String) {
        _serverIp.value = ip
        onServerIpChange(ip)
    }

    fun fetchStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.getStatus()
            _isLoading.value = false
            result.onSuccess {
                _status.value = it
            }.onFailure {
                _error.value = it.message ?: "Unknown error"
            }
        }
    }

    fun registerToken(token: String) {
        viewModelScope.launch {
            repository.registerToken(token)
        }
    }
}