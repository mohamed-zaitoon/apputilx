package apputilx.helpers

import android.content.Context
import android.content.res.Configuration

internal object Display {

    /**
     * Returns true if the device orientation is Portrait.
     */
    fun isPortrait(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    /**
     * Returns true if the device orientation is Landscape.
     */
    fun isLandscape(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    /**
     * Returns the screen width in density-independent pixels (dp).
     */
    fun getScreenWidthDp(context: Context): Int {
        return context.resources.configuration.screenWidthDp
    }

    /**
     * Returns the screen height in density-independent pixels (dp).
     */
    fun getScreenHeightDp(context: Context): Int {
        return context.resources.configuration.screenHeightDp
    }
}
