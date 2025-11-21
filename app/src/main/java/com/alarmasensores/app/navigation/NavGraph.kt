package com.alarmasensores.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alarmasensores.app.ui.screens.auth.ForgotPasswordScreen
import com.alarmasensores.app.ui.screens.auth.LoginScreen
import com.alarmasensores.app.ui.screens.auth.RegisterScreen
import com.alarmasensores.app.ui.screens.dashboard.DashboardScreen
import com.alarmasensores.app.ui.screens.settings.SettingsScreen
import com.alarmasensores.app.ui.screens.history.HistoryScreen
import com.alarmasensores.app.ui.screens.history.getSampleEvents
import androidx.compose.runtime.*
import com.alarmasensores.app.data.model.AlarmConfig

/**
 * Grafo de navegación de la aplicación
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    val authViewModel: com.alarmasensores.app.viewmodel.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val alarmViewModel: com.alarmasensores.app.viewmodel.AlarmViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    // Observar estado de autenticación para navegación automática si ya está logueado
    val currentUser by authViewModel.currentUser.collectAsState()
    
    // Efecto para navegar al dashboard si hay usuario logueado al iniciar
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            // Iniciar monitoreo de datos cuando hay usuario
            alarmViewModel.startMonitoring()
            
            if (navController.currentDestination?.route == Screen.Login.route) {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla de Login
        composable(Screen.Login.route) {
            val isLoading by authViewModel.isLoading.collectAsState()
            val errorMessage by authViewModel.errorMessage.collectAsState()

            LoginScreen(
                isLoading = isLoading,
                errorMessage = errorMessage,
                onLoginClick = { email, password ->
                    authViewModel.login(email, password) {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onCreateAccountClick = {
                    authViewModel.clearError()
                    navController.navigate(Screen.Register.route)
                },
                onForgotPasswordClick = {
                    authViewModel.clearError()
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }
        
        // Pantalla de Registro
        composable(Screen.Register.route) {
            val isLoading by authViewModel.isLoading.collectAsState()
            val errorMessage by authViewModel.errorMessage.collectAsState()

            RegisterScreen(
                isLoading = isLoading,
                errorMessage = errorMessage,
                onRegisterClick = { fullName, email, password, confirmPassword ->
                    authViewModel.register(fullName, email, password) {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onLoginClick = {
                    authViewModel.clearError()
                    navController.popBackStack()
                }
            )
        }
        
        // Pantalla de Recuperación de Contraseña
        composable(Screen.ForgotPassword.route) {
            val isLoading by authViewModel.isLoading.collectAsState()
            val errorMessage by authViewModel.errorMessage.collectAsState()

            ForgotPasswordScreen(
                isLoading = isLoading,
                errorMessage = errorMessage,
                onResetPasswordClick = { email ->
                    authViewModel.resetPassword(email) {
                        // Mostrar mensaje de éxito o navegar atrás
                        navController.popBackStack()
                    }
                },
                onBackClick = {
                    authViewModel.clearError()
                    navController.popBackStack()
                }
            )
        }
        
        // Pantalla de Dashboard
        composable(Screen.Dashboard.route) {
            val alarmState by alarmViewModel.alarmState.collectAsState()
            
            DashboardScreen(
                isAlarmEnabled = alarmState.enabled,
                onToggleAlarm = {
                    alarmViewModel.toggleAlarm()
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onHistoryClick = {
                    navController.navigate(Screen.History.route)
                },
                onMenuClick = {
                    // TODO: Implementar menú lateral
                }
            )
        }
        
        // Pantalla de Configuración
        composable(Screen.Settings.route) {
            val config by alarmViewModel.alarmConfig.collectAsState()
            
            SettingsScreen(
                config = config,
                onConfigChange = { newConfig ->
                    alarmViewModel.updateConfig(newConfig)
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        // Pantalla de Historial
        composable(Screen.History.route) {
            val events by alarmViewModel.detectionEvents.collectAsState()
            
            HistoryScreen(
                events = events,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}


