package org.duckdns.pynetti.forecast.graphview

import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * MyBarChartRenderer
 * custom highlight that highlights whole column and fakes highlight draw location to enable fixed custom marker on top left corner
 */
class MyBarChartRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {

    //Whole column highlight
    override fun prepareBarHighlight(
        x: Float,
        y1: Float,
        y2: Float,
        barWidthHalf: Float,
        trans: Transformer
    ) {

        val left = x - barWidthHalf
        val right = x + barWidthHalf
        // val top = y1
        // val bottom = y2
        mChart.centerOfView
        mBarRect.set(left, mChart.yChartMax, right, y2)

        trans.rectToPixelPhase(mBarRect, mAnimator.phaseY)
    }

    //Fakes highlights drawing position to enable fixed custom marker on top left corner
    override fun setHighlightDrawPos(high: Highlight, bar: RectF) {
        high.setDraw(mChart.contentRect.left + 25, mChart.contentRect.top + 25)
    }
}