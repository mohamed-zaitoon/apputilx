package com.mohamedzaitoon.apputilx.apps.examplekotlin

import android.app.Application
import com.mohamedzaitoon.apputilx.AppUtilX

class ExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppUtilX.initialize(this)
    }
}
