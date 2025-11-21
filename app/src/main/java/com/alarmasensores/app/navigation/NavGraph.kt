package com.alarmasensores.app.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alarmasensores.app.ui.screens.auth.ForgotPasswordScreen
import com.alarmasensores.app.ui.screens.auth.LoginScreen
import com.alarmasensores.app.ui.screens.auth.RegisterScreen
import com.alarmasensores.app.ui.screens.dashboard.DashboardScreen
import com.alarmasensores.app.ui.screens.settings.SettingsScreen
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
        
        // Pantalla de Historial (placeholder)
        composable(Screen.History.route) {
            // TODO: Implementar HistoryScreen
            HistoryPlaceholder(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

// Placeholders temporales para las pantallas que aún no se implementan
@Composable
fun DashboardPlaceholder(
    onSettingsClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    androidx.compose.material3.Surface(
        modifier = androidx.compose.ui.Modifier.fillMaxSize()
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            androidx.compose.material3.Text(
                text = "Dashboard",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            androidx.compose.material3.Button(onClick = onSettingsClick) {
                androidx.compose.material3.Text("Ir a Configuración")
            }
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
            androidx.compose.material3.Button(onClick = onHistoryClick) {
                androidx.compose.material3.Text("Ir a Historial")
            }
        }
    }
}

@Composable
fun SettingsPlaceholder(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    androidx.compose.material3.Surface(
        modifier = androidx.compose.ui.Modifier.fillMaxSize()
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            androidx.compose.material3.Text(
                text = "Configuración",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            androidx.compose.material3.Button(onClick = onBackClick) {
                androidx.compose.material3.Text("Volver")
            }
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
            androidx.compose.material3.Button(onClick = onLogoutClick) {
                androidx.compose.material3.Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
fun HistoryPlaceholder(
    onBackClick: () -> Unit
) {
    androidx.compose.material3.Surface(
        modifier = androidx.compose.ui.Modifier.fillMaxSize()
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            androidx.compose.material3.Text(
                text = "Historial de Detecciones",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            androidx.compose.material3.Button(onClick = onBackClick) {
                androidx.compose.material3.Text("Volver")
            }
        }
    }
}
