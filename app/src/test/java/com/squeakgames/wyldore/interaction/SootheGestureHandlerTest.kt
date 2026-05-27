package com.squeakgames.wyldore.interaction

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SootheGestureHandlerTest {

    @Test
    fun startsNull() { assertNull(SootheGestureHandler().lastGesture.value) }

    @Test
    fun tap_setsGestureAndDamping() {
        val h = SootheGestureHandler()
        h.handleGesture(SootheGestureHandler.SootheGesture.TAP)
        assertEquals(SootheGestureHandler.SootheGesture.TAP, h.lastGesture.value)
        assertTrue(h.esiDampingActive.value)
    }

    @Test
    fun damping_expiresAfterDuration() {
        val h = SootheGestureHandler()
        h.handleGesture(SootheGestureHandler.SootheGesture.TAP)
        h.tick(20_000L)
        assertFalse(h.esiDampingActive.value)
    }

    @Test
    fun doubleTap_dampingLasts5Min() {
        val h = SootheGestureHandler()
        h.handleGesture(SootheGestureHandler.SootheGesture.DOUBLE_TAP)
        h.tick(299_000L)
        assertTrue(h.esiDampingActive.value)
        h.tick(2_000L)
        assertFalse(h.esiDampingActive.value)
    }

    @Test
    fun reset_clearsAll() {
        val h = SootheGestureHandler()
        h.handleGesture(SootheGestureHandler.SootheGesture.STROKE)
        h.reset()
        assertNull(h.lastGesture.value)
        assertFalse(h.esiDampingActive.value)
    }
}
