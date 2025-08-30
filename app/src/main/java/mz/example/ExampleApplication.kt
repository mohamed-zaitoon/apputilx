package mz.example

import android.app.Application
import mz.MzUtil
class ExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
	//	MzUtil.initialize(this)
		
        // Reassign the static instance to this subclass
    }
}
