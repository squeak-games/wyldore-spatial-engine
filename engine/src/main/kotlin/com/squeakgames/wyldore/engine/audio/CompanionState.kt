package com.squeakgames.wyldore.engine.audio

enum class CompanionState(
    val baseFrequency: Float,
    val overtones: List<Float>,
    val breathingBpm: Int,
    val crossfadeDurationSec: Float,
) {
    DEEP_REST(
        baseFrequency = 150f,
        overtones = listOf(1f, 0.5f, 0.25f),
        breathingBpm = 4,
        crossfadeDurationSec = 8f,
    ),
    CONTENT(
        baseFrequency = 280f,
        overtones = listOf(1f, 0.7f, 0.3f),
        breathingBpm = 6,
        crossfadeDurationSec = 5f,
    ),
    ALERT(
        baseFrequency = 440f,
        overtones = listOf(1f, 0.8f, 0.5f),
        breathingBpm = 10,
        crossfadeDurationSec = 4f,
    ),
    UNEASY(
        baseFrequency = 600f,
        overtones = listOf(1f, 0.9f, 0.6f, 0.3f),
        breathingBpm = 14,
        crossfadeDurationSec = 3f,
    ),
    DISTRESS(
        baseFrequency = 800f,
        overtones = listOf(1f, 0.95f, 0.7f, 0.5f, 0.2f),
        breathingBpm = 18,
        crossfadeDurationSec = 3f,
    ),
}
