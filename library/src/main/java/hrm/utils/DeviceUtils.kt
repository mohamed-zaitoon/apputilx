package hrm.utils

import android.os.Build

internal object DeviceUtils {

    fun manufacturer(): String = Build.MANUFACTURER
    fun model(): String = Build.MODEL
    fun brand(): String = Build.BRAND
    fun sdk(): Int = Build.VERSION.SDK_INT
    fun androidVersion(): String = Build.VERSION.RELEASE
}