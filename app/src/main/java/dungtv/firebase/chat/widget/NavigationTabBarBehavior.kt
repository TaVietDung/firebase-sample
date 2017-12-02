package dungtv.firebase.chat.widget

import android.animation.ObjectAnimator
import android.os.Build
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorCompat
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.view.View
import android.view.ViewGroup

class NavigationTabBarBehavior(behaviorTranslationEnabled: Boolean) : VerticalScrollingBehavior<NavigationTabBar>() {

    private var mTranslationAnimator: ViewPropertyAnimatorCompat? = null
    private var mTranslationObjectAnimator: ObjectAnimator? = null
    private var mSnackBarLayout: Snackbar.SnackbarLayout? = null
    private var mFloatingActionButton: FloatingActionButton? = null

    private var mSnackBarHeight = -1
    private var mTargetOffset = 0f
    private var mFabTargetOffset = 0f
    private var mFabDefaultBottomMargin = 0f

    private var mHidden: Boolean = false
    private var mFabBottomMarginInitialized: Boolean = false
    private var mBehaviorTranslationEnabled = true

    init {
        this.mBehaviorTranslationEnabled = behaviorTranslationEnabled
    }

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: NavigationTabBar?, dependency: View?): Boolean {
        updateSnackBar(child, dependency)
        updateFloatingActionButton(dependency)
        return super.layoutDependsOn(parent, child, dependency)
    }

    public override fun onNestedVerticalOverScroll() {
        // This method is intentionally empty, because of override
    }

    public override fun onDirectionNestedPreScroll() {
        // This method is intentionally empty, because of override
    }

    override fun onNestedDirectionFling(): Boolean {
        return false
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: NavigationTabBar, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        if (dyConsumed < 0)
            handleDirection(child, VerticalScrollingBehavior.SCROLL_DIRECTION_DOWN)
        else if (dyConsumed > 0)
            handleDirection(child, VerticalScrollingBehavior.SCROLL_DIRECTION_UP)
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: NavigationTabBar, directTargetChild: View, target: View, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes)
    }

    // Handle scroll direction
    private fun handleDirection(child: NavigationTabBar, scrollDirection: Int) {
        if (!mBehaviorTranslationEnabled) return
        if (scrollDirection == VerticalScrollingBehavior.SCROLL_DIRECTION_DOWN && mHidden) {
            mHidden = false
            animateOffset(child, 0, false, true)
        } else if (scrollDirection == VerticalScrollingBehavior.SCROLL_DIRECTION_UP && !mHidden) {
            mHidden = true
            animateOffset(child, child.height, false, true)
        }
    }

    // Animate offset
    private fun animateOffset(child: NavigationTabBar, offset: Int, forceAnimation: Boolean, withAnimation: Boolean) {
        if (!mBehaviorTranslationEnabled && !forceAnimation) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            ensureOrCancelObjectAnimation(child, offset, withAnimation)
            mTranslationObjectAnimator!!.start()
        } else {
            ensureOrCancelAnimator(child, withAnimation)
            mTranslationAnimator!!.translationY(offset.toFloat()).start()
        }
    }

    // Manage animation for Android >= KITKAT
    private fun ensureOrCancelAnimator(child: NavigationTabBar, withAnimation: Boolean) {
        if (mTranslationAnimator == null) {
            mTranslationAnimator = ViewCompat.animate(child)
            mTranslationAnimator!!.duration = (if (withAnimation) ANIMATION_DURATION else 0).toLong()
            mTranslationAnimator!!.setUpdateListener { view ->
                // Animate snack bar
                if (mSnackBarLayout != null && mSnackBarLayout!!.layoutParams is ViewGroup.MarginLayoutParams) {
                    mTargetOffset = child.barHeight - view.translationY

                    val p = mSnackBarLayout!!.layoutParams as ViewGroup.MarginLayoutParams

                    p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, mTargetOffset.toInt())
                    mSnackBarLayout!!.requestLayout()
                }
                // Animate Floating Action Button
                if (mFloatingActionButton != null && mFloatingActionButton!!.layoutParams is ViewGroup.MarginLayoutParams) {
                    val p = mFloatingActionButton!!.layoutParams as ViewGroup.MarginLayoutParams

                    mFabTargetOffset = mFabDefaultBottomMargin - view.translationY
                    p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, mFabTargetOffset.toInt())
                    mFloatingActionButton!!.requestLayout()
                }
            }
            mTranslationAnimator!!.interpolator = INTERPOLATOR
        } else {
            mTranslationAnimator!!.duration = (if (withAnimation) ANIMATION_DURATION else 0).toLong()
            mTranslationAnimator!!.cancel()
        }
    }

    // Manage animation for Android < KITKAT
    private fun ensureOrCancelObjectAnimation(child: NavigationTabBar, offset: Int, withAnimation: Boolean) {
        if (mTranslationObjectAnimator != null) mTranslationObjectAnimator!!.cancel()

        mTranslationObjectAnimator = objectAnimatorOfTranslationY(child, offset)
        mTranslationObjectAnimator!!.duration = (if (withAnimation) ANIMATION_DURATION else 0).toLong()
        mTranslationObjectAnimator!!.interpolator = INTERPOLATOR
        mTranslationObjectAnimator!!.addUpdateListener {
            if (mSnackBarLayout != null && mSnackBarLayout!!.layoutParams is ViewGroup.MarginLayoutParams) {
                mTargetOffset = child.barHeight - child.translationY

                val p = mSnackBarLayout!!.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, mTargetOffset.toInt())
                mSnackBarLayout!!.requestLayout()
            }
            // Animate Floating Action Button
            if (mFloatingActionButton != null && mFloatingActionButton!!.layoutParams is ViewGroup.MarginLayoutParams) {
                mFabTargetOffset = mFabDefaultBottomMargin - child.translationY

                val p = mFloatingActionButton!!.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, mFabTargetOffset.toInt())
                mFloatingActionButton!!.requestLayout()
            }
        }
    }

    // Enable or not the behavior translation
    fun setBehaviorTranslationEnabled(behaviorTranslationEnabled: Boolean) {
        this.mBehaviorTranslationEnabled = behaviorTranslationEnabled
    }

    // Hide NTB with animation
    fun hideView(view: NavigationTabBar, offset: Int, withAnimation: Boolean) {
        if (!mHidden) {
            mHidden = true
            animateOffset(view, offset, true, withAnimation)
        }
    }

    // Reset NTB position with animation
    fun resetOffset(view: NavigationTabBar, withAnimation: Boolean) {
        if (mHidden) {
            mHidden = false
            animateOffset(view, 0, true, withAnimation)
        }
    }

    // Update snack bar bottom margin
    private fun updateSnackBar(child: NavigationTabBar?, dependency: View?) {
        if (dependency != null && dependency is Snackbar.SnackbarLayout) {
            mSnackBarLayout = dependency
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mSnackBarLayout!!.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                    if (mFloatingActionButton != null && mFloatingActionButton!!.layoutParams is ViewGroup.MarginLayoutParams) {
                        mFabTargetOffset = mFabDefaultBottomMargin - child!!.translationY

                        val p = mFloatingActionButton!!.layoutParams as ViewGroup.MarginLayoutParams
                        p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, mFabTargetOffset.toInt())
                        mFloatingActionButton!!.requestLayout()
                    }
                }
            }

            if (mSnackBarHeight == -1) mSnackBarHeight = dependency.height
            val targetMargin = (child!!.barHeight - child.translationY).toInt()

            child.bringToFront()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dependency.stateListAnimator = null
                dependency.elevation = 0.0f
            }

            if (dependency.layoutParams is ViewGroup.MarginLayoutParams) {
                val p = dependency.layoutParams as ViewGroup.MarginLayoutParams

                p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, targetMargin)
                dependency.requestLayout()
            }
        }
    }

    // Update floating action button bottom margin
    private fun updateFloatingActionButton(dependency: View?) {
        if (dependency != null && dependency is FloatingActionButton) {
            mFloatingActionButton = dependency

            if (!mFabBottomMarginInitialized && dependency.layoutParams is ViewGroup.MarginLayoutParams) {
                mFabBottomMarginInitialized = true

                val p = dependency.layoutParams as ViewGroup.MarginLayoutParams
                mFabDefaultBottomMargin = p.bottomMargin.toFloat()
            }
        }
    }

    companion object {

        private val INTERPOLATOR = LinearOutSlowInInterpolator()
        private val ANIMATION_DURATION = 300

        private fun objectAnimatorOfTranslationY(target: View, offset: Int): ObjectAnimator {
            return ObjectAnimator.ofFloat<View>(target, View.TRANSLATION_Y, offset.toFloat())
        }

        fun from(view: NavigationTabBar): NavigationTabBarBehavior {
            val params = view.layoutParams as? CoordinatorLayout.LayoutParams
                    ?: throw IllegalArgumentException("The view is not a child of CoordinatorLayout")

            return params.behavior as? NavigationTabBarBehavior ?: throw IllegalArgumentException(
                    "The view is not associated with NavigationTabBarBehavior")
        }
    }
}