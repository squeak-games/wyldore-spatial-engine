package com.squeakgames.wyldore.sensor

import com.squeakgames.wyldore.location.SanctuaryZoneManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EnvironmentalStressIndex(
    noiseWeight: Float = 0.55f,
    motionWeight: Float = 0.30f,
    locationWeight: Float = 0.15f,
    private val decayConstant: Float = 0.3f,
) {

    init {
        require(noiseWeight + motionWeight + locationWeight in 0.99f..1.01f) {
            "Weights must sum to 1.0"
        }
    }

    val noiseWeight: Float = noiseWeight
    val motionWeight: Float = motionWeight
    val locationWeight: Float = locationWeight

    private val _composite = MutableStateFlow(0f)
    val composite: StateFlow<Float> = _composite.asStateFlow()

    var smoothedNoise: Float = 0f
        private set
    var smoothedMotion: Float = 0f
        private set
    var smoothedLocation: Float = 0f
        private set

    fun update(noiseRms: Float, stepCadence: Float, zoneState: SanctuaryZoneManager.ZoneState) {
        val rawNoise = noiseRms.coerceIn(0f, 1f)
        val rawMotion = (stepCadence / 120f).coerceIn(0f, 1f)
        val rawLocation = when (zoneState) {
            is SanctuaryZoneManager.ZoneState.Unknown -> 0.5f
            is SanctuaryZoneManager.ZoneState.Inside -> 0.3f
            is SanctuaryZoneManager.ZoneState.Outside -> 0.7f
            is SanctuaryZoneManager.ZoneState.Dwelling -> 0.1f
        }

        val current = _composite.value
        val isRising = (noiseRms > smoothedNoise) || (stepCadence > smoothedMotion * 120f)

        val alpha = if (isRising) decayConstant else decayConstant * 0.5f

        smoothedNoise = lerp(smoothedNoise, rawNoise, alpha)
        smoothedMotion = lerp(smoothedMotion, rawMotion, alpha)
        smoothedLocation = lerp(smoothedLocation, rawLocation, alpha)

        val next = smoothedNoise * noiseWeight +
            smoothedMotion * motionWeight +
            smoothedLocation * locationWeight

        val hysteresis = if (next > current) {
            next
        } else {
            current + (next - current) * 0.5f
        }

        _composite.value = hysteresis.coerceIn(0f, 1f)
    }

    fun reset() {
        smoothedNoise = 0f
        smoothedMotion = 0f
        smoothedLocation = 0f
        _composite.value = 0f
    }

    private fun lerp(from: Float, to: Float, alpha: Float): Float =
        from + (to - from) * alpha
}
