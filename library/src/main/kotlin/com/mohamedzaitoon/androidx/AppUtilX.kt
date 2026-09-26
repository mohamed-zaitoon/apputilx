package com.mohamedzaitoon.androidx

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.mohamedzaitoon.androidx.net.Network

object AppUtilX {

    private var application: Application? = null

    fun initialize(app: Application) {
        application = app
        Network.initialize(app)
        registerLifecycle(app)
    }

    internal fun ctx(): Context {
        return application
            ?: throw IllegalStateException("AppUtilX must be initialized: AppUtilX.initialize(application)")
    }

    internal fun act(): Activity? {
        return currentActivity
    }

    private var currentActivity: Activity? = null

    private fun registerLifecycle(app: Application) {
        app.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(
                a: Activity,
                b: Bundle?
            ) {
                currentActivity = a
            }

            override fun onActivityStarted(a: Activity) {
                currentActivity = a
            }

            override fun onActivityResumed(a: Activity) {
                currentActivity = a
            }

            override fun onActivityPaused(a: Activity) {}
            override fun onActivityStopped(a: Activity) {}
            override fun onActivitySaveInstanceState(
                a: Activity,
                b: Bundle
            ) {
            }

            override fun onActivityDestroyed(a: Activity) {
                if (currentActivity == a) {
                    currentActivity = null
                }
            }
        })
    }

    fun log(tag: String, message: String) = Log.d(tag, message)
    fun logWarning(tag: String, message: String) = Log.w(tag, message)
    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
            showLogDialog("Error", tag, "$message\n\n${throwable.localizedMessage}")
        } else {
            Log.e(tag, message)
            showLogDialog("Error", tag, message)
        }
    }

    private fun showLogDialog(type: String, tag: String, message: String) {
        val activity = act() ?: return
        activity.runOnUiThread {
            AlertDialog.Builder(activity)
                .setTitle("$type : $tag")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", null)
                .show()
        }
    }
}

/** Backwards-compatible alias for Utils */
typealias Utils = AppUtilX
