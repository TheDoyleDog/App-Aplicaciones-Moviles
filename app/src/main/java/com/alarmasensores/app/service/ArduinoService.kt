package com.alarmasensores.app.service

import android.util.Log
import com.alarmasensores.app.data.model.AlarmConfig
import com.alarmasensores.app.data.model.SensorData
import com.google.firebase.database.DatabaseReference

/**
 * Servicio para comunicación con Arduino
 * Abstrae la implementación de Firebase/ThingSpeak
 */
class ArduinoService {
    
    private val firebaseHelper = FirebaseHelper()
    private var sensorDataListener: DatabaseReference? = null
    
    companion object {
        private const val TAG = "ArduinoService"
    }
    
    /**
     * Conectar y empezar a escuchar datos del sensor
     */
    fun connect(
        onSensorDataReceived: (SensorData) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        Log.d(TAG, "Conectando con Arduino...")
        
        // Iniciar listener de Firebase
        sensorDataListener = firebaseHelper.readSensorData(
            onDataReceived = { data ->
                Log.d(TAG, "Datos del sensor recibidos: $data")
                onSensorDataReceived(data)
            },
            onError = { error ->
                Log.e(TAG, "Error recibiendo datos: $error")
                onError(error)
            }
        )
    }
    
    /**
     * Desconectar del Arduino
     */
    fun disconnect() {
        Log.d(TAG, "Desconectando de Arduino...")
        sensorDataListener?.removeEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {}
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
        sensorDataListener = null
    }
    
    /**
     * Activar alarma
     */
    fun enableAlarm(
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        Log.d(TAG, "Activando alarma en Arduino...")
        firebaseHelper.setAlarmEnabled(
            enabled = true,
            onSuccess = {
                Log.d(TAG, "✅ Alarma activada")
                onSuccess()
            },
            onError = { error ->
                Log.e(TAG, "❌ Error activando alarma: $error")
                onError(error)
            }
        )
    }
    
    /**
     * Desactivar alarma
     */
    fun disableAlarm(
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        Log.d(TAG, "Desactivando alarma en Arduino...")
        firebaseHelper.setAlarmEnabled(
            enabled = false,
            onSuccess = {
                Log.d(TAG, "✅ Alarma desactivada")
                onSuccess()
            },
            onError = { error ->
                Log.e(TAG, "❌ Error desactivando alarma: $error")
                onError(error)
            }
        )
    }
    
    /**
     * Enviar configuración al Arduino
     */
    fun sendConfiguration(
        config: AlarmConfig,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        Log.d(TAG, "Enviando configuración al Arduino: $config")
        
        // TODO: Implementar envío de configuración completa
        // Por ahora solo enviamos la intensidad basada en la distancia
        val intensity = (config.detectionDistance * 10).coerceIn(0, 100)
        
        firebaseHelper.setBuzzerIntensity(
            intensity = intensity,
            onSuccess = {
                Log.d(TAG, "✅ Configuración enviada")
                onSuccess()
            },
            onError = { error ->
                Log.e(TAG, "❌ Error enviando configuración: $error")
                onError(error)
            }
        )
    }
    
    /**
     * Obtener estado actual del sensor (lectura única)
     */
    fun getSensorStatus(
        onDataReceived: (SensorData) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        Log.d(TAG, "Obteniendo estado del sensor...")
        firebaseHelper.readSensorDataOnce(
            onDataReceived = { data ->
                Log.d(TAG, "Estado del sensor: $data")
                onDataReceived(data)
            },
            onError = { error ->
                Log.e(TAG, "Error obteniendo estado: $error")
                onError(error)
            }
        )
    }
    
    /**
     * Verificar conexión con Arduino
     */
    fun checkConnection(
        onConnected: () -> Unit,
        onDisconnected: () -> Unit
    ) {
        // Intentar leer datos para verificar conexión
        getSensorStatus(
            onDataReceived = { onConnected() },
            onError = { onDisconnected() }
        )
    }
}
