package com.mohamedzaitoon.example

import android.app.Application
import apputilx.Utils

class ExampleApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        Utils.initialize(this)
    }
}
