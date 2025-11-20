package com.alarmasensores.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alarmasensores.app.ui.screens.auth.LoginScreen
import com.alarmasensores.app.ui.theme.AlarmaSensoresTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlarmaSensoresTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(
                        onLoginClick = { email, password ->
                            // TODO: Implementar lógica de login
                        },
                        onCreateAccountClick = {
                            // TODO: Navegar a pantalla de registro
                        },
                        onForgotPasswordClick = {
                            // TODO: Navegar a pantalla de recuperación de contraseña
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AlarmaSensoresTheme {
        LoginScreen()
    }
}
