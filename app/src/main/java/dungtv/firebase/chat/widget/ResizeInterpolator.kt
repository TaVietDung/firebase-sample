package dungtv.firebase.chat.widget

import android.view.animation.Interpolator

// Resize interpolator to create smooth effect on pointer according to inspiration design
// This is like improved accelerated and decelerated interpolator
class ResizeInterpolator : Interpolator {

    // Check whether side we move
    private var mResizeIn: Boolean = false

    override fun getInterpolation(input: Float): Float {
        return if (mResizeIn)
            (1.0f - Math.pow((1.0f - input).toDouble(), (2.0f * FACTOR).toDouble())).toFloat()
        else
            Math.pow(input.toDouble(), (2.0f * FACTOR).toDouble()).toFloat()
    }

    fun getResizeInterpolation(input: Float, resizeIn: Boolean): Float {
        mResizeIn = resizeIn
        return getInterpolation(input)
    }

    companion object {
        // Spring factor
        const val FACTOR = 1.0f
    }
}