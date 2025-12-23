package com.squeakgames.wyldore

import android.app.Application

class WyldoreApp : Application() {
    val container: AppContainer = AppContainer(this)
}