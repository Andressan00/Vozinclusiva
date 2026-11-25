package com.example.vozinclusiva.data.repository

import android.content.Context
import com.example.vozinclusiva.data.local.model.UserProfile
import com.example.vozinclusiva.data.local.model.UserType

/**
 * Guarda y lee los datos básicos del usuario de forma local (SharedPreferences privadas).
 * No requiere dependencias nuevas ni internet.
 */
class UserProfileRepository(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(profile: UserProfile) {
        prefs.edit()
            .putString(KEY_NAME, profile.name.trim())
            .putString(KEY_TYPE, profile.userType.name)
            .putString(KEY_AGE, profile.age.trim())
            .putString(KEY_PHONE, profile.phone.trim())
            .putString(KEY_EMERGENCY_NAME, profile.emergencyName.trim())
            .putString(KEY_EMERGENCY_PHONE, profile.emergencyPhone.trim())
            .putString(KEY_NOTES, profile.notes.trim())
            .apply()
    }

    /** Devuelve el perfil guardado o null si todavía no hay datos. */
    fun load(): UserProfile? {
        val name = prefs.getString(KEY_NAME, null) ?: return null
        val type = runCatching { UserType.valueOf(prefs.getString(KEY_TYPE, "") ?: "") }
            .getOrDefault(UserType.SORDA)
        return UserProfile(
            name = name,
            userType = type,
            age = prefs.getString(KEY_AGE, "") ?: "",
            phone = prefs.getString(KEY_PHONE, "") ?: "",
            emergencyName = prefs.getString(KEY_EMERGENCY_NAME, "") ?: "",
            emergencyPhone = prefs.getString(KEY_EMERGENCY_PHONE, "") ?: "",
            notes = prefs.getString(KEY_NOTES, "") ?: ""
        )
    }

    fun hasProfile(): Boolean = prefs.contains(KEY_NAME)

    fun clear() = prefs.edit().clear().apply()

    private companion object {
        const val PREFS_NAME = "vozinclusiva_profile"
        const val KEY_NAME = "name"
        const val KEY_TYPE = "user_type"
        const val KEY_AGE = "age"
        const val KEY_PHONE = "phone"
        const val KEY_EMERGENCY_NAME = "emergency_name"
        const val KEY_EMERGENCY_PHONE = "emergency_phone"
        const val KEY_NOTES = "notes"
    }
}