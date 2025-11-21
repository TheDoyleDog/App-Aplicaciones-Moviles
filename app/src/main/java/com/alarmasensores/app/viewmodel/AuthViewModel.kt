package com.alarmasensores.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alarmasensores.app.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para autenticación
 */
class AuthViewModel : ViewModel() {
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * Iniciar sesión
     */
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // TODO: Implementar lógica de autenticación real
                // Por ahora, simulamos un login exitoso
                val user = User(
                    id = "user_${System.currentTimeMillis()}",
                    email = email,
                    fullName = "Usuario Demo"
                )
                _currentUser.value = user
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al iniciar sesión"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Registrar nuevo usuario
     */
    fun register(fullName: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // TODO: Implementar lógica de registro real
                // Por ahora, simulamos un registro exitoso
                val user = User(
                    id = "user_${System.currentTimeMillis()}",
                    email = email,
                    fullName = fullName
                )
                _currentUser.value = user
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al registrar usuario"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Recuperar contraseña
     */
    fun resetPassword(email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // TODO: Implementar lógica de recuperación real
                // Por ahora, simulamos éxito
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al enviar instrucciones"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Cerrar sesión
     */
    fun logout() {
        _currentUser.value = null
        _errorMessage.value = null
    }
    
    /**
     * Limpiar mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
