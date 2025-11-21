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
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla de Login
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = { email, password ->
                    // TODO: Implementar lógica de login
                    // Por ahora navega directamente al dashboard
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onCreateAccountClick = {
                    navController.navigate(Screen.Register.route)
                },
                onForgotPasswordClick = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }
        
        // Pantalla de Registro
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterClick = { fullName, email, password, confirmPassword ->
                    // TODO: Implementar lógica de registro
                    // Por ahora navega al dashboard
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // Pantalla de Recuperación de Contraseña
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onResetPasswordClick = { email ->
                    // TODO: Implementar lógica de recuperación
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // Pantalla de Dashboard
        composable(Screen.Dashboard.route) {
            var isAlarmEnabled by remember { mutableStateOf(false) }
            
            DashboardScreen(
                isAlarmEnabled = isAlarmEnabled,
                onToggleAlarm = {
                    isAlarmEnabled = !isAlarmEnabled
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
            var config by remember { mutableStateOf(AlarmConfig()) }
            
            SettingsScreen(
                config = config,
                onConfigChange = { newConfig ->
                    config = newConfig
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        // Pantalla de Historial
        composable(Screen.History.route) {
            HistoryScreen(
                events = getSampleEvents(),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}


