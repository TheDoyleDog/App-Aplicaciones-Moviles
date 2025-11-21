package com.alarmasensores.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alarmasensores.app.data.model.AlarmConfig
import com.alarmasensores.app.data.model.AlarmState
import com.alarmasensores.app.data.model.DetectionEvent
import com.alarmasensores.app.data.model.SensorData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para el estado y configuración de la alarma
 */
class AlarmViewModel : ViewModel() {
    
    // Estado de la alarma
    private val _alarmState = MutableStateFlow(AlarmState())
    val alarmState: StateFlow<AlarmState> = _alarmState.asStateFlow()
    
    // Configuración de la alarma
    private val _alarmConfig = MutableStateFlow(AlarmConfig())
    val alarmConfig: StateFlow<AlarmConfig> = _alarmConfig.asStateFlow()
    
    // Datos del sensor
    private val _sensorData = MutableStateFlow(SensorData())
    val sensorData: StateFlow<SensorData> = _sensorData.asStateFlow()
    
    // Historial de detecciones
    private val _detectionEvents = MutableStateFlow<List<DetectionEvent>>(emptyList())
    val detectionEvents: StateFlow<List<DetectionEvent>> = _detectionEvents.asStateFlow()
    
    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    /**
     * Activar/Desactivar alarma
     */
    fun toggleAlarm() {
        viewModelScope.launch {
            val newState = _alarmState.value.copy(
                enabled = !_alarmState.value.enabled,
                lastUpdate = System.currentTimeMillis()
            )
            _alarmState.value = newState
            
            // TODO: Enviar estado al Arduino/Firebase
        }
    }
    
    /**
     * Actualizar configuración
     */
    fun updateConfig(config: AlarmConfig) {
        viewModelScope.launch {
            _alarmConfig.value = config
            
            // TODO: Guardar en DataStore y enviar al Arduino
        }
    }
    
    /**
     * Actualizar datos del sensor
     */
    fun updateSensorData(data: SensorData) {
        viewModelScope.launch {
            _sensorData.value = data
            
            // Si hay movimiento detectado, agregar al historial
            if (data.motionDetected) {
                addDetectionEvent(
                    DetectionEvent(
                        id = "event_${System.currentTimeMillis()}",
                        timestamp = data.timestamp,
                        message = "Movimiento Detectado"
                    )
                )
            }
        }
    }
    
    /**
     * Agregar evento de detección al historial
     */
    private fun addDetectionEvent(event: DetectionEvent) {
        val currentEvents = _detectionEvents.value.toMutableList()
        currentEvents.add(0, event) // Agregar al inicio
        _detectionEvents.value = currentEvents
        
        // TODO: Guardar en base de datos local
    }
    
    /**
     * Cargar historial de detecciones
     */
    fun loadDetectionHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // TODO: Cargar desde base de datos o Firebase
                // Por ahora, usar eventos de ejemplo
                _detectionEvents.value = generateSampleEvents()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Limpiar historial
     */
    fun clearHistory() {
        _detectionEvents.value = emptyList()
    }
    
    /**
     * Generar eventos de ejemplo
     */
    private fun generateSampleEvents(): List<DetectionEvent> {
        val now = System.currentTimeMillis()
        val oneHour = 60 * 60 * 1000L
        val oneDay = 24 * oneHour
        
        return listOf(
            DetectionEvent(
                id = "1",
                timestamp = now - (2 * oneHour),
                message = "Movimiento Detectado"
            ),
            DetectionEvent(
                id = "2",
                timestamp = now - (6 * oneHour),
                message = "Movimiento Detectado"
            ),
            DetectionEvent(
                id = "3",
                timestamp = now - oneDay - (2 * oneHour),
                message = "Movimiento Detectado"
            ),
            DetectionEvent(
                id = "4",
                timestamp = now - (3 * oneDay),
                message = "Movimiento Detectado"
            )
        )
    }
}
