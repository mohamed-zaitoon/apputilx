package mz.example

import android.app.Application
import android.content.Context
import apputilx.*

class ExampleApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        AppUtils.initialize(this)
        registerActivityLifecycleCallbacks(AppUtils.activityTracker)

        
    }
}

