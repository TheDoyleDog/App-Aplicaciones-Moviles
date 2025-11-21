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
 * Pantalla de Registro
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRegisterClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onLoginClick: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    val isFormValid = fullName.isNotBlank() && 
                     email.isNotBlank() && 
                     password.isNotBlank() && 
                     confirmPassword.isNotBlank() &&
                     password == confirmPassword
    
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
                imageVector = Icons.Default.Shield,
                contentDescription = "App Logo",
                modifier = Modifier.size(80.dp),
                tint = PrimaryBlue
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Título
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtítulo
            Text(
                text = "Regístrate para comenzar",
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
            
            // Campo de Nombre Completo
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Nombre Completo") },
                placeholder = { Text("Ingresa tu nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    focusedLabelColor = PrimaryBlue
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo de Email
            EmailTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "Ingresa tu email"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo de Contraseña
            PasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                placeholder = "Ingresa tu contraseña"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo de Confirmar Contraseña
            PasswordTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirmar Contraseña",
                placeholder = "Confirma tu contraseña"
            )
            
            // Mensaje de error si las contraseñas no coinciden
            if (confirmPassword.isNotBlank() && password != confirmPassword) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Las contraseñas no coinciden",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (isLoading) {
                CircularProgressIndicator(color = PrimaryBlue)
            } else {
                // Botón de Registro
                PrimaryButton(
                    text = "Registrarse",
                    onClick = { onRegisterClick(fullName, email, password, confirmPassword) },
                    enabled = isFormValid
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Enlace "Ya tienes cuenta"
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes una cuenta? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onLoginClick) {
                    Text(
                        text = "Iniciar Sesión",
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
