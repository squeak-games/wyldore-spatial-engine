package com.squeakgames.wyldore.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class BondTier(
    val label: String,
    val cumulativeCalmHoursRequired: Int,
    val harmonicPalette: Int,
) {
    STRANGER("Stranger", 0, 1),
    FAMILIAR("Familiar", 2, 2),
    COMPANION("Companion", 8, 3),
    BONDED("Bonded", 24, 4),
    SYMBIOTE("Symbiote", 72, 5),
}

class BondProgression {

    private val _tier = MutableStateFlow(BondTier.STRANGER)
    val tier: StateFlow<BondTier> = _tier.asStateFlow()

    private var totalCalmMinutes: Int = 0

    fun recordCalmMinutes(minutes: Int) {
        totalCalmMinutes += minutes
        _tier.value = resolveTier()
    }

    fun currentTier(): BondTier = _tier.value

    fun progressToNext(): Float {
        val current = _tier.value
        val next = nextTier(current) ?: return 1f
        val elapsed = totalCalmMinutes.coerceAtMost(next.cumulativeCalmHoursRequired * 60)
        val required = next.cumulativeCalmHoursRequired * 60
        return elapsed.toFloat() / required.toFloat()
    }

    fun reset() {
        totalCalmMinutes = 0
        _tier.value = BondTier.STRANGER
    }

    private fun resolveTier(): BondTier {
        val hours = totalCalmMinutes / 60
        return BondTier.values()
            .lastOrNull { hours >= it.cumulativeCalmHoursRequired }
            ?: BondTier.STRANGER
    }

    private fun nextTier(current: BondTier): BondTier? {
        val values = BondTier.values()
        val idx = values.indexOf(current)
        return values.getOrNull(idx + 1)
    }
}

data class SessionSummary(
    val sessionDurationMinutes: Int,
    val calmMinutes: Int,
    val stressMinutes: Int,
    val gesturesPerformed: Int,
    val bondProgression: BondTier,
)
