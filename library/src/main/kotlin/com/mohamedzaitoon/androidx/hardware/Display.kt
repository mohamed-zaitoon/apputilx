package com.mohamedzaitoon.androidx.hardware

import android.content.Context
import android.content.res.Configuration
import com.mohamedzaitoon.androidx.AppUtilX

object Display {

    fun isPortrait(context: Context = AppUtilX.ctx()): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    fun isLandscape(context: Context = AppUtilX.ctx()): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    fun getScreenWidthDp(context: Context = AppUtilX.ctx()): Int {
        return context.resources.configuration.screenWidthDp
    }

    fun getScreenHeightDp(context: Context = AppUtilX.ctx()): Int {
        return context.resources.configuration.screenHeightDp
    }
}
