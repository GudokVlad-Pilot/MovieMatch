package com.example.moviematch

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore instance
val Context.dataStore by preferencesDataStore("user_preferences")

class UserPreferences(private val context: Context) {
    companion object {
        private val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
    }

    // Retrieve the "Remember Me" state
    val rememberMeState: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[REMEMBER_ME_KEY] ?: false }

    // Save the "Remember Me" state
    suspend fun setRememberMeState(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMEMBER_ME_KEY] = value
        }
    }
}
