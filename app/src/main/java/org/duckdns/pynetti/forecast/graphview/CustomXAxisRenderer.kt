package org.duckdns.pynetti.forecast.graphview

import android.graphics.Canvas
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 *CustomXAxisRenderer parses and renders X axis labels showing date,time and weather symbol
 */
class CustomXAxisRenderer(viewPortHandler: ViewPortHandler, xAxis: XAxis, trans: Transformer) :
    XAxisRenderer(viewPortHandler, xAxis, trans) {

    override fun drawLabel(
        c: Canvas,
        formattedLabel: String,
        x: Float,
        y: Float,
        anchor: MPPointF,
        angleDegrees: Float
    ) {


        val line =
            formattedLabel.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees)
        for (i in 1 until line.size) { // we've already processed 1st line
            Utils.drawXAxisValue(
                c, line[i], x, y - mAxisLabelPaint.textSize * i,
                mAxisLabelPaint, anchor, angleDegrees
            )
        }
    }
}