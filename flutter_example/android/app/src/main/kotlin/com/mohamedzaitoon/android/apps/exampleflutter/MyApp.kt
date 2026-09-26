package com.mohamedzaitoon.android.apps.exampleflutter

import android.app.Application
import com.mohamedzaitoon.android.core.AppUtilX

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppUtilX.initialize(this)
    }
}
