package com.squeakgames.wyldore.sensor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object SensorNotificationManager {

    private const val CHANNEL_NAME = "Sensor Collector"
    private const val CHANNEL_DESC = "Wyldore companion sensor monitoring"

    fun createNotification(context: Context): Notification {
        ensureChannel(context)
        return NotificationCompat.Builder(context, SensorCollectorService.CHANNEL_ID)
            .setContentTitle("Wyldore")
            .setContentText("Companion is listening")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                SensorCollectorService.CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW,
            ).apply { description = CHANNEL_DESC }
            manager.createNotificationChannel(channel)
        }
    }
}
