package com.alarmasensores.app.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alarmasensores.app.data.model.DetectionEvent
import com.alarmasensores.app.data.model.SensorType
import com.alarmasensores.app.ui.theme.AccentOrange
import com.alarmasensores.app.ui.theme.PrimaryBlue
import com.alarmasensores.app.ui.theme.PrimaryDark

/**
 * Pantalla de Historial de Detecciones
 * Diseño basado en historialdedetecciones.html
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    events: List<DetectionEvent> = emptyList(),
    onBackClick: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("Hoy") }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    
    // Contexto para el DatePickerDialog
    val context = androidx.compose.ui.platform.LocalContext.current
    val calendar = java.util.Calendar.getInstance()
    
    // Función para mostrar el DatePicker
    fun showDatePicker() {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = java.util.Calendar.getInstance()
                selectedCal.set(year, month, dayOfMonth, 0, 0, 0)
                selectedCal.set(java.util.Calendar.MILLISECOND, 0)
                selectedDateMillis = selectedCal.timeInMillis
                selectedFilter = "Seleccionar"
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Lógica de filtrado
    val filteredEvents = remember(events, selectedFilter, selectedDateMillis) {
        val now = java.util.Calendar.getInstance()
        val eventCal = java.util.Calendar.getInstance()
        
        events.filter { event ->
            eventCal.timeInMillis = event.timestamp
            
            when (selectedFilter) {
                "Todos" -> true
                "Hoy" -> {
                    now.get(java.util.Calendar.YEAR) == eventCal.get(java.util.Calendar.YEAR) &&
                    now.get(java.util.Calendar.DAY_OF_YEAR) == eventCal.get(java.util.Calendar.DAY_OF_YEAR)
                }
                "Ayer" -> {
                    val yesterday = java.util.Calendar.getInstance()
                    yesterday.add(java.util.Calendar.DAY_OF_YEAR, -1)
                    yesterday.get(java.util.Calendar.YEAR) == eventCal.get(java.util.Calendar.YEAR) &&
                    yesterday.get(java.util.Calendar.DAY_OF_YEAR) == eventCal.get(java.util.Calendar.DAY_OF_YEAR)
                }
                "Últimos 7 días" -> {
                    val sevenDaysAgo = java.util.Calendar.getInstance()
                    sevenDaysAgo.add(java.util.Calendar.DAY_OF_YEAR, -7)
                    event.timestamp >= sevenDaysAgo.timeInMillis
                }
                "Seleccionar" -> {
                    selectedDateMillis?.let { selected ->
                        val selectedCal = java.util.Calendar.getInstance().apply { timeInMillis = selected }
                        selectedCal.get(java.util.Calendar.YEAR) == eventCal.get(java.util.Calendar.YEAR) &&
                        selectedCal.get(java.util.Calendar.DAY_OF_YEAR) == eventCal.get(java.util.Calendar.DAY_OF_YEAR)
                    } ?: true
                }
                else -> true
            }
        }
    }
    
    // Agrupar eventos filtrados por día
    val groupedEvents = filteredEvents.groupBy { it.getRelativeDay() }
    
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
                        text = "Historial de Detecciones",
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
                    containerColor = PrimaryDark
                )
            )
            
            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "Todos",
                    onClick = { selectedFilter = "Todos" },
                    label = { Text("Todos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                FilterChip(
                    selected = selectedFilter == "Hoy",
                    onClick = { selectedFilter = "Hoy" },
                    label = { Text("Hoy") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                
                FilterChip(
                    selected = selectedFilter == "Ayer",
                    onClick = { selectedFilter = "Ayer" },
                    label = { Text("Ayer") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                
                FilterChip(
                    selected = selectedFilter == "Últimos 7 días",
                    onClick = { selectedFilter = "Últimos 7 días" },
                    label = { Text("7 días") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                
                FilterChip(
                    selected = selectedFilter == "Seleccionar",
                    onClick = { showDatePicker() },
                    label = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(if (selectedFilter == "Seleccionar" && selectedDateMillis != null) {
                                val sdf = java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault())
                                sdf.format(java.util.Date(selectedDateMillis!!))
                            } else "Otro")
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
            
            // Content
            if (filteredEvents.isEmpty()) {
                // Empty State
                EmptyHistoryState()
            } else {
                // Event List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groupedEvents.forEach { (day, dayEvents) ->
                        item {
                            Text(
                                text = day,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        items(dayEvents) { event ->
                            DetectionEventItem(event = event)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Item de evento de detección
 */
@Composable
fun DetectionEventItem(event: DetectionEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icon
                Surface(
                    shape = CircleShape,
                    color = AccentOrange.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = AccentOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Info
                Column {
                    Text(
                        text = event.message,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = event.getFormattedTime(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Arrow
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Estado vacío cuando no hay detecciones
 */
@Composable
fun EmptyHistoryState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(80.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsOff,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Sin Detecciones",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "No hay eventos de detección para el período seleccionado.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Función helper para generar eventos de ejemplo
 */
fun getSampleEvents(): List<DetectionEvent> {
    val now = System.currentTimeMillis()
    val oneHour = 60 * 60 * 1000L
    val oneDay = 24 * oneHour
    
    return listOf(
        DetectionEvent(
            id = "1",
            timestamp = now - (2 * oneHour),
            message = "Movimiento Detectado",
            sensorType = SensorType.MOTION
        ),
        DetectionEvent(
            id = "2",
            timestamp = now - (6 * oneHour),
            message = "Movimiento Detectado",
            sensorType = SensorType.MOTION
        ),
        DetectionEvent(
            id = "3",
            timestamp = now - oneDay - (2 * oneHour),
            message = "Movimiento Detectado",
            sensorType = SensorType.MOTION
        ),
        DetectionEvent(
            id = "4",
            timestamp = now - (3 * oneDay),
            message = "Movimiento Detectado",
            sensorType = SensorType.MOTION
        )
    )
}
