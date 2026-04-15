package com.squeakgames.wyldore.engine.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CompanionStateTest {

    @Test
    fun deepRest_hasLowestFrequency() {
        assertEquals(150f, CompanionState.DEEP_REST.baseFrequency, 0.001f)
    }

    @Test
    fun distress_hasHighestFrequency() {
        assertEquals(800f, CompanionState.DISTRESS.baseFrequency, 0.001f)
    }

    @Test
    fun distress_fastestBreathing() {
        assertEquals(18, CompanionState.DISTRESS.breathingBpm)
    }

    @Test
    fun deepRest_slowestBreathing() {
        assertEquals(4, CompanionState.DEEP_REST.breathingBpm)
    }

    @Test
    fun distress_shortestCrossfade() {
        assertEquals(3f, CompanionState.DISTRESS.crossfadeDurationSec, 0.001f)
    }

    @Test
    fun deepRest_longestCrossfade() {
        assertEquals(8f, CompanionState.DEEP_REST.crossfadeDurationSec, 0.001f)
    }

    @Test
    fun allStates_havePositiveFrequency() {
        CompanionState.values().forEach { state ->
            assertTrue("${state.name} frequency must be positive", state.baseFrequency > 0f)
        }
    }

    @Test
    fun allStates_haveOvertones() {
        CompanionState.values().forEach { state ->
            assertTrue("${state.name} must have overtones", state.overtones.isNotEmpty())
        }
    }
}
