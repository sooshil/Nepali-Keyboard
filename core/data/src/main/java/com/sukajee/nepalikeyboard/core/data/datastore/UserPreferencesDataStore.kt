package com.sukajee.nepalikeyboard.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "nepali_keyboard_prefs"
)

/**
 * Persists user preferences for the keyboard.
 * Access via Koin injection — do not instantiate directly.
 */
class UserPreferencesDataStore(private val context: Context) {

    companion object {
        private val KEY_INPUT_MODE = stringPreferencesKey("input_mode")
        private val KEY_THEME = stringPreferencesKey("theme")
        private val KEY_VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        private val KEY_SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val KEY_KEY_HEIGHT_DP = intPreferencesKey("key_height_dp")
        private val KEY_SHOW_SUGGESTIONS = booleanPreferencesKey("show_suggestions")
        private val KEY_AUTO_CORRECT_ENABLED = booleanPreferencesKey("auto_correct_enabled")
    }

    val inputMode: Flow<String> = context.dataStore.data
        .map { it[KEY_INPUT_MODE] ?: "DEVANAGARI" }

    val theme: Flow<String> = context.dataStore.data
        .map { it[KEY_THEME] ?: "SYSTEM" }

    val vibrationEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_VIBRATION_ENABLED] ?: true }

    val soundEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_SOUND_ENABLED] ?: false }

    val keyHeightDp: Flow<Int> = context.dataStore.data
        .map { it[KEY_KEY_HEIGHT_DP] ?: 52 }

    val showSuggestions: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_SHOW_SUGGESTIONS] ?: true }

    val autoCorrectEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_AUTO_CORRECT_ENABLED] ?: false }

    suspend fun setInputMode(mode: String) {
        context.dataStore.edit { it[KEY_INPUT_MODE] = mode }
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { it[KEY_THEME] = theme }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_VIBRATION_ENABLED] = enabled }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SOUND_ENABLED] = enabled }
    }

    suspend fun setKeyHeightDp(dp: Int) {
        context.dataStore.edit { it[KEY_KEY_HEIGHT_DP] = dp }
    }

    suspend fun setShowSuggestions(show: Boolean) {
        context.dataStore.edit { it[KEY_SHOW_SUGGESTIONS] = show }
    }

    suspend fun setAutoCorrectEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_CORRECT_ENABLED] = enabled }
    }
}