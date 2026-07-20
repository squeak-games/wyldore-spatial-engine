package com.squeakgames.wyldore

import android.content.Context
import com.squeakgames.wyldore.audio.SpatialPanner
import com.squeakgames.wyldore.engine.BondProgression
import com.squeakgames.wyldore.engine.EcsWorld
import com.squeakgames.wyldore.engine.SceneCoreHost
import com.squeakgames.wyldore.engine.audio.SpatialAudioEngine
import com.squeakgames.wyldore.health.HealthConnectBridge
import com.squeakgames.wyldore.interaction.SootheGestureHandler
import com.squeakgames.wyldore.location.SanctuaryZoneManager
import com.squeakgames.wyldore.sensor.EnvironmentalStressIndex

/**
 * Manual dependency container, scoped to the application lifetime.
 *
 * Wiring is local and explicit: no DI framework, no service locator, no
 * global accessor. Every dependency runs on-device — there is no network
 * client and no telemetry surface wired in here, by design.
 */
class AppContainer(context: Context) {
    val world: EcsWorld by lazy { EcsWorld() }
    val panner: SpatialPanner by lazy { SpatialPanner() }
    val health: HealthConnectBridge by lazy { HealthConnectBridge(context) }

    val sanctuaryZoneManager: SanctuaryZoneManager by lazy {
        SanctuaryZoneManager(context)
    }

    val environmentalStressIndex: EnvironmentalStressIndex by lazy {
        EnvironmentalStressIndex()
    }

    val sootheGestureHandler: SootheGestureHandler by lazy {
        SootheGestureHandler()
    }

    val bondProgression: BondProgression by lazy {
        BondProgression(context)
    }

    val spatialAudioEngine: SpatialAudioEngine by lazy {
        SpatialAudioEngine(panner)
    }

    val sceneHost: SceneCoreHost by lazy {
        SceneCoreHost(world, panner)
    }
}