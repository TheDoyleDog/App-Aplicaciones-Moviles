package com.alarmasensores.app.data.model

/**
 * Modelo de datos para el usuario
 */
data class User(
    val id: String = "",
    val email: String = "",
    val fullName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
