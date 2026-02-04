package com.squeakgames.wyldore.sensor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class MicrophoneAmplitudeReaderTest {

    @Test
    fun reader_hasExpectedBufferParameters() {
        val reader = MicrophoneAmplitudeReader(org.robolectric.RuntimeEnvironment.getApplication())
        assertEquals(44100, reader.sampleRate)
        assertEquals(2048, reader.bufferSize)
    }

    @Test
    fun reader_permissionNotGranted_byDefault() {
        val reader = MicrophoneAmplitudeReader(org.robolectric.RuntimeEnvironment.getApplication())
        assertFalse(reader.hasPermission())
    }

    @Test
    fun windowMs_isApproximately46ms() {
        val reader = MicrophoneAmplitudeReader(org.robolectric.RuntimeEnvironment.getApplication())
        val expectedMs = 2048f / 44100f * 1000f
        assertEquals(expectedMs, reader.windowMs, 0.5f)
    }
}
