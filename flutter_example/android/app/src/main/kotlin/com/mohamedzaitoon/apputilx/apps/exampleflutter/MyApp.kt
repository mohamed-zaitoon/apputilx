package com.mohamedzaitoon.apputilx.apps.exampleflutter

import android.app.Application
import com.mohamedzaitoon.apputilx.core.AppUtilX

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppUtilX.initialize(this)
    }
}
