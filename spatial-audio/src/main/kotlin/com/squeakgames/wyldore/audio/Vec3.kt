package com.squeakgames.wyldore.audio

data class Vec3(val x: Float, val y: Float, val z: Float) {
    operator fun minus(o: Vec3) = Vec3(x - o.x, y - o.y, z - o.z)
    companion object {
        val ZERO = Vec3(0f, 0f, 0f)
    }
}

fun Vec3.length(): Float =
    kotlin.math.sqrt(x * x + y * y + z * z)
