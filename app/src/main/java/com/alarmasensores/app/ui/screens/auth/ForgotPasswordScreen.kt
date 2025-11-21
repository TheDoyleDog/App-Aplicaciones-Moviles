package com.alarmasensores.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alarmasensores.app.ui.components.EmailTextField
import com.alarmasensores.app.ui.components.PrimaryButton
import com.alarmasensores.app.ui.theme.PrimaryBlue

/**
 * Pantalla de Recuperación de Contraseña
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onResetPasswordClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = { Text("Recuperar Contraseña") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
            
            // Contenido
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icono
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock Icon",
                    modifier = Modifier.size(80.dp),
                    tint = PrimaryBlue
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Título
                Text(
                    text = "¿Olvidaste tu Contraseña?",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Descripción
                Text(
                    text = "Ingresa tu email y te enviaremos instrucciones para restablecer tu contraseña.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
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
                
                // Campo de Email
                EmailTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    placeholder = "Ingresa tu email"
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (isLoading) {
                    CircularProgressIndicator(color = PrimaryBlue)
                } else {
                    // Botón de Enviar
                    PrimaryButton(
                        text = "Enviar Instrucciones",
                        onClick = {
                            onResetPasswordClick(email)
                            showSuccessMessage = true
                        },
                        enabled = email.isNotBlank()
                    )
                }
                
                // Mensaje de éxito
                if (showSuccessMessage) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = "✓ Se han enviado las instrucciones a tu email",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Enlace para volver al login
                TextButton(onClick = onBackClick) {
                    Text(
                        text = "Volver al Inicio de Sesión",
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
