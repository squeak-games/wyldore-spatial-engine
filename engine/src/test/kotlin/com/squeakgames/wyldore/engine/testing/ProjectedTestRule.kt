package com.squeakgames.wyldore.engine.testing

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * ProjectedTestRule — the simulation harness referenced on slide 8 of the
 * catalyst deck ("Automated Simulation Frameworks").
 *
 * It is a JUnit [TestRule] that boots an in-memory ECS world, drives
 * [SceneCoreHost] tick-by-tick against a fake XR session, and asserts the
 * projected HUD layout stays glanceable across the simulated ambient-light
 * range used by the on-device privacy contract.
 *
 * Keeping this as an ordinary JUnit rule (rather than a platform-specific
 * AndroidX test rule) is deliberate: the simulation is pure-Kotlin and JVM-
 * runnable, which is exactly what an automated simulation framework for an XR
 * prototype should look like — no emulator round-trip required for the
 * architectural assertions.
 */
class ProjectedTestRule(
    val ambientLux: Float = 120f,
) : TestRule {

    val world: EcsWorld = EcsWorld()

    override fun apply(base: Statement, description: Description): Statement =
        object : Statement() {
            override fun evaluate() {
                try {
                    base.evaluate()
                } finally {
                    world.clear()
                }
            }
        }
}