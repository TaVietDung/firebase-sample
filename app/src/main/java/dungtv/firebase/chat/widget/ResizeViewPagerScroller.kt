package dungtv.firebase.chat.widget

import android.content.Context
import android.widget.Scroller

// Custom scroller with custom scroll duration
class ResizeViewPagerScroller(context: Context, private val animationDuration: Int) : Scroller(context) {

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, animationDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        super.startScroll(startX, startY, dx, dy, animationDuration)
    }
}