package com.alarmasensores.app.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alarmasensores.app.ui.screens.auth.ForgotPasswordScreen
import com.alarmasensores.app.ui.screens.auth.LoginScreen
import com.alarmasensores.app.ui.screens.auth.RegisterScreen
import com.alarmasensores.app.ui.screens.dashboard.DashboardScreen
import com.alarmasensores.app.ui.screens.settings.SettingsScreen
import com.alarmasensores.app.ui.screens.history.HistoryScreen
import com.alarmasensores.app.data.model.AlarmConfig
import com.alarmasensores.app.data.model.AlarmState
import com.alarmasensores.app.data.model.DetectionEvent
import com.alarmasensores.app.data.model.SensorData
import com.alarmasensores.app.data.model.User
import com.alarmasensores.app.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Grafo de navegación de la aplicación
 * Gestiona todo el estado y la lógica de negocio para simplificar la arquitectura.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    val scope = rememberCoroutineScope()
    val repository = remember { FirebaseRepository() }

    // --- Estado Global ---
    var currentUser by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Estado de Alarma
    var alarmState by remember { mutableStateOf(AlarmState()) }
    var alarmConfig by remember { mutableStateOf(AlarmConfig()) }
    var detectionEvents by remember { mutableStateOf<List<DetectionEvent>>(emptyList()) }
    
    // Inicialización: Verificar usuario actual
    LaunchedEffect(Unit) {
        currentUser = repository.getCurrentUser()
    }

    // --- Efectos y Monitoreo ---
    // Monitorear datos de Firebase cuando hay un usuario logueado
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            // Navegar al dashboard si estamos en login
            if (navController.currentDestination?.route == Screen.Login.route) {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }

            // Iniciar monitoreo de datos
            launch {
                repository.getAlarmState()
                    .catch { e -> println("Error monitoring alarm state: ${e.message}") }
                    .collect { alarmState = it }
            }
            launch {
                repository.getAlarmConfig()
                    .catch { e -> println("Error monitoring config: ${e.message}") }
                    .collect { alarmConfig = it }
            }
            launch {
                repository.getDetectionHistory()
                    .catch { e -> println("Error monitoring history: ${e.message}") }
                    .collect { detectionEvents = it }
            }
        } else {
            // Limpiar estado al cerrar sesión
            alarmState = AlarmState()
            alarmConfig = AlarmConfig()
            detectionEvents = emptyList()
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla de Login
        composable(Screen.Login.route) {
            LoginScreen(
                isLoading = isLoading,
                errorMessage = errorMessage,
                onLoginClick = { email, password ->
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            currentUser = repository.login(email, password)
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Error al iniciar sesión"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                onCreateAccountClick = {
                    errorMessage = null
                    navController.navigate(Screen.Register.route)
                },
                onForgotPasswordClick = {
                    errorMessage = null
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }
        
        // Pantalla de Registro
        composable(Screen.Register.route) {
            RegisterScreen(
                isLoading = isLoading,
                errorMessage = errorMessage,
                onRegisterClick = { fullName, email, password, confirmPassword ->
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            currentUser = repository.register(email, password, fullName)
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Error al registrar usuario"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                onLoginClick = {
                    errorMessage = null
                    navController.popBackStack()
                }
            )
        }
        
        // Pantalla de Recuperación de Contraseña
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                isLoading = isLoading,
                errorMessage = errorMessage,
                onResetPasswordClick = { email ->
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            repository.resetPassword(email)
                            // Éxito: volver atrás
                            navController.popBackStack()
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Error al enviar instrucciones"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                onBackClick = {
                    errorMessage = null
                    navController.popBackStack()
                }
            )
        }
        
        // Pantalla de Dashboard
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                isAlarmEnabled = alarmState.enabled,
                onToggleAlarm = {
                    scope.launch {
                        val newState = alarmState.copy(
                            enabled = !alarmState.enabled,
                            lastUpdate = System.currentTimeMillis()
                        )
                        // Optimista
                        alarmState = newState
                        try {
                            repository.saveAlarmState(newState)
                        } catch (e: Exception) {
                            // Revertir
                            alarmState = alarmState.copy(enabled = !newState.enabled)
                        }
                    }
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onHistoryClick = {
                    navController.navigate(Screen.History.route)
                }
            )
        }
        
        // Pantalla de Configuración
        composable(Screen.Settings.route) {
            SettingsScreen(
                config = alarmConfig,
                onConfigChange = { newConfig ->
                    scope.launch {
                        // Optimista
                        alarmConfig = newConfig
                        try {
                            repository.saveAlarmConfig(newConfig)
                        } catch (e: Exception) {
                            // Manejar error si es necesario
                        }
                    }
                },
                onClearHistory = {
                    scope.launch {
                        try {
                            repository.clearHistory()
                            // El listener en LaunchedEffect actualizará la lista de eventos automáticamente
                        } catch (e: Exception) {
                            // Manejar error si es necesario
                        }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onLogoutClick = {
                    repository.logout()
                    currentUser = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        // Pantalla de Historial
        composable(Screen.History.route) {
            HistoryScreen(
                events = detectionEvents,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}


