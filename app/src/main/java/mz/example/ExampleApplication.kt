package mz.example

import android.app.Application
import android.content.Context
import hrm.utils.*

class ExampleApplication : Application() {


override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppUtils.attachBaseContext(newBase))
    }
    
    override fun onCreate() {
        super.onCreate()
        AppUtils.initialize(this)
        registerActivityLifecycleCallbacks(AppUtils.activityTracker)
        AppUtils.setLanguage("fr")
        
    }
}
