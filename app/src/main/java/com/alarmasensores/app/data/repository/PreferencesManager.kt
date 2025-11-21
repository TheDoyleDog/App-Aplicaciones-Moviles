package com.alarmasensores.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.alarmasensores.app.data.model.AlarmConfig
import com.alarmasensores.app.data.model.AlarmState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Manager para persistencia de preferencias usando DataStore
 */
class PreferencesManager(private val context: Context) {
    
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = "alarm_preferences"
        )
        
        // Keys para AlarmState
        private val ALARM_ENABLED = booleanPreferencesKey("alarm_enabled")
        private val ALARM_LAST_UPDATE = longPreferencesKey("alarm_last_update")
        
        // Keys para AlarmConfig
        private val SCHEDULE_FROM = stringPreferencesKey("schedule_from")
        private val SCHEDULE_TO = stringPreferencesKey("schedule_to")
        private val DETECTION_DISTANCE = intPreferencesKey("detection_distance")
        private val ALARM_SOUND = stringPreferencesKey("alarm_sound")
        
        // Keys para User
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }
    
    /**
     * Guardar estado de la alarma
     */
    suspend fun saveAlarmState(state: AlarmState) {
        context.dataStore.edit { preferences ->
            preferences[ALARM_ENABLED] = state.enabled
            preferences[ALARM_LAST_UPDATE] = state.lastUpdate
        }
    }
    
    /**
     * Obtener estado de la alarma
     */
    val alarmState: Flow<AlarmState> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            AlarmState(
                enabled = preferences[ALARM_ENABLED] ?: false,
                lastUpdate = preferences[ALARM_LAST_UPDATE] ?: 0L
            )
        }
    
    /**
     * Guardar configuración de la alarma
     */
    suspend fun saveAlarmConfig(config: AlarmConfig) {
        context.dataStore.edit { preferences ->
            preferences[SCHEDULE_FROM] = config.scheduleFrom
            preferences[SCHEDULE_TO] = config.scheduleTo
            preferences[DETECTION_DISTANCE] = config.detectionDistance
            preferences[ALARM_SOUND] = config.alarmSound
        }
    }
    
    /**
     * Obtener configuración de la alarma
     */
    val alarmConfig: Flow<AlarmConfig> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            AlarmConfig(
                scheduleFrom = preferences[SCHEDULE_FROM] ?: "22:00",
                scheduleTo = preferences[SCHEDULE_TO] ?: "06:00",
                detectionDistance = preferences[DETECTION_DISTANCE] ?: 5,
                alarmSound = preferences[ALARM_SOUND] ?: "Siren"
            )
        }
    
    /**
     * Guardar información de usuario
     */
    suspend fun saveUserInfo(userId: String, email: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USER_EMAIL] = email
            preferences[USER_NAME] = name
            preferences[IS_LOGGED_IN] = true
        }
    }
    
    /**
     * Obtener estado de login
     */
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }
    
    /**
     * Obtener email del usuario
     */
    val userEmail: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[USER_EMAIL] ?: ""
        }
    
    /**
     * Limpiar datos de usuario (logout)
     */
    suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(USER_EMAIL)
            preferences.remove(USER_NAME)
            preferences[IS_LOGGED_IN] = false
        }
    }
    
    /**
     * Limpiar todas las preferencias
     */
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
