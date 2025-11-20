package com.alarmasensores.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alarmasensores.app.data.model.AlarmConfig
import com.alarmasensores.app.data.model.AlarmSound
import com.alarmasensores.app.ui.theme.PrimaryBlue
import com.alarmasensores.app.ui.theme.WarningRed

/**
 * Pantalla de Configuración/Ajustes
 * Diseño basado en ajustes.html
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    config: AlarmConfig = AlarmConfig(),
    onConfigChange: (AlarmConfig) -> Unit = {},
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var scheduleFrom by remember { mutableStateOf(config.scheduleFrom) }
    var scheduleTo by remember { mutableStateOf(config.scheduleTo) }
    var detectionDistance by remember { mutableStateOf(config.detectionDistance.toFloat()) }
    var selectedSound by remember { mutableStateOf(config.alarmSound) }
    var showSoundDialog by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Text(
                        text = "Ajustes",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
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
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Monitoring Schedule Section
                SettingsSection(title = "Horario de Monitoreo") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column {
                            // From Time
                            SettingsItem(
                                icon = Icons.Default.Schedule,
                                title = "Desde",
                                value = scheduleFrom,
                                onClick = {
                                    // TODO: Mostrar time picker
                                }
                            )
                            
                            Divider()
                            
                            // To Time
                            SettingsItem(
                                icon = Icons.Default.Update,
                                title = "Hasta",
                                value = scheduleTo,
                                onClick = {
                                    // TODO: Mostrar time picker
                                }
                            )
                        }
                    }
                }
                
                // Sensor Settings Section
                SettingsSection(title = "Configuración de Sensores") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Detection Distance
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SocialDistance,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(8.dp)
                                )
                                
                                Text(
                                    text = "Distancia de Detección",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Slider
                            Slider(
                                value = detectionDistance,
                                onValueChange = { detectionDistance = it },
                                valueRange = 0f..10f,
                                steps = 9,
                                colors = SliderDefaults.colors(
                                    thumbColor = PrimaryBlue,
                                    activeTrackColor = PrimaryBlue
                                )
                            )
                            
                            // Labels
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Baja",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Media",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Alta",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Alarm Sound
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        SettingsItem(
                            icon = Icons.Default.VolumeUp,
                            title = "Sonido de Alarma",
                            value = selectedSound,
                            onClick = { showSoundDialog = true },
                            showArrow = true
                        )
                    }
                }
                
                // Account Section
                SettingsSection(title = "Cuenta") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Button(
                            onClick = onLogoutClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = WarningRed
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Cerrar Sesión",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Sound Selection Dialog
    if (showSoundDialog) {
        AlarmSoundDialog(
            currentSound = selectedSound,
            onSoundSelected = { sound ->
                selectedSound = sound
                showSoundDialog = false
            },
            onDismiss = { showSoundDialog = false }
        )
    }
}

/**
 * Sección de configuración con título
 */
@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        content()
    }
}

/**
 * Item de configuración clickeable
 */
@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit,
    showArrow: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(40.dp).padding(8.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = PrimaryBlue
                )
                if (showArrow) {
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Diálogo para seleccionar el sonido de alarma
 */
@Composable
fun AlarmSoundDialog(
    currentSound: String,
    onSoundSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Sonido") },
        text = {
            Column {
                AlarmSound.values().forEach { sound ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentSound == sound.name,
                            onClick = { onSoundSelected(sound.name) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = sound.displayName)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
