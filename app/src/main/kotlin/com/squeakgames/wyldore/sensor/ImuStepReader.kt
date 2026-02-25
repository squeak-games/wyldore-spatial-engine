package com.squeakgames.wyldore.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.sqrt

class ImuStepReader(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _stepCadence = MutableStateFlow(0f)
    val stepCadence: StateFlow<Float> = _stepCadence.asStateFlow()

    private val _movementRegularity = MutableStateFlow(1f)
    val movementRegularity: StateFlow<Float> = _movementRegularity.asStateFlow()

    private var lastStepTimestamp = 0L
    private var stepIntervals = mutableListOf<Float>()
    private var peakAccel = 0.0
    private var valleyAccel = 0.0
    private var isAbovePeak = false

    val accelerationEvents: Flow<FloatArray> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    trySend(event.values.clone())
                    processStep(event.values, event.timestamp)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
        accelerometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        awaitClose { sensorManager.unregisterListener(listener) }
    }

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(stepListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(stepListener)
    }

    private fun processStep(values: FloatArray, timestamp: Long) {
        val magnitude = sqrt(
            values[0].toDouble() * values[0].toDouble() +
            values[1].toDouble() * values[1].toDouble() +
            values[2].toDouble() * values[2].toDouble()
        )

        val gravity = 9.81
        if (magnitude > gravity + 2.0) {
            if (!isAbovePeak) {
                isAbovePeak = true
                peakAccel = magnitude
            } else if (magnitude > peakAccel) {
                peakAccel = magnitude
            }
        } else if (magnitude < gravity - 1.5) {
            if (isAbovePeak && peakAccel > gravity + 3.0) {
                valleyAccel = magnitude
                if (lastStepTimestamp > 0) {
                    val interval = (timestamp - lastStepTimestamp) / 1_000_000_000f
                    if (interval in 0.2f..2.0f) {
                        stepIntervals.add(interval)
                        if (stepIntervals.size > 10) stepIntervals.removeFirst()
                        val avgInterval = stepIntervals.average().toFloat()
                        _stepCadence.value = if (avgInterval > 0f) 60f / avgInterval else 0f
                        if (stepIntervals.size >= 3) {
                            val mean = stepIntervals.average()
                            val variance = stepIntervals.map { (it - mean) * (it - mean) }.average()
                            _movementRegularity.value = (1f - (sqrt(variance) / mean).toFloat().coerceIn(0f, 1f))
                        }
                    }
                }
                lastStepTimestamp = timestamp
                isAbovePeak = false
            }
        }
    }

    private val stepListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            processStep(event.values, event.timestamp)
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }
}
