package org.duckdns.pynetti.forecast.pagerview

/*
SwipeLockableViewPager custom viewPager to be able to experiment with disabling swipe for changing pages

 */

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import androidx.core.view.ViewCompat.setAlpha
import androidx.core.view.ViewCompat.setTranslationY
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.view.View
import androidx.core.view.ViewCompat.setTranslationX


class SwipeLockableViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    private var swipeEnabled = true

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (swipeEnabled) {
            true -> super.onTouchEvent(event)
            false -> false
        }
    }


    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return when (swipeEnabled) {
            true -> super.onInterceptTouchEvent(event)
            false -> false
        }
    }

    fun setSwipePagingEnabled(swipeEnabled: Boolean) {
        this.swipeEnabled = swipeEnabled
    }
}

/**
 * Uses a combination of a PageTransformer and swapping X & Y coordinates
 * of touch events to create the illusion of a vertically scrolling ViewPager.
 *
 * Requires API 11+
 *
 */
class VerticalViewPager : ViewPager {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        // The majority of the magic happens here
        setPageTransformer(true, VerticalPageTransformer())
        // The easiest way to get rid of the overscroll drawing that happens on the left and right
        overScrollMode = OVER_SCROLL_NEVER
    }

    private inner class VerticalPageTransformer : PageTransformer {

        override fun transformPage(view: View, position: Float) {
            when {
                position < -1 -> view.alpha = 0f
                position <= 1 -> {
                    view.alpha = 1f
                    // Counteract the default slide transition
                    view.translationX = view.width * -position

                    //set Y position to swipe in from top

                    view.translationY = position * view.height
                }
                // (1,+Infinity]
                // This page is way off-screen to the right.
                else -> view.alpha = 0f

            }

        }
    }

    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    private fun swapXY(ev: MotionEvent): MotionEvent {
        val width = width.toFloat()
        val height = height.toFloat()

        val newX = ev.y / height * width
        val newY = ev.x / width * height

        ev.setLocation(newX, newY)

        return ev
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val intercepted = super.onInterceptTouchEvent(swapXY(ev))
        swapXY(ev) // return touch coordinates to original reference frame for any child views
        return intercepted
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return super.onTouchEvent(swapXY(ev))
    }

}
