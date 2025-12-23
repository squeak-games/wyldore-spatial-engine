package com.squeakgames.wyldore.engine

import com.squeakgames.wyldore.audio.SpatialPanner
import com.squeakgames.wyldore.engine.testing.ProjectedTestRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Smoke test for the engine module's per-frame loop.
 *
 * This is the simulation test the catalyst deck's slide 8 points at when it
 * claims "Automated Simulation Frameworks": run the tick loop on the JVM,
 * without a headset, and assert that the scene-graph bridge produces a
 * glanceable HUD pose after N ticks.
 */
class SceneCoreHostTest {

    @get:Rule
    val xr = ProjectedTestRule(ambientLux = 120f)

    @Test
    fun tickOnce_createsCreatureEntity_inFrontOfListener() {
        val world = xr.world
        val panner = SpatialPanner()
        val host = SceneCoreHost(world, panner, session = null)

        host.tickOnce()

        assertEquals(1, world.entities().size)
        val creature = world.entities().single()
        // The spatial anchor for the companion creature sits ~2m in front of
        // the listener origin — matches the 6DoF panning initial pose.
        assertEquals(0f, creature.position.x, 0.001f)
        assertEquals(1.2f, creature.position.y, 0.001f)
        assertEquals(-2f, creature.position.z, 0.001f)
    }

    @Test
    fun hud_staysGlanceable_acrossAmbientRange() {
        for (lux in floatArrayOf(50f, 120f, 400f)) {
            val layer = ProjectedLayer()
            assertTrue("poor glanceability at lux=$lux", layer.isGlanceable(lux))
        }
    }

    @Test
    fun spatialPanner_tracksCreaturePose_onEachTick() {
        val world = xr.world
        val panner = SpatialPanner()
        val host = SceneCoreHost(world, panner, session = null)

        repeat(3) { host.tickOnce() }

        assertEquals(1, world.entities().size)
        assertTrue(panner.lastListenerPose.z < 0f)
    }
}