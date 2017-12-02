package dungtv.firebase.chat.widget


import android.annotation.TargetApi
import android.os.Build
import android.support.design.widget.CoordinatorLayout
import android.view.View

abstract class VerticalScrollingBehavior<V : View> : CoordinatorLayout.Behavior<V>() {

    private var mTotalDyUnconsumed = 0
    private var mTotalDy = 0
    private var overScrollDirection = SCROLL_NONE
    private var scrollDirection = SCROLL_NONE

    companion object {
        const val SCROLL_DIRECTION_UP = 1
        const val SCROLL_DIRECTION_DOWN = -1
        const val SCROLL_NONE = 0
    }

    // Direction of the over scroll: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
    // Unconsumed value, negative or positive based on the direction
    // Cumulative value for current direction
    protected abstract fun onNestedVerticalOverScroll()

    // Direction of the over scroll: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
    protected abstract fun onDirectionNestedPreScroll()

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View,
                                     target: View, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes and View.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dxConsumed: Int,
                                dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        if (dyUnconsumed > 0 && mTotalDyUnconsumed < 0) {
            mTotalDyUnconsumed = 0
            overScrollDirection = SCROLL_DIRECTION_UP
        } else if (dyUnconsumed < 0 && mTotalDyUnconsumed > 0) {
            mTotalDyUnconsumed = 0
            overScrollDirection = SCROLL_DIRECTION_DOWN
        }

        mTotalDyUnconsumed += dyUnconsumed
        onNestedVerticalOverScroll()
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View,
                                   dx: Int, dy: Int, consumed: IntArray) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed)
        if (dy > 0 && mTotalDy < 0) {
            mTotalDy = 0
            scrollDirection = SCROLL_DIRECTION_UP
        } else if (dy < 0 && mTotalDy > 0) {
            mTotalDy = 0
            scrollDirection = SCROLL_DIRECTION_DOWN
        }
        mTotalDy += dy
        onDirectionNestedPreScroll()
    }

    override fun onNestedFling(coordinatorLayout: CoordinatorLayout, child: V, target: View, velocityX: Float,
                               velocityY: Float, consumed: Boolean): Boolean {
        super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed)
        scrollDirection = if (velocityY > 0) SCROLL_DIRECTION_UP else SCROLL_DIRECTION_DOWN
        return onNestedDirectionFling()
    }

    protected abstract fun onNestedDirectionFling(): Boolean

}