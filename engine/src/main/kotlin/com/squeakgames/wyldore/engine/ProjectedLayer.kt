package com.squeakgames.wyldore.engine

/**
 * The "Compose for XR Layer" from slide 7 — a high-contrast, glanceable HUD
 * projected via the Jetpack XR Projected API.
 *
 * The Projected API lets an app render a 2D Compose surface onto a virtual
 * panel "projected" into the user's field of view (head-tracked, frame-tap
 * reactive). This class holds the layout intent for that panel: contrast
 * ratios, opacity caps, anchor bias. The actual Compose tree lives in the
 * `:app` module; this module only owns the spatial intent of the panel.
 */
data class ProjectedLayer(
    val anchor: Anchor = Anchor.SPATIAL_FIXED,
    val opacityCap: Float = 0.86f,
    val contrastFloor: Float = 7.0f,
    val headTracked: Boolean = true,
) {

    /**
     * Probe used by the simulation test harness — reports whether the panel
     * meets glanceability targets. (See `engine/src/test/...ProjectedTest*`.)
     */
    fun isGlanceable(ambientLux: Float): Boolean =
        contrastFloor >= MIN_CONTRAST_FLOOR && ambientLux >= MIN_AMBIENT_LUX

    companion object {
        const val MIN_CONTRAST_FLOOR = 7.0f
        const val MIN_AMBIENT_LUX = 50f
    }
}

enum class Anchor { SPATIAL_FIXED, HEAD_LOCKED, SURFACE_ATTACHED }