package org.duckdns.pynetti.forecast.graphview

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.markerview.view.*
import org.duckdns.pynetti.forecast.data.ForecastData
import java.text.SimpleDateFormat
import java.util.*


/**
 * CustomMarkerView
 * MarkerView that is show on top left corner on chart
 */
class CustomMarkerView(
    context: Context,
    layoutResource: Int,
    private val forecastData: ForecastData
) : MarkerView(context, layoutResource) {

    private lateinit var mOffset: MPPointF

    private val dateTextView = markerDateTextView
    private val textViews = arrayOf<TextView>(
        markerTemperatureTextView,
        markerPrecipitationTextView,
        markerWindspeedTextView,
        markerHumidityTextView
    )
    private val valueNames = arrayOf("Temperature", "precipitation1h", "WindSpeedMS", "Humidity")
    val symbolArray = arrayOf("\uD83C\uDF21", "\uD83D\uDCA7", "\uD83D\uDCA8", "\uD83C\uDF2B️")
    val unitArray = arrayOf("°C", "mm", "m/s", "%")
    // callbacks every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry, highlight: Highlight) {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH':00:00Z'", Locale.US)
        val date = formatter.parse(forecastData.data[valueNames[0]]!!.keys.toList()[e.x.toInt()])
        formatter.applyPattern("dd.MM 'klo' HH:mm ")
        dateTextView.text = formatter.format(date)

        for (index in 0 until textViews.size) {
            textViews[index].text =
                "${symbolArray[index]} ${forecastData.data[valueNames[index]]!!.values.toList().get(
                    e.x.toInt()
                )} ${unitArray[index]}"
        }


        // this will perform necessary layouting
        super.refreshContent(e, highlight)
    }


    override fun getOffset(): MPPointF {

        if (!::mOffset.isInitialized) {
            mOffset = MPPointF(0f, 0f)
        }

        return mOffset
    }


}