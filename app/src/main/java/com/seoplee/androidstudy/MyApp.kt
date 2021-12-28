package com.seoplee.androidstudy

import android.app.Application
import android.content.Context

class MyApp : Application() {

    init {
        appContext = this
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}