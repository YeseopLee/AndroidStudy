package com.seoplee.androidstudy

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltAndroidApp
class MyApp : Application() {

    init {
        appContext = this
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}