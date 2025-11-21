package com.alarmasensores.app.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.alarmasensores.app.MainActivity

/**
 * Helper para gestión de notificaciones push
 */
class NotificationHelper(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID = "alarm_notifications"
        private const val CHANNEL_NAME = "Alarma de Seguridad"
        private const val CHANNEL_DESCRIPTION = "Notificaciones de detección de movimiento"
        private const val NOTIFICATION_ID_MOTION = 1001
        private const val NOTIFICATION_ID_ALARM = 1002
    }
    
    init {
        createNotificationChannel()
    }
    
    /**
     * Crear canal de notificaciones (requerido para Android 8.0+)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Mostrar notificación de movimiento detectado
     */
    fun showMotionDetectedNotification(timestamp: Long) {
        if (!hasNotificationPermission()) {
            return
        }
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Movimiento Detectado")
            .setContentText("Se ha detectado movimiento en el área monitoreada")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_MOTION, notification)
    }
    
    /**
     * Mostrar notificación de alarma activada
     */
    fun showAlarmActivatedNotification() {
        if (!hasNotificationPermission()) {
            return
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setContentTitle("🔒 Alarma Activada")
            .setContentText("El sistema de seguridad está activo")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_ALARM, notification)
    }
    
    /**
     * Mostrar notificación de alarma desactivada
     */
    fun showAlarmDeactivatedNotification() {
        if (!hasNotificationPermission()) {
            return
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("🔓 Alarma Desactivada")
            .setContentText("El sistema de seguridad está inactivo")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_ALARM, notification)
    }
    
    /**
     * Mostrar notificación personalizada
     */
    fun showCustomNotification(
        title: String,
        message: String,
        notificationId: Int = NOTIFICATION_ID_MOTION
    ) {
        if (!hasNotificationPermission()) {
            return
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
    
    /**
     * Cancelar todas las notificaciones
     */
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
    
    /**
     * Verificar si tenemos permiso de notificaciones
     */
    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // No se requiere permiso en versiones anteriores
        }
    }
    
    /**
     * Verificar si las notificaciones están habilitadas
     */
    fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}
