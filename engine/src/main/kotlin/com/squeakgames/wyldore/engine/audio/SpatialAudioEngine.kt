package com.squeakgames.wyldore.engine.audio

import com.squeakgames.wyldore.audio.SpatialPanner
import com.squeakgames.wyldore.audio.Vec3

class SpatialAudioEngine(
    private val panner: SpatialPanner,
) {

    var currentState: CompanionState = CompanionState.CONTENT
        private set
    var targetState: CompanionState = CompanionState.CONTENT
        private set

    private var transitionProgress: Float = 1f
    private var elapsedTransitionMs: Long = 0L

    fun setTargetState(state: CompanionState) {
        if (state != targetState) {
            targetState = state
            transitionProgress = 0f
            elapsedTransitionMs = 0L
        }
    }

    fun tick(deltaMs: Long, headPosition: Vec3) {
        if (transitionProgress < 1f) {
            elapsedTransitionMs += deltaMs
            transitionProgress = (elapsedTransitionMs / (targetState.crossfadeDurationSec * 1000f)).coerceAtMost(1f)
            if (transitionProgress >= 1f) {
                currentState = targetState
            }
        }

        val sourcePos = computeSpatialPosition(currentState, headPosition)
        panner.updateSourcePose(sourcePos)
        panner.updateListenerPose(headPosition)
    }

    private fun computeSpatialPosition(state: CompanionState, head: Vec3): Vec3 {
        return when (state) {
            CompanionState.DEEP_REST -> Vec3(0.3f, -0.2f, -2.0f)
            CompanionState.CONTENT -> Vec3(0.0f, 0.0f, -1.8f)
            CompanionState.ALERT -> Vec3(0.5f, 0.3f, -1.5f)
            CompanionState.UNEASY -> Vec3(-0.8f, 0.5f, -1.0f)
            CompanionState.DISTRESS -> Vec3(0.0f, 1.0f, -0.5f)
        }
    }
}
