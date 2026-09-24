package apputilx.helpers

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

internal object Device {

    fun manufacturer(): String = Build.MANUFACTURER
    fun model(): String = Build.MODEL
    fun brand(): String = Build.BRAND
    fun sdk(): Int = Build.VERSION.SDK_INT
    fun androidVersion(): String = Build.VERSION.RELEASE
    fun supportedAbis(): List<String> = Build.SUPPORTED_ABIS.toList()

    fun deviceName(): String {
        return listOf(Build.MANUFACTURER, Build.MODEL)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .trim()
    }

    fun isTablet(context: Context): Boolean {
        val screenLayout = context.resources.configuration.screenLayout and
            Configuration.SCREENLAYOUT_SIZE_MASK
        return screenLayout >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    fun isEmulator(): Boolean {
        val fingerprint = Build.FINGERPRINT.lowercase(Locale.US)
        val model = Build.MODEL.lowercase(Locale.US)
        val manufacturer = Build.MANUFACTURER.lowercase(Locale.US)
        val brand = Build.BRAND.lowercase(Locale.US)
        val device = Build.DEVICE.lowercase(Locale.US)
        val product = Build.PRODUCT.lowercase(Locale.US)

        return fingerprint.startsWith("generic") ||
            fingerprint.contains("emulator") ||
            model.contains("google_sdk") ||
            model.contains("emulator") ||
            model.contains("android sdk built for") ||
            manufacturer.contains("genymotion") ||
            brand.startsWith("generic") && device.startsWith("generic") ||
            product == "google_sdk"
    }
}
