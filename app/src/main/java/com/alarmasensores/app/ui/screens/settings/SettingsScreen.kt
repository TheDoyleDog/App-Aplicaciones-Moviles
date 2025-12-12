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
    onClearHistory: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var scheduleFrom by remember(config.scheduleFrom) { mutableStateOf(config.scheduleFrom) }
    var scheduleTo by remember(config.scheduleTo) { mutableStateOf(config.scheduleTo) }
    var detectionDistance by remember(config.detectionDistance) { mutableStateOf(config.detectionDistance.toFloat()) }
    var selectedSound by remember(config.alarmSound) { mutableStateOf(config.alarmSound) }
    var showSoundDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    
    // Time Picker State
    val context = androidx.compose.ui.platform.LocalContext.current
    val calendar = java.util.Calendar.getInstance()
    
    fun showTimePicker(initialTime: String, onTimeSelected: (String) -> Unit) {
        val parts = initialTime.split(":")
        val hour = if (parts.size == 2) parts[0].toIntOrNull() ?: 12 else 12
        val minute = if (parts.size == 2) parts[1].toIntOrNull() ?: 0 else 0
        
        android.app.TimePickerDialog(
            context,
            { _, h, m ->
                val formattedTime = String.format("%02d:%02d", h, m)
                onTimeSelected(formattedTime)
            },
            hour,
            minute,
            true // 24 hour format
        ).show()
    }
    
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
                                    showTimePicker(scheduleFrom) { newTime ->
                                        scheduleFrom = newTime
                                        onConfigChange(config.copy(scheduleFrom = newTime))
                                    }
                                }
                            )
                            
                            Divider()
                            
                            // To Time
                            SettingsItem(
                                icon = Icons.Default.Update,
                                title = "Hasta",
                                value = scheduleTo,
                                onClick = {
                                    showTimePicker(scheduleTo) { newTime ->
                                        scheduleTo = newTime
                                        onConfigChange(config.copy(scheduleTo = newTime))
                                    }
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
                            
                            // Slider (50cm a 400cm)
                            Slider(
                                value = detectionDistance,
                                onValueChange = { detectionDistance = it },
                                onValueChangeFinished = {
                                    onConfigChange(config.copy(detectionDistance = detectionDistance.toInt()))
                                },
                                valueRange = 50f..400f,
                                steps = 34, // Saltos de ~10cm
                                colors = SliderDefaults.colors(
                                    thumbColor = PrimaryBlue,
                                    activeTrackColor = PrimaryBlue
                                )
                            )
                            
                            // Display value
                            Text(
                                text = "${detectionDistance.toInt()} cm",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PrimaryBlue,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
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
                        Column {
                            // Clear History Button
                            Button(
                                onClick = { showClearHistoryDialog = true },
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
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Eliminar Historial",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                            
                            Divider()

                            // Logout Button
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
    }
    
    // Sound Selection Dialog
    if (showSoundDialog) {
        AlarmSoundDialog(
            currentSound = selectedSound,
            onSoundSelected = { sound ->
                selectedSound = sound
                onConfigChange(config.copy(alarmSound = sound))
                showSoundDialog = false
            },
            onDismiss = { showSoundDialog = false }
        )
    }
    
    // Clear History Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("¿Eliminar Historial?") },
            text = { Text("Esta acción eliminará todos los eventos de detección registrados. No se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearHistory()
                        showClearHistoryDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = WarningRed)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancelar")
                }
            }
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
