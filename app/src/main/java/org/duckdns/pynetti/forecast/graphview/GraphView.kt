package org.duckdns.pynetti.forecast.graphview

/**
 * GraphView Fragment that show Graph of forecast data
 */

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.*
import java.util.*
import com.github.mikephil.charting.components.YAxis
import org.duckdns.pynetti.forecast.R
import org.duckdns.pynetti.forecast.data.AppViewModel
import org.duckdns.pynetti.forecast.data.ForecastData

class GraphView : Fragment() {
    private lateinit var viewModel: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //retainInstance = true
        val view = inflater.inflate(R.layout.fragment_graph_view, container, false)
        viewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        viewModel.forecastData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { forecastData ->
                if (forecastData != null)
                    updateChart(forecastData)
            })


        // Inflate the layout for this fragment
        return view
    }

    private fun updateChart(forecastData: ForecastData) {
        val chartView = view!!.findViewById<CombinedChart>(R.id.chart)
        chartView.renderer = MyCombinedChartRenderer(
            chartView,
            chartView.animator,
            chartView.viewPortHandler
        )

        //Add custom marker that stays on top left corner
        val marker = CustomMarkerView(
            context!!,
            R.layout.markerview,
            forecastData
        )
        chartView.marker = marker

        val temperatureLineChartArray = ArrayList<Entry>()
        val temperatureArray = forecastData.data["Temperature"]!!.values.toList()
        for (i in 0 until forecastData.data["Temperature"]!!.size) {
            if (temperatureArray[i] != "-") {
                temperatureLineChartArray.add(Entry(i.toFloat(), temperatureArray[i].toFloat()))
            }
        }
        val precipitationBarChartArray = ArrayList<BarEntry>()
        val precipitationArray = forecastData.data["precipitation1h"]!!.values.toList()
        for (i in 0 until forecastData.data["precipitation1h"]!!.size) {
            if (precipitationArray[i] != "-") {
                precipitationBarChartArray.add(
                    BarEntry(
                        i.toFloat(),
                        precipitationArray[i].toFloat()
                    )
                )
            }
        }

        //Create temperature LineDataSet
        val lineDataSet = LineDataSet(temperatureLineChartArray, "Temperature °C")
        lineDataSet.color = Color.RED
        lineDataSet.lineWidth = 3f
        lineDataSet.setDrawCircles(false)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.cubicIntensity = 0.1f
        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSet.setDrawHighlightIndicators(false)
        lineDataSet.isHighlightEnabled = false

        //Keep temperature scale constant (Always 40 degrees high)
        val leftAxis = chartView.axisLeft
        leftAxis.setDrawGridLines(false)
        val lineChartMin = kotlin.math.floor(lineDataSet.yMin / 10) * 10
        leftAxis.axisMaximum = lineChartMin + 40
        leftAxis.axisMinimum = lineChartMin
        leftAxis.setDrawZeroLine(true)
        leftAxis.setDrawGridLines(false)

        //Setup Precipitation barDataSet
        val barDataSet = BarDataSet(precipitationBarChartArray, "Precipitation mm")
        barDataSet.color = Color.BLUE
        barDataSet.highLightAlpha = 50
        barDataSet.axisDependency = YAxis.AxisDependency.RIGHT

        //Setup precipitation max value
        val rightAxis = chartView.axisRight
        rightAxis.setDrawGridLines(false)
        val barDataMax = kotlin.math.floor(barDataSet.yMax / 5) * 5 + 5
        rightAxis.axisMinimum = 0f
        rightAxis.axisMaximum = barDataMax
        rightAxis.setDrawGridLines(false)
        rightAxis.labelCount = barDataMax.toInt()

        //create CombinedData variable and disable value "labels"
        val lineData = LineData(lineDataSet)
        lineData.setDrawValues(false)
        val barData = BarData(barDataSet)
        barData.setDrawValues(false)
        val data = CombinedData()
        data.setDrawValues(false)
        data.setData(lineData)
        data.setData(barData)

        //Setup chartView
        chartView.setXAxisRenderer(
            CustomXAxisRenderer(
                chartView.viewPortHandler,
                chartView.xAxis,
                chartView.getTransformer(YAxis.AxisDependency.LEFT)
            )
        )
        chartView.setBackgroundColor(resources.getColor(R.color.colorPrimaryLight))
        chartView.extraTopOffset = 40f
        chartView.xAxis.valueFormatter =
            MyXAxisFormatter(forecastData)
        chartView.xAxis.labelCount = 10
        chartView.xAxis.textSize = 13f
        chartView.xAxis.setDrawGridLines(false)
        chartView.data = data
        chartView.setVisibleXRangeMaximum(10f)
        chartView.maxHighlightDistance = Float.MAX_VALUE
        chartView.setDrawGridBackground(false)
        chartView.description.isEnabled = false
        chartView.invalidate()

    }

}
