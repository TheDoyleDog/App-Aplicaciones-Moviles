package com.alarmasensores.app.data.model

/**
 * Modelo de datos para los sensores Arduino
 * Basado en los ejemplos de Firebase proporcionados
 */
data class SensorData(
    val motionDetected: Boolean = false,
    val distance: Float = 0f,
    val buzzerActive: Boolean = false,
    val ledActive: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    // Constructor sin parámetros requerido por Firebase
    constructor() : this(false, 0f, false, false, System.currentTimeMillis())
    
    /**
     * Verifica si hay alguna alerta activa
     */
    fun hasAlert(): Boolean {
        return motionDetected || buzzerActive
    }
    
    /**
     * Retorna el estado del sistema como texto
     */
    fun getSystemStatus(): String {
        return when {
            motionDetected && buzzerActive -> "¡Alerta! Movimiento detectado"
            motionDetected -> "Movimiento detectado"
            buzzerActive -> "Alarma activada"
            else -> "Sistema normal"
        }
    }
}

/**
 * Modelo para control de actuadores (envío a Arduino)
 * Basado en los ejemplos de Firebase proporcionados
 */
data class ActuatorControl(
    var enabled: Boolean = false,
    var intensity: Int = 0,
    var lastUpdate: Long = System.currentTimeMillis(),
    var mode: String = "manual"
) {
    // Constructor sin parámetros requerido por Firebase
    constructor() : this(false, 0, System.currentTimeMillis(), "manual")
}
