package com.squeakgames.wyldore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.squeakgames.wyldore.engine.SceneCoreHost
import com.squeakgames.wyldore.engine.audio.CompanionState
import com.squeakgames.wyldore.health.HealthConnectBridge

class MainActivity : ComponentActivity() {

    private val container by lazy { (application as WyldoreApp).container }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize(), color = Color.Black) {
                    PrototypeSurface(
                        health = container.health,
                        sceneHost = container.sceneHost,
                        esi = container.environmentalStressIndex,
                        audioEngine = container.spatialAudioEngine,
                        bondProgression = container.bondProgression,
                    )
                }
            }
        }
    }
}

@Composable
private fun PrototypeSurface(
    health: HealthConnectBridge,
    sceneHost: SceneCoreHost,
    esi: com.squeakgames.wyldore.sensor.EnvironmentalStressIndex,
    audioEngine: com.squeakgames.wyldore.engine.audio.SpatialAudioEngine,
    bondProgression: com.squeakgames.wyldore.engine.BondProgression,
) {
    val heartRate = health.heartRate.collectAsState(initial = 0)
    val esiValue = esi.composite.collectAsState(initial = 0f)
    val bondTier by bondProgression.tier.collectAsState()

    val companionState = remember(esiValue.value) {
        when {
            esiValue.value < 0.2f -> CompanionState.DEEP_REST
            esiValue.value < 0.4f -> CompanionState.CONTENT
            esiValue.value < 0.6f -> CompanionState.ALERT
            esiValue.value < 0.8f -> CompanionState.UNEASY
            else -> CompanionState.DISTRESS
        }
    }

    LaunchedEffect(companionState) {
        audioEngine.setTargetState(companionState)
    }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(16L)
            sceneHost.tickOnce()
            audioEngine.tick(16L, com.squeakgames.wyldore.audio.Vec3.ZERO)
            if (esiValue.value < 0.3f) {
                bondProgression.recordCalmMinutes(1)
            }
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Text(
            text = "Wyldore prototype\n" +
                "creature bpm -> ${heartRate.value}\n" +
                "esi -> ${"%.2f".format(esiValue.value)}\n" +
                "state -> ${companionState.name}\n" +
                "bond -> ${bondTier.label}",
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}
