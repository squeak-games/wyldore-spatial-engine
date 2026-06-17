package com.squeakgames.wyldore.engine

import org.junit.Assert.assertEquals
import org.junit.Test

class BondProgressionTest {

    @Test
    fun startsAsStranger() { assertEquals(BondTier.STRANGER, BondProgression().currentTier()) }

    @Test
    fun after2Hours_becomesFamiliar() {
        val bp = BondProgression(); bp.recordCalmMinutes(121)
        assertEquals(BondTier.FAMILIAR, bp.currentTier())
    }

    @Test
    fun after8Hours_becomesCompanion() {
        val bp = BondProgression(); bp.recordCalmMinutes(8 * 60)
        assertEquals(BondTier.COMPANION, bp.currentTier())
    }

    @Test
    fun after72Hours_becomesSymbiote() {
        val bp = BondProgression(); bp.recordCalmMinutes(72 * 60)
        assertEquals(BondTier.SYMBIOTE, bp.currentTier())
    }

    @Test
    fun progress_startsAtZero() { assertEquals(0f, BondProgression().progressToNext(), 0.001f) }

    @Test
    fun reset_returnsToStranger() {
        val bp = BondProgression(); bp.recordCalmMinutes(72 * 60); bp.reset()
        assertEquals(BondTier.STRANGER, bp.currentTier())
    }
}
