package com.alarmasensores.app.service

import android.util.Log
import com.alarmasensores.app.data.model.ActuatorControl
import com.alarmasensores.app.data.model.SensorData
import com.google.firebase.database.*

/**
 * Helper para interacción con Firebase Realtime Database
 * Basado en los ejemplos de Firebase proporcionados
 */
class FirebaseHelper {
    
    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    
    companion object {
        private const val TAG = "FirebaseHelper"
        private const val SENSOR_DATA_PATH = "sensor_data"
        private const val ACTUATOR_CONTROL_PATH = "actuator_control"
    }
    
    /**
     * Escribir datos del actuador a Firebase
     * Basado en EscribirFirebase.kt
     */
    fun writeActuatorControl(
        control: ActuatorControl,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val myRef = database.getReference(ACTUATOR_CONTROL_PATH)
        
        Log.d(TAG, "Iniciando escritura en: $ACTUATOR_CONTROL_PATH")
        Log.d(TAG, "Objeto a escribir: $control")
        
        myRef.setValue(control)
            .addOnSuccessListener {
                Log.d(TAG, "✅ Datos escritos exitosamente en $ACTUATOR_CONTROL_PATH")
                onSuccess()
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "❌ Error escribiendo en $ACTUATOR_CONTROL_PATH", error)
                onError("Error: ${error.message}")
            }
    }
    
    /**
     * Leer datos del sensor desde Firebase
     * Basado en LeerFirebase.kt
     */
    fun readSensorData(
        onDataReceived: (SensorData) -> Unit,
        onError: (String) -> Unit = {}
    ): DatabaseReference {
        val myRef = database.getReference(SENSOR_DATA_PATH)
        
        Log.d(TAG, "Iniciando lectura de: $SENSOR_DATA_PATH")
        
        // Mantener sincronizado para uso offline
        myRef.keepSynced(true)
        
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val sensorData = snapshot.getValue(SensorData::class.java)
                    if (sensorData != null) {
                        Log.d(TAG, "✅ Datos recibidos: $sensorData")
                        onDataReceived(sensorData)
                    } else {
                        Log.w(TAG, "⚠️ Datos nulos recibidos de Firebase")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error parseando datos", e)
                    onError("Error: ${e.message}")
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "❌ Error en lectura de Firebase", error.toException())
                onError("Error: ${error.message}")
            }
        })
        
        return myRef
    }
    
    /**
     * Leer datos del sensor una sola vez
     */
    fun readSensorDataOnce(
        onDataReceived: (SensorData) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        val myRef = database.getReference(SENSOR_DATA_PATH)
        
        myRef.get()
            .addOnSuccessListener { snapshot ->
                try {
                    val sensorData = snapshot.getValue(SensorData::class.java)
                    if (sensorData != null) {
                        Log.d(TAG, "✅ Datos recibidos (once): $sensorData")
                        onDataReceived(sensorData)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error parseando datos", e)
                    onError("Error: ${e.message}")
                }
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "❌ Error leyendo de Firebase", error)
                onError("Error: ${error.message}")
            }
    }
    
    /**
     * Activar/Desactivar alarma en Arduino
     */
    fun setAlarmEnabled(
        enabled: Boolean,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val control = ActuatorControl(
            enabled = enabled,
            intensity = if (enabled) 100 else 0,
            lastUpdate = System.currentTimeMillis(),
            mode = "remote"
        )
        
        writeActuatorControl(control, onSuccess, onError)
    }
    
    /**
     * Configurar intensidad del buzzer
     */
    fun setBuzzerIntensity(
        intensity: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val control = ActuatorControl(
            enabled = true,
            intensity = intensity.coerceIn(0, 100),
            lastUpdate = System.currentTimeMillis(),
            mode = "manual"
        )
        
        writeActuatorControl(control, onSuccess, onError)
    }
}
