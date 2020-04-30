package org.duckdns.pynetti.forecast.forecastview

/*
ForecastView "main page" that shows 3 day forecast

 */


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.day_item.view.*
import kotlinx.android.synthetic.main.fragment_forecast_view.*
import org.duckdns.pynetti.forecast.MainActivity
import org.duckdns.pynetti.forecast.R
import org.duckdns.pynetti.forecast.data.AppViewModel
import org.duckdns.pynetti.forecast.data.ForecastData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class ForecastView : Fragment() {
    private lateinit var viewModel: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        viewModel.forecastData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { forecastData ->
                if (forecastData != null)
                    updateUI(extractElements(forecastData))
            })

        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_forecast_view, container, false)

    }


    private fun extractElements(foreCastData: ForecastData): HashMap<TextView, String> {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        val updateElements: HashMap<TextView, String> = HashMap()
        val dayArray = arrayOf(
            todayForecastView,
            tomorrowForecastView,
            overmorrowForecastView
        )//today,tomorrow,overmorrow views
        val timeTagArray = arrayOf(
            "forecastMorningView",
            "forecastAfternoonView",
            "forecastEveningView"
        )//morning,afternoon,evening views
        val variableArray = arrayOf("Temperature", "Humidity", "WindSpeedMS", "precipitationAmount")
        val symbolArray = arrayOf("\uD83C\uDF21", "\uD83C\uDF2B️", "\uD83D\uDCA8", "\uD83D\uDCA7")
        val unitArray = arrayOf("°C", "%", "m/s", "mm")
        val res = resources


        //replace overmorrow with weekday name as overmorrow isn't commonly used term
        calendar.add(Calendar.DAY_OF_MONTH, 2)
        updateElements[overmorrowForecastView.dayTextView] =
            SimpleDateFormat("EEEE", Locale.US).format(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, -2)


        //Last update timeStamp
        updateElements[todayForecastView.updateTimeStampView] =
            "${res.getString(R.string.lastUpdate)} ${SimpleDateFormat(
                "dd.MM.yyyy HH:mm",
                Locale.US
            ).format(formatter.parse(foreCastData.timeStamp))}"

        formatter.applyPattern("yyyy-MM-dd'T'HH':00:00Z'")
        //Fill the data fields
        for (day in 0 until 3) {
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            for (time in 0 until 3) {
                calendar.add(Calendar.HOUR_OF_DAY, 6)
                for (variable in 0 until 4) {
                    val forecastItem = dayArray[day].findViewById<ForecastItemView>(
                        res.getIdentifier(
                            timeTagArray[time],
                            "id",
                            activity!!.packageName
                        )
                    )
                    val textView = forecastItem.findViewById<TextView>(
                        res.getIdentifier(
                            "${variableArray[variable]}View",
                            "id",
                            activity!!.packageName
                        )
                    )
                    val timeStamp = formatter.format(calendar.time)
                    var value = foreCastData.data[variableArray[variable]]?.get(timeStamp)

                    if (variable == 3) {
                        val startPrecipitationString =
                            foreCastData.data[variableArray[variable]]?.get(timeStamp)

                        val startPrecipitation = if (startPrecipitationString != "-") {
                            startPrecipitationString?.toFloat() ?: 0.0f
                        } else {
                            0.0f
                        }

                        calendar.add(Calendar.HOUR_OF_DAY, 6)
                        val endPrecipitationString =
                            foreCastData.data[variableArray[variable]]?.get(
                                formatter.format(calendar.time)
                            )
                        value = if (endPrecipitationString != "-") {
                            val endPrecipitation = endPrecipitationString?.toFloat() ?: 0.0f
                            "%.2f".format((endPrecipitation - startPrecipitation))
                        } else {
                            "-.--"
                        }

                        calendar.add(Calendar.HOUR_OF_DAY, -6)
                    }
                    updateElements[textView] =
                        "${symbolArray[variable]} $value ${unitArray[variable]}"
                }

            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return updateElements
    }

    private fun updateUI(updates: HashMap<TextView, String>) {
        updates.map { (key, value) ->
            key.text = value
        }
    }

}
