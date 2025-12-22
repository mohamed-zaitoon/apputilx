package apputilx.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.AbsListView
import android.widget.ListView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.core.view.NestedScrollingChild
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.NestedScrollingParent
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * SwipeRefreshLayout (Kotlin, adapted).
 *
 * Place in: library/src/main/java/apputilx/widget/SwipeRefreshLayout.kt
 *
 * Compatibility notes:
 * - Exposes `var isRefreshing` (property) so Kotlin code can use `swipe.isRefreshing = true`
 * - Exposes setOnRefreshListener(listener: OnRefreshListener?) where OnRefreshListener is a `fun interface`,
 *   so you can pass a lambda: swipe.setOnRefreshListener { /* refresh */ }
 */
class SwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: android.util.AttributeSet? = null
) : ViewGroup(context, attrs), NestedScrollingParent, NestedScrollingChild {

    companion object {
        const val LARGE = CircularProgressDrawable.LARGE
        const val DEFAULT = CircularProgressDrawable.DEFAULT
        const val DEFAULT_SLINGSHOT_DISTANCE = -1

        @VisibleForTesting
        const val CIRCLE_DIAMETER = 40

        @VisibleForTesting
        const val CIRCLE_DIAMETER_LARGE = 56

        private val LOG_TAG = SwipeRefreshLayout::class.java.simpleName

        private const val MAX_ALPHA = 255
        private const val STARTING_PROGRESS_ALPHA = (0.3f * MAX_ALPHA).toInt()

        private const val DECELERATE_INTERPOLATION_FACTOR = 2f
        private const val INVALID_POINTER = -1
        private const val DRAG_RATE = 0.5f

        private const val MAX_PROGRESS_ANGLE = 0.8f

        private const val SCALE_DOWN_DURATION = 150

        private const val ALPHA_ANIMATION_DURATION = 300

        private const val ANIMATE_TO_TRIGGER_DURATION = 200

        private const val ANIMATE_TO_START_DURATION = 200

        private const val CIRCLE_BG_LIGHT = 0xFFFAFAFA.toInt()
        private const val DEFAULT_CIRCLE_TARGET = 64

        private val LAYOUT_ATTRS = intArrayOf(android.R.attr.enabled)
    }

    // target view inside this layout
    private var mTarget: View? = null

    private var mListener: OnRefreshListener? = null
    private var mRefreshing = false
    private val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    private var mTotalDragDistance = -1f

    // nested scrolling helpers
    private var mTotalUnconsumed = 0f
    private val mNestedScrollingParentHelper = NestedScrollingParentHelper(this)
    private val mNestedScrollingChildHelper = NestedScrollingChildHelper(this)
    private val mParentScrollConsumed = IntArray(2)
    private val mParentOffsetInWindow = IntArray(2)
    private var mNestedScrollInProgress = false

    private var mMediumAnimationDuration: Int = 0
    var mCurrentTargetOffsetTop = 0

    private var mInitialMotionY = 0f
    private var mInitialDownY = 0f
    private var mInitialDownX = 0f
    private var mIsBeingDragged = false
    private var mActivePointerId = INVALID_POINTER
    var mScale = false

    private var mReturningToStart = false
    private val mDecelerateInterpolator: DecelerateInterpolator =
        DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)

    var mCircleView: CircleImageView
    private var mCircleViewIndex = -1

    protected var mFrom = 0
    var mStartingScale = 0f
    protected var mOriginalOffsetTop = 0
    var mSpinnerOffsetEnd = 0
    var mCustomSlingshotDistance = 0
    var mProgress: CircularProgressDrawable
    private var mScaleAnimation: Animation? = null
    private var mScaleDownAnimation: Animation? = null
    private var mAlphaStartAnimation: Animation? = null
    private var mAlphaMaxAnimation: Animation? = null
    private var mScaleDownToStartAnimation: Animation? = null
    var mNotify = false
    private var mCircleDiameter: Int = 0
    var mUsingCustomStart = false

    var shouldCancelDrag = false

    private var mChildScrollUpCallback: OnChildScrollUpCallback? = null

    // NOTE: mRefreshListener is initialized after helper methods are defined
    private lateinit var mRefreshListener: Animation.AnimationListener

    // ---------- helper methods ----------

    private fun setColorViewAlpha(targetAlpha: Int) {
        mCircleView.background?.alpha = targetAlpha
        mProgress.alpha = targetAlpha
    }

    /**
     * Java-style listener interface but declared as `fun interface` so Kotlin lambda works.
     */
    fun interface OnRefreshListener {
        fun onRefresh()
    }

    /**
     * Set listener (accepts null). Because OnRefreshListener is a `fun interface` you can pass a lambda:
     * swipe.setOnRefreshListener { ... }
     */
    fun setOnRefreshListener(listener: OnRefreshListener?) {
        mListener = listener
    }

    /**
     * Expose property `isRefreshing` so Kotlin callers can read/write as property:
     * swipe.isRefreshing = true
     */
    var isRefreshing: Boolean
        get() = mRefreshing
        set(value) {
            // behaviour matches classic SwipeRefreshLayout.setRefreshing(boolean)
            if (value && mRefreshing != value) {
                mRefreshing = value
                val endTarget = if (!mUsingCustomStart) {
                    mSpinnerOffsetEnd + mOriginalOffsetTop
                } else {
                    mSpinnerOffsetEnd
                }
                setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop)
                mNotify = false
                startScaleUpAnimation(mRefreshListener)
            } else {
                setRefreshingInternal(value, false)
            }
        }

    fun setDistanceToTriggerSync(distance: Int) {
        mTotalDragDistance = distance.toFloat()
    }

    private fun ensureTarget() {
        if (mTarget == null) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child !== mCircleView) {
                    mTarget = child
                    break
                }
            }
        }
    }

    fun getProgressCircleDiameter(): Int = mCircleDiameter

    fun canChildScrollUp(): Boolean {
        mChildScrollUpCallback?.let {
            return it.canChildScrollUp(this, mTarget)
        }
        val target = mTarget
        if (target is ListView) {
            if (target.firstVisiblePosition > 0) return true
            val firstChild = target.getChildAt(0) ?: return false
            return firstChild.top < target.paddingTop
        }
        return target?.canScrollVertically(-1) ?: false
    }

    fun setOnChildScrollUpCallback(callback: OnChildScrollUpCallback?) {
        mChildScrollUpCallback = callback
    }

    private fun isAnimationRunning(animation: Animation?): Boolean {
        return animation != null && animation.hasStarted() && !animation.hasEnded()
    }

    private fun setTargetOffsetTopAndBottom(offset: Int) {
        mCircleView.bringToFront()
        ViewCompat.offsetTopAndBottom(mCircleView, offset)
        mCurrentTargetOffsetTop = mCircleView.top
    }

    private fun moveToStart(interpolatedTime: Float) {
        val targetTop = (mFrom + ((mOriginalOffsetTop - mFrom) * interpolatedTime)).toInt()
        val offset = targetTop - mCircleView.top
        setTargetOffsetTopAndBottom(offset)
    }

    fun reset() {
        mCircleView.clearAnimation()
        mProgress.stop()
        mCircleView.visibility = View.GONE
        setColorViewAlpha(MAX_ALPHA)
        if (mScale) {
            setAnimationProgress(0f)
        } else {
            setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop)
        }
        mCurrentTargetOffsetTop = mCircleView.top
    }

    // ---------- end helper methods ----------

    // ---------- constructor init ----------
    init {
        mMediumAnimationDuration = resources.getInteger(android.R.integer.config_mediumAnimTime)
        setWillNotDraw(false)

        val metrics: DisplayMetrics = resources.displayMetrics
        mCircleDiameter = (CIRCLE_DIAMETER * metrics.density).toInt()

        // create circle view + progress
        mCircleView = CircleImageView(context, CIRCLE_BG_LIGHT)
        mProgress = CircularProgressDrawable(context)
        mProgress.setStyle(CircularProgressDrawable.DEFAULT)

        // default color scheme (three colors requested)
        mProgress.setColorSchemeColors(
            Color.parseColor("#FE2C55"),
            Color.parseColor("#69C9D0"),
            Color.parseColor("#EE1D52")
        )

        mCircleView.setImageDrawable(mProgress)
        mCircleView.visibility = View.GONE
        addView(mCircleView)

        setChildrenDrawingOrderEnabled(true)

        mSpinnerOffsetEnd = (DEFAULT_CIRCLE_TARGET * metrics.density).toInt()
        mTotalDragDistance = mSpinnerOffsetEnd.toFloat()

        mOriginalOffsetTop = -mCircleDiameter
        mCurrentTargetOffsetTop = mOriginalOffsetTop
        moveToStart(1.0f)

        setNestedScrollingEnabled(true)

        val a: TypedArray? = attrs?.let { context.obtainStyledAttributes(it, LAYOUT_ATTRS) }
        setEnabled(a?.getBoolean(0, true) ?: true)
        a?.recycle()

        // Now initialize the refresh listener (after helper funcs exist)
        mRefreshListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                // animation may be null — handle safely
                if (mRefreshing) {
                    mProgress.alpha = MAX_ALPHA
                    mProgress.start()
                    if (mNotify) {
                        mListener?.onRefresh()
                    }
                    mCurrentTargetOffsetTop = mCircleView.top
                } else {
                    reset()
                }
            }
        }
    }
    // ---------- end init ----------

    // ---------- public/interaction methods ----------

    // internal renamed to avoid JVM signature clashes with property
    private fun setRefreshingInternal(refreshing: Boolean, notify: Boolean) {
        if (mRefreshing != refreshing) {
            mNotify = notify
            ensureTarget()
            mRefreshing = refreshing
            if (mRefreshing) {
                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener)
            } else {
                startScaleDownAnimation(mRefreshListener)
            }
        }
    }

    private fun startScaleUpAnimation(listener: Animation.AnimationListener?) {
        mCircleView.visibility = View.VISIBLE
        mProgress.alpha = MAX_ALPHA
        mScaleAnimation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                setAnimationProgress(interpolatedTime)
            }
        }
        mScaleAnimation?.duration = mMediumAnimationDuration.toLong()
        if (listener != null) {
            mCircleView.setAnimationListener(listener)
        }
        mCircleView.clearAnimation()
        mCircleView.startAnimation(mScaleAnimation)
    }

    fun setAnimationProgress(progress: Float) {
        mCircleView.scaleX = progress
        mCircleView.scaleY = progress
    }

    private fun startScaleDownAnimation(listener: Animation.AnimationListener?) {
        mScaleDownAnimation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                setAnimationProgress(1 - interpolatedTime)
            }
        }
        mScaleDownAnimation?.duration = SCALE_DOWN_DURATION.toLong()
        mCircleView.setAnimationListener(listener)
        mCircleView.clearAnimation()
        mCircleView.startAnimation(mScaleDownAnimation)
    }

    private fun startProgressAlphaStartAnimation() {
        mAlphaStartAnimation = startAlphaAnimation(mProgress.alpha, STARTING_PROGRESS_ALPHA)
    }

    private fun startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mProgress.alpha, MAX_ALPHA)
    }

    private fun startAlphaAnimation(startingAlpha: Int, endingAlpha: Int): Animation {
        val alpha = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                mProgress.alpha =
                    (startingAlpha + ((endingAlpha - startingAlpha) * interpolatedTime)).toInt()
            }
        }
        alpha.duration = ALPHA_ANIMATION_DURATION.toLong()
        mCircleView.setAnimationListener(null)
        mCircleView.clearAnimation()
        mCircleView.startAnimation(alpha)
        return alpha
    }

    @Deprecated("Use setProgressBackgroundColorSchemeResource")
    fun setProgressBackgroundColor(@ColorRes colorRes: Int) {
        setProgressBackgroundColorSchemeResource(colorRes)
    }

    fun setProgressBackgroundColorSchemeResource(@ColorRes colorRes: Int) {
        setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context, colorRes))
    }

    fun setProgressBackgroundColorSchemeColor(@ColorInt color: Int) {
        mCircleView.setBackgroundColor(color)
    }

    @Deprecated("Use setColorSchemeResources")
    fun setColorScheme(@ColorRes vararg colors: Int) {
        setColorSchemeResources(*colors)
    }

    fun setColorSchemeResources(@ColorRes vararg colorResIds: Int) {
        val ctx = context
        val colorRes = IntArray(colorResIds.size)
        for (i in colorResIds.indices) {
            colorRes[i] = ContextCompat.getColor(ctx, colorResIds[i])
        }
        setColorSchemeColors(*colorRes)
    }

    fun setColorSchemeColors(@ColorInt vararg colors: Int) {
        ensureTarget()
        mProgress.setColorSchemeColors(*colors)
    }

    // ---------- touch / nested scrolling / spinner movement ----------

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        ensureTarget()
        val action = ev.actionMasked
        var pointerIndex: Int

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false
        }

        if (!isEnabled || mReturningToStart || mRefreshing || mNestedScrollInProgress || ev.pointerCount > 1) {
            return false
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if (canChildScrollUp()) return false
                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCircleView.top)
                mActivePointerId = ev.getPointerId(0)
                mIsBeingDragged = false

                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) return false
                mInitialDownY = ev.getY(pointerIndex)
                mInitialDownX = ev.getX(pointerIndex)
            }

            MotionEvent.ACTION_MOVE -> {
                if (canChildScrollUp() || shouldCancelDrag) return false
                if (mActivePointerId == INVALID_POINTER) {
                    Log.w(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.")
                    return false
                }
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) return false

                val diffX = ev.x - mInitialDownX
                val diffY = ev.y - mInitialDownY
                if (diffY < 0) {
                    shouldCancelDrag = true
                    return false
                }
                if (abs(diffY) < abs(diffX)) {
                    // horizontal scroll
                    return false
                }

                val y = ev.getY(pointerIndex)
                startDragging(y)
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                shouldCancelDrag = false
                mIsBeingDragged = false
                mActivePointerId = INVALID_POINTER
            }
        }

        return mIsBeingDragged
    }

    override fun requestDisallowInterceptTouchEvent(b: Boolean) {
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget is AbsListView) ||
            (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget!!))
        ) {
            // ignore
        } else {
            super.requestDisallowInterceptTouchEvent(b)
        }
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return isEnabled && !mReturningToStart && !mRefreshing &&
                (nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
        startNestedScroll(axes and ViewCompat.SCROLL_AXIS_VERTICAL)
        mTotalUnconsumed = 0f
        mNestedScrollInProgress = true
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - mTotalUnconsumed.toInt()
                mTotalUnconsumed = 0f
            } else {
                mTotalUnconsumed -= dy
                consumed[1] = dy
            }
            moveSpinner(mTotalUnconsumed)
        }

        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0f && abs(dy - consumed[1]) > 0) {
            mCircleView.visibility = View.GONE
        }

        val parentConsumed = mParentScrollConsumed
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0]
            consumed[1] += parentConsumed[1]
        }
    }

    override fun getNestedScrollAxes(): Int = mNestedScrollingParentHelper.nestedScrollAxes

    override fun onStopNestedScroll(target: View) {
        mNestedScrollingParentHelper.onStopNestedScroll(target)
        mNestedScrollInProgress = false
        if (mTotalUnconsumed > 0) {
            finishSpinner(mTotalUnconsumed)
            mTotalUnconsumed = 0f
        }
        stopNestedScroll()
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetInWindow)
        val dy = dyUnconsumed + mParentOffsetInWindow[1]
        if (dy < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += abs(dy.toFloat())
            moveSpinner(mTotalUnconsumed)
        }
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mNestedScrollingChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean = mNestedScrollingChildHelper.isNestedScrollingEnabled

    override fun startNestedScroll(axes: Int): Boolean = mNestedScrollingChildHelper.startNestedScroll(axes)

    override fun stopNestedScroll() = mNestedScrollingChildHelper.stopNestedScroll()

    override fun hasNestedScrollingParent(): Boolean = mNestedScrollingChildHelper.hasNestedScrollingParent()

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?
    ): Boolean = mNestedScrollingChildHelper.dispatchNestedScroll(
        dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow
    )

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean = mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean =
        dispatchNestedPreFling(velocityX, velocityY)

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean =
        dispatchNestedFling(velocityX, velocityY, consumed)

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean =
        mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean =
        mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY)

    private fun moveSpinner(overscrollTop: Float) {
        mProgress.setArrowEnabled(true)
        val originalDragPercent = overscrollTop / mTotalDragDistance
        val dragPercent = min(1f, abs(originalDragPercent))
        val adjustedPercent = max(dragPercent - .4f, 0f) * 5 / 3
        val extraOS = abs(overscrollTop) - mTotalDragDistance
        val slingshotDist = if (mCustomSlingshotDistance > 0) {
            mCustomSlingshotDistance.toFloat()
        } else {
            if (mUsingCustomStart) (mSpinnerOffsetEnd - mOriginalOffsetTop).toFloat() else mSpinnerOffsetEnd.toFloat()
        }
        val tensionSlingshotPercent = max(
            0f,
            min(extraOS, slingshotDist * 2) / slingshotDist
        )
        val q = tensionSlingshotPercent / 4f
        val tensionPercent = (q - q * q) * 2f

        val extraMove = (slingshotDist) * tensionPercent * 2

        val targetY = mOriginalOffsetTop + ((slingshotDist * dragPercent) + extraMove).toInt()
        if (mCircleView.visibility != View.VISIBLE) {
            mCircleView.visibility = View.VISIBLE
        }
        if (!mScale) {
            mCircleView.scaleX = 1f
            mCircleView.scaleY = 1f
        }

        if (mScale) {
            setAnimationProgress(min(1f, overscrollTop / mTotalDragDistance))
        }
        if (overscrollTop < mTotalDragDistance) {
            if (mProgress.alpha > STARTING_PROGRESS_ALPHA && !isAnimationRunning(mAlphaStartAnimation)) {
                startProgressAlphaStartAnimation()
            }
        } else {
            if (mProgress.alpha < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
                startProgressAlphaMaxAnimation()
            }
        }
        val strokeStart = adjustedPercent * .8f
        mProgress.setStartEndTrim(0f, min(MAX_PROGRESS_ANGLE, strokeStart))
        mProgress.setArrowScale(min(1f, adjustedPercent))

        val rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f
        mProgress.setProgressRotation(rotation)
        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop)
    }

    private fun finishSpinner(overscrollTop: Float) {
        if (overscrollTop > mTotalDragDistance) {
            // when user pulled enough -> set refreshing and notify
            setRefreshingInternal(true, true)
        } else {
            mRefreshing = false
            mProgress.setStartEndTrim(0f, 0f)
            var listener: Animation.AnimationListener? = null
            if (!mScale) {
                listener = object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        if (!mScale) {
                            startScaleDownAnimation(null)
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                }
            }
            animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener)
            mProgress.setArrowEnabled(false)
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        var pointerIndex = -1

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false
        }

        if (!isEnabled || mReturningToStart || canChildScrollUp() || mRefreshing || mNestedScrollInProgress) {
            return false
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = ev.getPointerId(0)
                mIsBeingDragged = false
            }

            MotionEvent.ACTION_MOVE -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.w(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.")
                    return false
                }

                val diffX = ev.x - mInitialDownX
                val diffY = ev.y - mInitialDownY
                if (abs(diffY) < abs(diffX)) {
                    // horizontal scroll
                    return false
                }

                val y = ev.getY(pointerIndex)
                startDragging(y)

                if (mIsBeingDragged) {
                    val overscrollTop = (y - mInitialMotionY) * DRAG_RATE
                    if (overscrollTop > 0) {
                        moveSpinner(overscrollTop)
                    } else {
                        return false
                    }
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                pointerIndex = ev.actionIndex
                if (pointerIndex < 0) {
                    Log.w(LOG_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.")
                    return false
                }
                mActivePointerId = ev.getPointerId(pointerIndex)
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.w(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.")
                    return false
                }
                if (mIsBeingDragged) {
                    val y = ev.getY(pointerIndex)
                    val overscrollTop = (y - mInitialMotionY) * DRAG_RATE
                    mIsBeingDragged = false
                    finishSpinner(overscrollTop)
                }
                mActivePointerId = INVALID_POINTER
                return false
            }

            MotionEvent.ACTION_CANCEL -> return false
        }

        return true
    }

    private fun startDragging(y: Float) {
        val yDiff = y - mInitialDownY
        if (yDiff > mTouchSlop && !mIsBeingDragged) {
            mInitialMotionY = mInitialDownY + mTouchSlop
            mIsBeingDragged = true
            mProgress.alpha = STARTING_PROGRESS_ALPHA
        }
    }

    private fun animateOffsetToCorrectPosition(from: Int, listener: Animation.AnimationListener?) {
        mFrom = from
        mAnimateToCorrectPosition.reset()
        mAnimateToCorrectPosition.duration = ANIMATE_TO_TRIGGER_DURATION.toLong()
        mAnimateToCorrectPosition.interpolator = mDecelerateInterpolator
        if (listener != null) {
            mCircleView.setAnimationListener(listener)
        }
        mCircleView.clearAnimation()
        mCircleView.startAnimation(mAnimateToCorrectPosition)
    }

    private fun animateOffsetToStartPosition(from: Int, listener: Animation.AnimationListener?) {
        if (mScale) {
            startScaleDownReturnToStartAnimation(from, listener)
        } else {
            mFrom = from
            mAnimateToStartPosition.reset()
            mAnimateToStartPosition.duration = ANIMATE_TO_START_DURATION.toLong()
            mAnimateToStartPosition.interpolator = mDecelerateInterpolator
            if (listener != null) {
                mCircleView.setAnimationListener(listener)
            }
            mCircleView.clearAnimation()
            mCircleView.startAnimation(mAnimateToStartPosition)
        }
    }

    private val mAnimateToCorrectPosition = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            val endTarget = if (!mUsingCustomStart) {
                mSpinnerOffsetEnd - abs(mOriginalOffsetTop)
            } else {
                mSpinnerOffsetEnd
            }
            val targetTop = (mFrom + ((endTarget - mFrom) * interpolatedTime)).toInt()
            val offset = targetTop - mCircleView.top
            setTargetOffsetTopAndBottom(offset)
            mProgress.setArrowScale(1 - interpolatedTime)
        }
    }

    private val mAnimateToStartPosition = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            moveToStart(interpolatedTime)
        }
    }

    private fun startScaleDownReturnToStartAnimation(from: Int, listener: Animation.AnimationListener?) {
        mFrom = from
        mStartingScale = mCircleView.scaleX
        mScaleDownToStartAnimation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                val targetScale = (mStartingScale + (-mStartingScale * interpolatedTime))
                setAnimationProgress(targetScale)
                moveToStart(interpolatedTime)
            }
        }
        mScaleDownToStartAnimation?.duration = SCALE_DOWN_DURATION.toLong()
        if (listener != null) {
            mCircleView.setAnimationListener(listener)
        }
        mCircleView.clearAnimation()
        mCircleView.startAnimation(mScaleDownToStartAnimation)
    }

    // ---------- layout overrides ----------

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mTarget == null) ensureTarget()
        val target = mTarget ?: return
        target.measure(
            MeasureSpec.makeMeasureSpec(
                measuredWidth - paddingLeft - paddingRight,
                MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(
                measuredHeight - paddingTop - paddingBottom,
                MeasureSpec.EXACTLY
            )
        )
        mCircleView.measure(
            MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY)
        )
        mCircleViewIndex = -1
        for (index in 0 until childCount) {
            if (getChildAt(index) === mCircleView) {
                mCircleViewIndex = index
                break
            }
        }
    }

    // IMPORTANT: exact signature to satisfy ViewGroup abstract method
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = measuredWidth
        val height = measuredHeight
        if (childCount == 0) return
        if (mTarget == null) ensureTarget()
        val target = mTarget ?: return
        val childLeft = paddingLeft
        val childTop = paddingTop
        val childWidth = width - paddingLeft - paddingRight
        val childHeight = height - paddingTop - paddingBottom
        target.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        val circleWidth = mCircleView.measuredWidth
        val circleHeight = mCircleView.measuredHeight
        mCircleView.layout(
            width / 2 - circleWidth / 2,
            mCurrentTargetOffsetTop,
            width / 2 + circleWidth / 2,
            mCurrentTargetOffsetTop + circleHeight
        )
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.actionIndex
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mActivePointerId = ev.getPointerId(newPointerIndex)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (!enabled) {
            reset()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        reset()
    }

    interface OnChildScrollUpCallback {
        fun canChildScrollUp(parent: SwipeRefreshLayout, child: View?): Boolean
    }
}