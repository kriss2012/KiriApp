package com.kiriplatform.app.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("asg_prefs", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context).also { instance = it }
            }
        }
    }

    fun saveToken(token: String?) {
        token?.let { prefs.edit().putString("auth_token", it).apply() }
    }

    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun saveRefreshToken(token: String?) {
        token?.let { prefs.edit().putString("refresh_token", it).apply() }
    }

    fun getRefreshToken(): String? {
        return prefs.getString("refresh_token", null)
    }

    fun saveUserId(userId: String?) {
        userId?.let { prefs.edit().putString("user_id", it).apply() }
    }

    fun getUserId(): String? {
        return prefs.getString("user_id", null)
    }

    fun saveUserRole(role: String?) {
        role?.let { prefs.edit().putString("user_role", it).apply() }
    }

    fun getUserRole(): String? {
        return prefs.getString("user_role", "STUDENT")
    }

    fun setCanCreateEvents(canCreate: Boolean) {
        prefs.edit().putBoolean("can_create_events", canCreate).apply()
    }

    fun canCreateEvents(): Boolean {
        return prefs.getBoolean("can_create_events", false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    fun saveUserName(name: String?) {
        name?.let { prefs.edit().putString("user_name", it).apply() }
    }

    fun getUserName(): String? {
        return prefs.getString("user_name", null)
    }
}
