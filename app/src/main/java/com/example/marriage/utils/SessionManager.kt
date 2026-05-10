package com.example.marriage.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.marriage.network.models.UserData
import com.google.gson.Gson

/**
 * Manages JWT tokens and user session using SharedPreferences.
 * Single source of truth for auth state across the app.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val gson = Gson()

    companion object {
        private const val PREF_NAME      = "marriage_session"
        private const val KEY_ACCESS     = "access_token"
        private const val KEY_REFRESH    = "refresh_token"
        private const val KEY_USER       = "user_data"
        private const val KEY_USER_ID    = "user_id"
        private const val KEY_USER_NAME  = "user_name"
        private const val KEY_LOGGED_IN  = "is_logged_in"
    }

    // ── Save ──────────────────────────────────────────────────

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString(KEY_ACCESS, accessToken)
            .putString(KEY_REFRESH, refreshToken)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    fun saveUser(user: UserData) {
        prefs.edit()
            .putString(KEY_USER, gson.toJson(user))
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_USER_NAME, user.fullName)
            .apply()
    }

    // ── Get ───────────────────────────────────────────────────

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH, null)

    /** Returns "Bearer <token>" ready for Authorization header */
    fun getBearerToken(): String = "Bearer ${getAccessToken() ?: ""}"

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    fun getUser(): UserData? {
        val json = prefs.getString(KEY_USER, null) ?: return null
        return try { gson.fromJson(json, UserData::class.java) } catch (e: Exception) { null }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)

    // ── Clear ─────────────────────────────────────────────────

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
