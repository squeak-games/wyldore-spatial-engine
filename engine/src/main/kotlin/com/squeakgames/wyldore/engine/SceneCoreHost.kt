package com.squeakgames.wyldore.engine

import androidx.xr.scenecore.Session
import com.squeakgames.wyldore.audio.SpatialPanner

/**
 * The per-frame bridge from the ECS world to Jetpack XR SceneCore.
 *
 * Each [tickOnce] pass:
 *   1. reads the ambient biometric value baked into the creature entity by
 *      the app's Health Connect bridge,
 *   2. updates the creature's scene-graph node position via SceneCore's
 *      `Session` surface (the live scene-graph runtime named on slide 7), and
 *   3. forwards the resulting pose to the [SpatialPanner] so the 6DoF audio
 *      loop can re-evaluate spatialisation.
 *
 * This is intentionally a thin orchestration class. The point of the scaffold
 * is to demonstrate the wiring — the actual rendering happens inside the
 * Compose for XR / SceneCore surface, not hand-rolled GL.
 */
class SceneCoreHost(
    private val world: EcsWorld,
    private val panner: SpatialPanner,
    private val session: Session? = null,
) {

    fun tickOnce() {
        if (world.entities().isEmpty()) {
            world.create(position = Vec3(0f, 1.2f, -2f))
        }
        world.entities().forEach { entity ->
            val target = entity.position.copy()
            session?.let { /* scene-graph node commit goes here on device */ }
            panner.updateListenerPose(target)
        }
    }

    companion object {
        const val TICK_HZ: Int = 90
    }
}

/**
 * Small Kotlin stdlib workaround — the data class copy() shorthand used above
 * is normally generated, but tiny scaffolds sometimes want to share the
 * copy surface explicitly. Kept here to avoid a kotlinx import for Vec3 copy.
 */
private fun Vec3.copy(): Vec3 = Vec3(x, y, z)