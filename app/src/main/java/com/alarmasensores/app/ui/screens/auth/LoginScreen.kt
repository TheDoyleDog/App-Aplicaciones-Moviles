package com.alarmasensores.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alarmasensores.app.ui.components.EmailTextField
import com.alarmasensores.app.ui.components.PasswordTextField
import com.alarmasensores.app.ui.components.PrimaryButton
import com.alarmasensores.app.ui.theme.PrimaryBlue

/**
 * Pantalla de Login
 * Diseño basado en login.html
 */
@Composable
fun LoginScreen(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onCreateAccountClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo/Icono
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = "App Logo",
                modifier = Modifier.size(80.dp),
                tint = PrimaryBlue
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Título
            Text(
                text = "Bienvenido de Vuelta",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtítulo
            Text(
                text = "Inicia sesión en tu cuenta",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Mensaje de Error
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Campo de Email/Usuario
            EmailTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email / Usuario",
                placeholder = "Ingresa tu email o usuario"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo de Contraseña
            PasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                placeholder = "Ingresa tu contraseña"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Enlace "Olvidé mi Contraseña"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onForgotPasswordClick) {
                    Text(
                        text = "¿Olvidé mi Contraseña?",
                        color = PrimaryBlue,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (isLoading) {
                CircularProgressIndicator(color = PrimaryBlue)
            } else {
                // Botón de Login
                PrimaryButton(
                    text = "Iniciar Sesión",
                    onClick = { onLoginClick(email, password) },
                    enabled = email.isNotBlank() && password.isNotBlank()
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Enlace "Crear Cuenta"
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes una cuenta? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onCreateAccountClick) {
                    Text(
                        text = "Crear Cuenta",
                        color = PrimaryBlue,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}
