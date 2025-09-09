package hrm.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat

/**
 * Converted to Kotlin from the original Java CircleImageView.
 * Provides a circular image with backward-compatible shadow support.
 */
class CircleImageView(context: Context, color: Int) : AppCompatImageView(context) {

    companion object {
        private const val KEY_SHADOW_COLOR = 0x1E000000.toInt()
        private const val FILL_SHADOW_COLOR = 0x3D000000.toInt()

        // px constants (as floats in original Java)
        private const val X_OFFSET = 0f
        private const val Y_OFFSET = 1.75f
        private const val SHADOW_RADIUS = 3.5f
        private const val SHADOW_ELEVATION = 4
    }

    private var mListener: Animation.AnimationListener? = null
    private var mShadowRadius: Int = 0

    init {
        val density = context.resources.displayMetrics.density
        val shadowYOffset = (density * Y_OFFSET).toInt()
        val shadowXOffset = (density * X_OFFSET).toInt()

        mShadowRadius = (density * SHADOW_RADIUS).toInt()

        val circle: ShapeDrawable
        if (elevationSupported()) {
            circle = ShapeDrawable(OvalShape())
            // Elevation on Lollipop+ for native shadow
            ViewCompat.setElevation(this, SHADOW_ELEVATION * density)
        } else {
            // Pre-Lollipop: draw shadow via radial gradient
            val oval = OvalShadow(mShadowRadius)
            circle = ShapeDrawable(oval)
            setLayerType(View.LAYER_TYPE_SOFTWARE, circle.paint)
            circle.paint.setShadowLayer(
                mShadowRadius.toFloat(),
                shadowXOffset.toFloat(),
                shadowYOffset.toFloat(),
                KEY_SHADOW_COLOR
            )
            val padding = mShadowRadius
            // ensure inner image sits correctly within the shadow
            setPadding(padding, padding, padding, padding)
        }

        circle.paint.color = color
        // avoid deprecated ViewCompat.setBackground -> use background property
        background = circle
    }

    private fun elevationSupported(): Boolean =
        android.os.Build.VERSION.SDK_INT >= 21

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!elevationSupported()) {
            // add shadow padding to measured size for pre-L devices
            setMeasuredDimension(measuredWidth + mShadowRadius * 2, measuredHeight + mShadowRadius * 2)
        }
    }

    fun setAnimationListener(listener: Animation.AnimationListener?) {
        mListener = listener
    }

    override fun onAnimationStart() {
        super.onAnimationStart()
        // pass current animation (may be null) to listener
        mListener?.onAnimationStart(animation)
    }

    override fun onAnimationEnd() {
        super.onAnimationEnd()
        // pass current animation (may be null) to listener
        mListener?.onAnimationEnd(animation)
    }

    /**
     * Update the background color using a color resource id.
     */
    fun setBackgroundColorRes(colorRes: Int) {
        setBackgroundColor(ContextCompat.getColor(context, colorRes))
    }

    override fun setBackgroundColor(color: Int) {
        (background as? ShapeDrawable)?.paint?.color = color
    }

    /**
     * Inner OvalShape that draws the radial shadow for pre-Lollipop devices.
     */
    private inner class OvalShadow(private val shadowRadiusLocal: Int) : OvalShape() {
        private var mRadialGradient: RadialGradient? = null
        private val mShadowPaint = Paint()

        init {
            // initialize gradient with a safe diameter (will be updated in onResize)
            updateRadialGradient(0)
        }

        override fun onResize(width: Float, height: Float) {
            super.onResize(width, height)
            updateRadialGradient(width.toInt())
        }

        override fun draw(canvas: Canvas, paint: Paint) {
            val viewWidth = this@CircleImageView.width
            val viewHeight = this@CircleImageView.height
            // draw shadow circle
            canvas.drawCircle(viewWidth / 2f, viewHeight / 2f, viewWidth / 2f, mShadowPaint)
            // draw inner circle (actual content)
            canvas.drawCircle(
                viewWidth / 2f,
                viewHeight / 2f,
                viewWidth / 2f - shadowRadiusLocal,
                paint
            )
        }

        private fun updateRadialGradient(diameter: Int) {
            // center at diameter/2, radius = shadowRadiusLocal
            mRadialGradient = RadialGradient(
                diameter / 2f,
                diameter / 2f,
                shadowRadiusLocal.toFloat(),
                intArrayOf(FILL_SHADOW_COLOR, Color.TRANSPARENT),
                null,
                Shader.TileMode.CLAMP
            )
            mShadowPaint.shader = mRadialGradient
        }
    }
}