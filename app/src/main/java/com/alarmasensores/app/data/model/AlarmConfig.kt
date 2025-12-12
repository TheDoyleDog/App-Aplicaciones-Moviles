package com.alarmasensores.app.data.model

/**
 * Modelo de datos para la configuración de la alarma
 */
data class AlarmConfig(
    val scheduleFrom: String = "22:00",
    val scheduleTo: String = "06:00",
    val detectionDistance: Int = 100, // Distancia en cm (50cm - 400cm)
    val alarmSound: String = "Siren"
) {
    /**
     * Retorna el nivel de distancia como texto
     */
    fun getDistanceLevel(): String {
        return "${detectionDistance} cm"
    }
    
    /**
     * Retorna la distancia en metros
     */
    fun getDistanceInMeters(): Float {
        return detectionDistance / 100f
    }
}

/**
 * Opciones disponibles para el sonido de alarma
 */
enum class AlarmSound(val displayName: String) {
    SIREN("Sirena"),
    BEEP("Beep"),
    BELL("Campana"),
    BUZZER("Zumbador"),
    MARIO("Mario Bros")
}
