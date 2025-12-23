package com.squeakgames.wyldore

import android.content.Context
import com.squeakgames.wyldore.health.HealthConnectBridge
import com.squeakgames.wyldore.engine.EcsWorld
import com.squeakgames.wyldore.audio.SpatialPanner

/**
 * Manual dependency container.
 *
 * Keeps wiring local and explicit: no DI framework, no service locator leak.
 * The privacy contract (docs/PRIVACY.md) rests on the fact that every dependency
 * in this graph runs on-device — there is no network client and no telemetry
 * surface wired in here, by design.
 */
class AppContainer(context: Context) {
    val world: EcsWorld = EcsWorld()
    val panner: SpatialPanner = SpatialPanner()
    val health: HealthConnectBridge = HealthConnectBridge(context)
}