package com.squeakgames.wyldore

import android.app.Application
import android.content.Intent
import com.squeakgames.wyldore.sensor.SensorCollectorService

class WyldoreApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val intent = Intent(this, SensorCollectorService::class.java)
        startForegroundService(intent)
    }
}
