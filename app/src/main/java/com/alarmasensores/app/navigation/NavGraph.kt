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
            DashboardPlaceholder(
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
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onSettingsClick) {
                Text("Ir a Configuración")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onHistoryClick) {
                Text("Ir a Historial")
            }
        }
    }
}

@Composable
fun SettingsPlaceholder(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Configuración",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBackClick) {
                Text("Volver")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onLogoutClick) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
fun HistoryPlaceholder(
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Historial de Detecciones",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBackClick) {
                Text("Volver")
            }
        }
    }
}
