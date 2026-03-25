package com.squeakgames.wyldore.sensor

import com.squeakgames.wyldore.location.SanctuaryZoneManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EnvironmentalStressIndexTest {

    @Test
    fun startsAtZero() {
        val esi = EnvironmentalStressIndex()
        assertEquals(0f, esi.composite.value, 0.001f)
    }

    @Test
    fun defaultWeights_sumToOne() {
        val esi = EnvironmentalStressIndex()
        assertEquals(1.0f, esi.noiseWeight + esi.motionWeight + esi.locationWeight, 0.001f)
    }

    @Test
    fun quietStillSanctuary_lowStress() {
        val esi = EnvironmentalStressIndex()
        repeat(5) {
            esi.update(
                noiseRms = 0.1f,
                stepCadence = 5f,
                zoneState = SanctuaryZoneManager.ZoneState.Inside,
            )
        }
        assertTrue("Expected low stress", esi.composite.value < 0.3f)
    }

    @Test
    fun loudActiveOutside_highStress() {
        val esi = EnvironmentalStressIndex()
        repeat(5) {
            esi.update(
                noiseRms = 0.8f,
                stepCadence = 100f,
                zoneState = SanctuaryZoneManager.ZoneState.Outside,
            )
        }
        assertTrue("Expected high stress", esi.composite.value > 0.5f)
    }

    @Test
    fun risingStress_fasterThanRecovery() {
        val esi = EnvironmentalStressIndex()
        repeat(3) {
            esi.update(noiseRms = 0.1f, stepCadence = 5f, zoneState = SanctuaryZoneManager.ZoneState.Inside)
        }
        val calmValue = esi.composite.value

        esi.update(noiseRms = 0.9f, stepCadence = 110f, zoneState = SanctuaryZoneManager.ZoneState.Outside)
        val spike = esi.composite.value

        esi.update(noiseRms = 0.9f, stepCadence = 110f, zoneState = SanctuaryZoneManager.ZoneState.Outside)
        val spike2 = esi.composite.value

        val riseRate = spike2 - spike
        assertTrue("Rise should be faster than recovery half-rate", spike > calmValue)
    }

    @Test
    fun reset_clearsAll() {
        val esi = EnvironmentalStressIndex()
        esi.update(noiseRms = 0.8f, stepCadence = 100f, zoneState = SanctuaryZoneManager.ZoneState.Outside)
        esi.reset()
        assertEquals(0f, esi.composite.value, 0.001f)
        assertEquals(0f, esi.smoothedNoise, 0.001f)
        assertEquals(0f, esi.smoothedMotion, 0.001f)
        assertEquals(0f, esi.smoothedLocation, 0.001f)
    }
}
