package com.squeakgames.wyldore.engine

/**
 * Minimal entity-component-store.
 *
 * The deck's "Spatial Engine Paradigm (ECS)" claim leans on a per-frame
 * architectural loop that is data-oriented, allocation-light and trivially
 * threadable — the transition away from a 2D-object-graph renderer described
 * on slide 8. This file holds the data half of that loop: entities are plain
 * integer ids, components are open value classes with no behaviour, systems
 * (see [SceneCoreHost]) iterate component slices each tick.
 *
 * Scope of this scaffold: a single component type and a create+iterate API
 * sufficient to drive the prototype tick demonstrated by the `ProjectedTestRule`
 * harness in this module's test sources.
 */
class EcsWorld {

    private val positions: MutableMap<EntityId, Vec3> = LinkedHashMap()

    fun create(id: EntityId = EntityId.next(), position: Vec3 = Vec3.ZERO): Entity {
        positions[id] = position
        return Entity(id, position)
    }

    fun positionOf(id: EntityId): Vec3 = positions[id] ?: Vec3.ZERO
    fun moveTo(id: EntityId, to: Vec3) { positions[id] = to }
    fun entities(): List<Entity> = positions.entries.map { Entity(it.key, it.value) }

    fun clear() = positions.clear()
}

@JvmInline
value class EntityId(val value: Int) {
    companion object {
        private var counter = 0
        fun next(): EntityId = EntityId(counter++)
    }
}

data class Entity(val id: EntityId, val position: Vec3)

data class Vec3(val x: Float, val y: Float, val z: Float) {
    operator fun plus(o: Vec3) = Vec3(x + o.x, y + o.y, z + o.z)
    operator fun minus(o: Vec3) = Vec3(x - o.x, y - o.y, z - o.z)
    companion object {
        val ZERO = Vec3(0f, 0f, 0f)
    }
}