package com.alarmasensores.app.navigation

/**
 * Definición de rutas de navegación
 */
sealed class Screen(val route: String) {
    // Autenticación
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    
    // Principales
    object Dashboard : Screen("dashboard")
    object Settings : Screen("settings")
    object History : Screen("history")
}
