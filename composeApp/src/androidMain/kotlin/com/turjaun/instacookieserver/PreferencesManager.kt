package com.turjaun.instacookieserver

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class PreferencesManager(private val context: Context) {
    companion object {
        private val SERVER_IP_KEY = stringPreferencesKey("server_ip")
        private const val DEFAULT_IP = "http://192.168.1.100:5000/"
    }

    suspend fun getServerIp(): String = context.dataStore.data.map { preferences ->
        preferences[SERVER_IP_KEY] ?: DEFAULT_IP
    }.first()

    suspend fun saveServerIp(ip: String) {
        context.dataStore.edit { preferences ->
            preferences[SERVER_IP_KEY] = ip
        }
    }
}