package mz.example

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import hrm.AppUtils
import mz.example.R
import android.content.Context

class ExampleActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppUtils.attachBaseContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppUtils.initialize(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        // لتغيير اللغة أثناء التشغيل:
        AppUtils.setLanguage("fr")
       // recreate()
    }
}
