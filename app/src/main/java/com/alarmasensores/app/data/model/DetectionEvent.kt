package com.alarmasensores.app.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Modelo de datos para eventos de detección
 */
data class DetectionEvent(
    val id: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val message: String = "Movimiento Detectado",
    val sensorType: SensorType = SensorType.MOTION,
    val distance: Float? = null // Solo para sensor de distancia
) {
    /**
     * Retorna la fecha formateada
     */
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Retorna la hora formateada
     */
    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Retorna el día relativo (Hoy, Ayer, fecha)
     */
    fun getRelativeDay(): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val oneDayMillis = 24 * 60 * 60 * 1000
        
        return when {
            diff < oneDayMillis -> "Hoy"
            diff < 2 * oneDayMillis -> "Ayer"
            else -> getFormattedDate()
        }
    }
}

/**
 * Tipos de sensores
 */
enum class SensorType {
    MOTION,    // HC-SR501
    DISTANCE   // HC-SR04
}
