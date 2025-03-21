package com.example.budgetbuddy.auth

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val PREF_NAME = "TrackyyySession"
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val KEY_EMAIL = "email"
        const val KEY_USERNAME = "username"
    }

    fun createLoginSession(email: String, username: String) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserDetails(): HashMap<String, String?> {
        val user = HashMap<String, String?>()
        user[KEY_EMAIL] = sharedPreferences.getString(KEY_EMAIL, null)
        user[KEY_USERNAME] = sharedPreferences.getString(KEY_USERNAME, null)
        return user
    }

    fun logoutUser() {
        editor.clear()
        editor.apply()
    }
} 