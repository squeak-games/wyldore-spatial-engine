package com.squeakgames.wyldore.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Ambient biometric gating loop.
 *
 * Reads heart rate via the Android Health Connect API (the integration path
 * named on slide 2 of the catalyst deck) and exposes it as a StateFlow. The
 * creature companion uses this as an arousal / arousal-baseline input:
 * elevated heart rate nudges the creature's ambient soundscape toward
 * "calming" loops, low / stable HR toward "playful" loops.
 *
 * Privacy contract (docs/PRIVACY.md): this value never leaves the device. It is
 * consumed in-memory by the engine, never persisted, and never written to any
 * network surface — there is no network surface wired into [AppContainer].
 */
class HealthConnectBridge(context: Context) {

    private val client: HealthConnectClient =
        HealthConnectClient.getOrCreate(context)

    private val _heartRate = MutableStateFlow(0)
    val heartRate: StateFlow<Int> = _heartRate.asStateFlow()

    val requiredPermissions: Set<String> = setOf(
        HealthPermission.getReadPermissionFor(HeartRateRecord::class),
    )

    fun isAvailable(): Boolean = HealthConnectClient.isHealthConnectAvailable(context)

    /**
     * Prototype pulse — stand-in for the realisation flow that subscribes to
     * the HealthConnect change listener once OS consent has been granted. Kept
     * dependency-free so the phone-side preview can render without the HC
     * provider installed.
     */
    fun seedPrototypePulse(bpm: Int) {
        _heartRate.value = bpm
    }
}