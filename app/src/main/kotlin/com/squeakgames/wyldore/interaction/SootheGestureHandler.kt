package com.squeakgames.wyldore.interaction

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SootheGestureHandler {

    private val _lastGesture = MutableStateFlow<SootheGesture?>(null)
    val lastGesture: StateFlow<SootheGesture?> = _lastGesture.asStateFlow()

    private val _esiDampingActive = MutableStateFlow(false)
    val esiDampingActive: StateFlow<Boolean> = _esiDampingActive.asStateFlow()

    private var dampingTimerMs: Long = 0L

    fun handleGesture(gesture: SootheGesture) {
        _lastGesture.value = gesture
        dampingTimerMs = gesture.dampingDurationMs
        _esiDampingActive.value = true
    }

    fun tick(deltaMs: Long) {
        if (dampingTimerMs > 0) {
            dampingTimerMs -= deltaMs
            if (dampingTimerMs <= 0) {
                dampingTimerMs = 0
                _esiDampingActive.value = false
            }
        }
    }

    fun reset() {
        _lastGesture.value = null
        _esiDampingActive.value = false
        dampingTimerMs = 0
    }

    enum class SootheGesture(
        val label: String,
        val esiDampingPercent: Float,
        val dampingDurationMs: Long,
    ) {
        TAP("Brief tap", 0f, 15_000L),
        DOUBLE_TAP("Double tap", 0f, 300_000L),
        LONG_PRESS("Long press", 0f, 0L),
        STROKE("Stroke forward", 0.30f, 30_000L),
    }
}
