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
import kotlinx.coroutines.flow.catch

/**
 * ViewModel para el estado y configuración de la alarma
 */
class AlarmViewModel : ViewModel() {
    
    private val repository = com.alarmasensores.app.data.repository.FirebaseRepository()
    
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
    
    private var monitoringJob: kotlinx.coroutines.Job? = null

    init {
        // Intentar iniciar monitoreo al crear, pero no crashear si falla
        startMonitoring()
    }

    fun startMonitoring() {
        // Cancelar monitoreo anterior si existe
        monitoringJob?.cancel()
        
        monitoringJob = viewModelScope.launch {
            // Estado de la Alarma
            launch {
                try {
                    repository.getAlarmState()
                        .catch { e -> 
                            // Log error or ignore if just "no user"
                            println("Error monitoring alarm state: ${e.message}")
                        }
                        .collect { state ->
                            _alarmState.value = state
                        }
                } catch (e: Exception) {
                    println("Error collecting alarm state: ${e.message}")
                }
            }
            
            // Configuración
            launch {
                try {
                    repository.getAlarmConfig()
                        .catch { e -> 
                             println("Error monitoring config: ${e.message}")
                        }
                        .collect { config ->
                            _alarmConfig.value = config
                        }
                } catch (e: Exception) {
                    println("Error collecting config: ${e.message}")
                }
            }
            
            // Historial
            launch {
                try {
                    repository.getDetectionHistory()
                        .catch { e -> 
                             println("Error monitoring history: ${e.message}")
                        }
                        .collect { events ->
                            _detectionEvents.value = events
                        }
                } catch (e: Exception) {
                    println("Error collecting history: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Activar/Desactivar alarma
     */
    fun toggleAlarm() {
        viewModelScope.launch {
            val newState = _alarmState.value.copy(
                enabled = !_alarmState.value.enabled,
                lastUpdate = System.currentTimeMillis()
            )
            // Actualización optimista
            _alarmState.value = newState
            
            try {
                repository.saveAlarmState(newState)
            } catch (e: Exception) {
                // Revertir si falla
                _alarmState.value = _alarmState.value.copy(enabled = !newState.enabled)
            }
        }
    }
    
    /**
     * Actualizar configuración
     */
    fun updateConfig(config: AlarmConfig) {
        viewModelScope.launch {
            // Actualización optimista
            _alarmConfig.value = config
            
            try {
                repository.saveAlarmConfig(config)
            } catch (e: Exception) {
                // Manejar error
            }
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
                val event = DetectionEvent(
                    id = "event_${System.currentTimeMillis()}",
                    timestamp = data.timestamp,
                    message = "Movimiento Detectado"
                )
                addDetectionEvent(event)
            }
        }
    }
    
    /**
     * Agregar evento de detección al historial
     */
    private fun addDetectionEvent(event: DetectionEvent) {
        viewModelScope.launch {
            try {
                repository.addDetectionEvent(event)
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }
    
    /**
     * Cargar historial de detecciones
     * (Ya se hace automáticamente en el init con el Flow)
     */
    fun loadDetectionHistory() {
        // No es necesario hacer nada explícito si usamos Flow
    }
    
    /**
     * Limpiar historial
     */
    fun clearHistory() {
        viewModelScope.launch {
            try {
                repository.clearHistory()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }
}
