package com.squeakgames.wyldore.engine.testing

import com.squeakgames.wyldore.engine.EcsWorld
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

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
