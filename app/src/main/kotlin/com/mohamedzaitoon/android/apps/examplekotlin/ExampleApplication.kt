package com.mohamedzaitoon.android.apps.examplekotlin

import android.app.Application
import com.mohamedzaitoon.android.core.AppUtilX

class ExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppUtilX.initialize(this)
    }
}
