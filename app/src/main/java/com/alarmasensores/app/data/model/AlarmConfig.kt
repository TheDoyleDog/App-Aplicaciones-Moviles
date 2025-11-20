package com.alarmasensores.app.data.model

/**
 * Modelo de datos para la configuración de la alarma
 */
data class AlarmConfig(
    val scheduleFrom: String = "22:00",
    val scheduleTo: String = "06:00",
    val detectionDistance: Int = 5, // Rango 0-10 (Baja=0-3, Media=4-6, Alta=7-10)
    val alarmSound: String = "Siren"
) {
    /**
     * Retorna el nivel de distancia como texto
     */
    fun getDistanceLevel(): String {
        return when (detectionDistance) {
            in 0..3 -> "Baja"
            in 4..6 -> "Media"
            in 7..10 -> "Alta"
            else -> "Media"
        }
    }
    
    /**
     * Retorna la distancia en metros (aproximado)
     */
    fun getDistanceInMeters(): Float {
        return detectionDistance * 0.5f // 0-5 metros
    }
}

/**
 * Opciones disponibles para el sonido de alarma
 */
enum class AlarmSound(val displayName: String) {
    SIREN("Sirena"),
    BEEP("Beep"),
    BELL("Campana"),
    BUZZER("Zumbador")
}
