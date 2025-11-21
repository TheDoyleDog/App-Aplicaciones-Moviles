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
    
    private val repository = com.alarmasensores.app.data.repository.FirebaseRepository()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        // Verificar si ya hay usuario logueado
        _currentUser.value = repository.getCurrentUser()
    }
    
    /**
     * Iniciar sesión
     */
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val user = repository.login(email, password)
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
                val user = repository.register(email, password, fullName)
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
                repository.resetPassword(email)
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
        repository.logout()
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
