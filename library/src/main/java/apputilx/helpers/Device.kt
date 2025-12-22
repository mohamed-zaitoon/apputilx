package apputilx.helpers

import android.os.Build

internal object Device {

    fun manufacturer(): String = Build.MANUFACTURER
    fun model(): String = Build.MODEL
    fun brand(): String = Build.BRAND
    fun sdk(): Int = Build.VERSION.SDK_INT
    fun androidVersion(): String = Build.VERSION.RELEASE
}