package com.squeakgames.wyldore.engine

import android.content.Context
import android.content.SharedPreferences
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

class BondProgression(context: Context? = null) {

    private val prefs: SharedPreferences? =
        context?.getSharedPreferences("bond_progression", Context.MODE_PRIVATE)

    private val _tier = MutableStateFlow(BondTier.STRANGER)
    val tier: StateFlow<BondTier> = _tier.asStateFlow()

    private var totalCalmMinutes: Int = prefs?.getInt(PREF_CALM_MINUTES, 0) ?: 0

    init {
        _tier.value = resolveTier()
    }

    fun recordCalmMinutes(minutes: Int) {
        totalCalmMinutes += minutes
        persistCalmMinutes()
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
        persistCalmMinutes()
        _tier.value = BondTier.STRANGER
    }

    private fun persistCalmMinutes() {
        prefs?.edit()?.putInt(PREF_CALM_MINUTES, totalCalmMinutes)?.apply()
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

    companion object {
        private const val PREF_CALM_MINUTES = "total_calm_minutes"
    }
}

data class SessionSummary(
    val sessionDurationMinutes: Int,
    val calmMinutes: Int,
    val stressMinutes: Int,
    val gesturesPerformed: Int,
    val bondProgression: BondTier,
)
