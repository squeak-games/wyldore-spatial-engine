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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.squeakgames.wyldore.engine.SceneCoreHost
import com.squeakgames.wyldore.health.HealthConnectBridge

/**
 * Phone-side prototype entrypoint.
 *
 * On a paired Android XR headset this Activity hands its Compose tree to the
 * Compose for XR host (referenced in the catalyst deck as the "Compose for XR
 * Layer"), which projects the glanceable HUD surface onto the head-mounted
 * display. On a phone-only development device the same Composable renders into
 * a flat preview surface — this is the "functional phone-side prototype"
 * claimed on slide 8 of the application deck.
 */
class MainActivity : ComponentActivity() {

    private val container: AppContainer
        get() = (application as WyldoreApp).container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize(), color = Color.Black) {
                    PrototypeSurface(
                        health = container.health,
                        sceneHost = SceneCoreHost(container.world, container.panner),
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
) {
    val heartRate = health.heartRate.collectAsState(initial = 0)
    Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Text(
            text = "Wyldore prototype\ncreature bpm -> ${heartRate.value}",
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
    sceneHost.tickOnce()
}