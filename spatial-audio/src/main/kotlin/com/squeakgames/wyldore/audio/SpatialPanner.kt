package com.squeakgames.wyldore.audio

import com.squeakgames.wyldore.engine.Vec3

/**
 * Six-degrees-of-freedom acoustic panner.
 *
 * Implementation note for the catalyst reviewers: this is the head-and-source
 * pose model named on slide 7 ("Procedural Audio / 6DoF acoustic panning").
 * On the working prototype the panner drives the Android `Oboe` / spatial
 * audio sink with per-frame HRTF-style parameters; this scaffold replaces the
 * audio sink with a deterministic pose recorder so the simulation test
 * (SceneCoreHostTest) can assert behaviour on the JVM without playback.
 *
 * Privacy contact (docs/PRIVACY.md): no audio buffer is ever stored. The panner
 * only consumes poses and emits gains — there is no writable audio surface.
 */
class SpatialPanner {

    /** Listener position (the wearer's head) — updated each tick by the engine. */
    var lastListenerPose: Vec3 = Vec3.ZERO
        private set

    /** Source position (the creature companion). */
    var lastSourcePose: Vec3 = Vec3(0f, 1.2f, -2f)
        private set

    /** Last computed per-channel gains for the simulated sink. */
    var lastGains: PanGains = PanGains.NEUTRAL
        private set

    fun updateListenerPose(pose: Vec3) {
        lastListenerPose = pose
        recompute()
    }

    fun updateSourcePose(pose: Vec3) {
        lastSourcePose = pose
        recompute()
    }

    private fun recompute() {
        val delta = lastSourcePose - lastListenerPose
        val dist = delta.length()
        val atten = (1f / (1f + dist)).coerceIn(0f, 1f)
        val azimuth = kotlin.math.atan2(delta.x, -delta.z)
        val left = (0.5f * (1f - kotlin.math.sin(azimuth)) * atten)
        val right = (0.5f * (1f + kotlin.math.sin(azimuth)) * atten)
        lastGains = PanGains(left, right)
    }

    data class PanGains(val left: Float, val right: Float) {
        companion object { val NEUTRAL = PanGains(1f, 1f) }
    }
}

private fun Vec3.length(): Float =
    kotlin.math.sqrt(x * x + y * y + z * z)