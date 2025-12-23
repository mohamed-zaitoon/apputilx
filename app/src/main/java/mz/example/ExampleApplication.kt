package mz.example

import android.app.Application
import android.content.Context
import apputilx.*

class ExampleApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        Utils.initialize(this)
        registerActivityLifecycleCallbacks(Utils.activityTracker)

        
    }
}

