package mz.example

import android.app.Application
import android.content.Context
import hrm.utils.*

class ExampleApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        AppUtils.initialize(this)
        registerActivityLifecycleCallbacks(AppUtils.activityTracker)

        
    }
}
