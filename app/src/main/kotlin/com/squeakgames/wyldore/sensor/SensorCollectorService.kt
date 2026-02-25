package com.squeakgames.wyldore.sensor

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SensorCollectorService : Service() {

    private val _microphoneRms = MutableStateFlow(0f)
    val microphoneRms: StateFlow<Float> = _microphoneRms.asStateFlow()

    private val _stepCadence = MutableStateFlow(0f)
    val stepCadence: StateFlow<Float> = _stepCadence.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val microphoneReader by lazy { MicrophoneAmplitudeReader(this) }
    private val imuReader by lazy { ImuStepReader(this) }

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, SensorNotificationManager.createNotification(this))
        startMicrophoneReading()
        imuReader.start()
        scope.launch {
            imuReader.stepCadence.collect { _stepCadence.value = it }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        microphoneReader.stop()
        imuReader.stop()
        scope.cancel()
        super.onDestroy()
    }

    private fun startMicrophoneReading() {
        scope.launch {
            microphoneReader.readAmplitudeLoop { rms ->
                _microphoneRms.value = rms
            }
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "sensor_collector"
    }
}
