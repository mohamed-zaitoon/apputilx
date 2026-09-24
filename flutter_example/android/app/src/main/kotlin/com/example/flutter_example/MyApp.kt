package com.example.flutter_example

import android.app.Application
import apputilx.Utils

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.initialize(this)
    }
}
