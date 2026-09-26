package com.mohamedzaitoon.apputilx.apps.examplekotlin

import android.app.Application
import com.mohamedzaitoon.apputilx.core.AppUtilX

class ExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppUtilX.initialize(this)
    }
}
