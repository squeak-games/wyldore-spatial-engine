package com.squeakgames.wyldore

import android.app.Application
import android.content.Intent
import com.squeakgames.wyldore.sensor.SensorCollectorService

class WyldoreApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        container = AppContainer(this)

        val intent = Intent(this, SensorCollectorService::class.java)
        startForegroundService(intent)
    }

    companion object {
        lateinit var instance: WyldoreApp
            private set
    }
}
