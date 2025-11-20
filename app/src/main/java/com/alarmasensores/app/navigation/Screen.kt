package com.alarmasensores.app.navigation

/**
 * Definición de las pantallas de la aplicación
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Dashboard : Screen("dashboard")
    object Settings : Screen("settings")
    object History : Screen("history")
}

