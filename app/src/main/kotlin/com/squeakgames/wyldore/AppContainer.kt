package com.squeakgames.wyldore

import android.content.Context
import com.squeakgames.wyldore.audio.SpatialPanner
import com.squeakgames.wyldore.engine.EcsWorld
import com.squeakgames.wyldore.health.HealthConnectBridge

/**
 * Manual dependency container, scoped to MainActivity.
 *
 * Wiring is local and explicit: no DI framework, no service locator, no
 * global accessor. Every dependency runs on-device — there is no network
 * client and no telemetry surface wired in here, by design.
 */
class AppContainer(context: Context) {
    val world: EcsWorld by lazy { EcsWorld() }
    val panner: SpatialPanner by lazy { SpatialPanner() }
    val health: HealthConnectBridge by lazy { HealthConnectBridge(context) }
}