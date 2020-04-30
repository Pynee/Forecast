package org.duckdns.pynetti.forecast.graphview

import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.renderer.*
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * MyCombinedChartRenderer
 * Custom CombinedChartRenderer just to enable custom BarChartRenderer
 */
class MyCombinedChartRenderer(
    chart: CombinedChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : CombinedChartRenderer(chart, animator, viewPortHandler) {


    override fun createRenderers() {

        mRenderers.clear()

        val chart = mChart.get() as CombinedChart


        val orders = chart.drawOrder

        for (order in orders) {

            when {

                (order.name == "BAR") -> {
                    if (chart.barData != null)
                        mRenderers.add(
                            MyBarChartRenderer(
                                chart,
                                mAnimator,
                                mViewPortHandler
                            )
                        )
                }
                (order.name == "BUBBLE") -> {
                    if (chart.bubbleData != null) mRenderers.add(
                        BubbleChartRenderer(
                            chart,
                            mAnimator,
                            mViewPortHandler
                        )
                    )
                }
                (order.name == "LINE") -> {
                    if (chart.lineData != null)
                        mRenderers.add(LineChartRenderer(chart, mAnimator, mViewPortHandler))
                }
                (order.name == "CANDLE") -> {
                    if (chart.candleData != null)
                        mRenderers.add(CandleStickChartRenderer(chart, mAnimator, mViewPortHandler))
                }
                (order.name == "SCATTER") -> {
                    if (chart.scatterData != null)
                        mRenderers.add(ScatterChartRenderer(chart, mAnimator, mViewPortHandler))
                }
            }
        }
    }
}
