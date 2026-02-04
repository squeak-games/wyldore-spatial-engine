package com.squeakgames.wyldore.sensor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

class MicrophoneAmplitudeReader(private val context: Context) {

    private var audioRecord: AudioRecord? = null
    private var isRunning = false

    val sampleRate: Int get() = SAMPLE_RATE
    val bufferSize: Int get() = BUFFER_SIZE
    val windowMs: Float get() = BUFFER_SIZE.toFloat() / SAMPLE_RATE * 1000f

    fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED

    suspend fun readAmplitudeLoop(onRms: (Float) -> Unit) = coroutineScope {
        if (!hasPermission()) return@coroutineScope
        isRunning = true
        withContext(Dispatchers.IO) {
            val buffer = ShortArray(BUFFER_SIZE)
            val record = createAudioRecord()
            audioRecord = record
            if (record.state != AudioRecord.STATE_INITIALIZED) return@withContext
            record.startRecording()
            try {
                while (isActive && isRunning) {
                    val read = record.read(buffer, 0, BUFFER_SIZE)
                    if (read > 0) {
                        var sumSquares = 0.0
                        for (i in 0 until read) {
                            val sample = buffer[i].toDouble() / Short.MAX_VALUE
                            sumSquares += sample * sample
                        }
                        val rms = sqrt(sumSquares / read).toFloat().coerceIn(0f, 1f)
                        onRms(rms)
                    }
                }
            } finally {
                record.stop()
                record.release()
                audioRecord = null
            }
        }
    }

    fun stop() {
        isRunning = false
        audioRecord?.let {
            try { it.stop() } catch (_: IllegalStateException) { }
            it.release()
        }
        audioRecord = null
    }

    private fun createAudioRecord(): AudioRecord {
        val minBuffer = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        return AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            minBuffer.coerceAtLeast(BUFFER_SIZE * 2),
        )
    }

    companion object {
        const val SAMPLE_RATE = 44100
        const val BUFFER_SIZE = 2048
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
}
