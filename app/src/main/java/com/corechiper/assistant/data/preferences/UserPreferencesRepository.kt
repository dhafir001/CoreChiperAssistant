package com.corechiper.assistant.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "corechiper_prefs")

data class AppPreferences(
    val themeMode: String = "SYSTEM", // "LIGHT", "DARK", "SYSTEM"
    val languageCode: String = "id", // "id" or "en"
    val biometricEnabled: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {
    private val THEME_KEY = stringPreferencesKey("theme_mode")
    private val LANG_KEY = stringPreferencesKey("language_code")
    private val BIOMETRIC_KEY = booleanPreferencesKey("biometric_enabled")

    val preferencesFlow: Flow<AppPreferences> = context.dataStore.data.map { prefs ->
        AppPreferences(
            themeMode = prefs[THEME_KEY] ?: "SYSTEM",
            languageCode = prefs[LANG_KEY] ?: "id",
            biometricEnabled = prefs[BIOMETRIC_KEY] ?: false
        )
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[THEME_KEY] = mode }
    }

    suspend fun setLanguage(code: String) {
        context.dataStore.edit { it[LANG_KEY] = code }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { it[BIOMETRIC_KEY] = enabled }
    }
}
