package com.squeakgames.wyldore.engine.audio

import com.squeakgames.wyldore.audio.SpatialPanner
import com.squeakgames.wyldore.audio.Vec3
import org.junit.Assert.assertEquals
import org.junit.Test

class SpatialAudioEngineTest {

    @Test
    fun startsInContentState() {
        val engine = SpatialAudioEngine(SpatialPanner())
        assertEquals(CompanionState.CONTENT, engine.currentState)
    }

    @Test
    fun setTargetState_changesTarget() {
        val engine = SpatialAudioEngine(SpatialPanner())
        engine.setTargetState(CompanionState.DISTRESS)
        assertEquals(CompanionState.DISTRESS, engine.targetState)
    }

    @Test
    fun deepRest_anchorsToRight() {
        val engine = SpatialAudioEngine(SpatialPanner())
        engine.tick(16L, Vec3.ZERO)
        assertEquals(CompanionState.CONTENT, engine.currentState)
    }

    @Test
    fun transition_completesAfterCrossfadeDuration() {
        val engine = SpatialAudioEngine(SpatialPanner())
        engine.setTargetState(CompanionState.DISTRESS)
        val crossfadeMs = (CompanionState.DISTRESS.crossfadeDurationSec * 1000f).toLong()
        engine.tick(crossfadeMs + 100L, Vec3.ZERO)
        assertEquals(CompanionState.DISTRESS, engine.currentState)
    }
}
