package com.mohamedzaitoon.android.core

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager


object Screen {

    private fun Context.getActivity(): Activity? {
        var ctx = this
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }
        return null
    }

    private fun Activity.hasSecureFlag(): Boolean {
        return (window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE) != 0
    }

    private fun applySecureFlag(activity: Activity, enable: Boolean) {
        val hasFlag = activity.hasSecureFlag()
        if (enable && !hasFlag) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else if (!enable && hasFlag) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    fun blockCapture(context: Context = AppUtilX.ctx()) {
        val activity = AppUtilX.act() ?: context.getActivity()
        activity?.let { applySecureFlag(it, true) }
    }

    fun unblockCapture(context: Context = AppUtilX.ctx()) {
        val activity = AppUtilX.act() ?: context.getActivity()
        activity?.let { applySecureFlag(it, false) }
    }

    fun isCaptureBlocked(activity: Activity? = AppUtilX.act()): Boolean {
        return activity?.hasSecureFlag() == true
    }
}
