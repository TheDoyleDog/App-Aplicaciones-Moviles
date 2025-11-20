package com.alarmasensores.app.data.model

/**
 * Modelo de datos para el estado de la alarma
 */
data class AlarmState(
    val enabled: Boolean = false,
    val lastUpdate: Long = System.currentTimeMillis()
) {
    /**
     * Retorna el texto del estado para mostrar en UI
     */
    fun getStatusText(): String {
        return if (enabled) "ACTIVADO" else "DESACTIVADO"
    }
    
    /**
     * Retorna el mensaje descriptivo del estado
     */
    fun getStatusMessage(): String {
        return if (enabled) {
            "El sistema de seguridad está activo."
        } else {
            "El sistema de seguridad está inactivo."
        }
    }
}
