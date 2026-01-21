package com.squeakgames.wyldore.sensor

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SensorCollectorService : Service() {

    private val _microphoneRms = MutableStateFlow(0f)
    val microphoneRms: StateFlow<Float> = _microphoneRms.asStateFlow()

    private val _stepCadence = MutableStateFlow(0f)
    val stepCadence: StateFlow<Float> = _stepCadence.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, SensorNotificationManager.createNotification(this))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
    }

    fun updateMicrophoneRms(rms: Float) { _microphoneRms.value = rms }
    fun updateStepCadence(cadence: Float) { _stepCadence.value = cadence }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "sensor_collector"
    }
}
