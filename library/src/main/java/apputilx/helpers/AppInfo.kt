package apputilx.helpers

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

internal object AppInfo {

    fun packageName(context: Context): String =
        context.packageName

    fun appName(context: Context): String {
        val label = context.packageManager.getApplicationLabel(context.applicationInfo)
        return label.toString()
    }

    fun versionName(context: Context): String =
        packageInfo(context).versionName.orEmpty()

    fun versionCode(context: Context): Long {
        val info = packageInfo(context)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            info.versionCode.toLong()
        }
    }

    fun installerPackageName(context: Context): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.packageManager
                .getInstallSourceInfo(context.packageName)
                .installingPackageName
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getInstallerPackageName(context.packageName)
        }
    }

    fun isDebuggable(context: Context): Boolean {
        return context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            packageInfo(context.packageManager, packageName)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun packageInfo(context: Context): PackageInfo =
        packageInfo(context.packageManager, context.packageName)

    private fun packageInfo(packageManager: PackageManager, packageName: String): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
        }
    }
}
