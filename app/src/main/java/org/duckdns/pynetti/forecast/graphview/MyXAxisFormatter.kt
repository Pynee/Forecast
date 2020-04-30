package org.duckdns.pynetti.forecast.graphview

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import org.duckdns.pynetti.forecast.data.ForecastData

import java.text.SimpleDateFormat
import java.util.*

/**
 *MyAxisFormatter parses forecast data for X axis rendering
 */
class MyXAxisFormatter(forecastData: ForecastData) : ValueFormatter() {
    //https://www.ilmatieteenlaitos.fi/latauspalvelun-pikaohje for symbol value explanations
    private val weatherSymbolsMap =
        mapOf(
            "1.0" to "☀️",
            "2.0" to "⛅",
            "21.0" to "\uD83C\uDF27",
            "22.0" to "\uD83C\uDF27",
            "23.0" to "\uD83C\uDF27",
            "3.0" to "☁",
            "31.0" to "\uD83C\uDF27",
            "32.0" to "\uD83C\uDF27",
            "33.0" to "\uD83C\uDF27",
            "41.0" to "\uD83C\uDF28",
            "42.0" to "\uD83C\uDF28",
            "43.0" to "\uD83C\uDF28",
            "51.0" to "\uD83C\uDF28",
            "52.0" to "\uD83C\uDF28",
            "53.0" to "\uD83C\uDF28",
            "61.0" to "⛈",
            "62.0" to "⛈",
            "63.0" to "\uD83C\uDF29",
            "64.0" to "\uD83C\uDF29",
            "71.0" to "\uD83C\uDF28",
            "72.0" to "\uD83C\uDF28",
            "73.0" to "\uD83C\uDF28",
            "81.0" to "\uD83C\uDF28",
            "82.0" to "\uD83C\uDF28",
            "83.0" to "\uD83C\uDF28",
            "91.0" to "\uD83C\uDF2B",
            "92.0" to "\uD83C\uDF2B"
        )
    private val forecastSymbolsArray = forecastData.data["WeatherSymbol3"]!!.values.toList()
    private val forecastTimestampsArray = forecastData.data["WeatherSymbol3"]!!.keys.toList()
    private val time = arrayOf(
        "00:00\ntest",
        "⛅\n01:00",
        "\uD83C\uDF1E\n02:00",
        "\uD83C\uDF26\n03:00",
        "\uD83C\uDF27\n04:00",
        "\uD83C\uDF28\n05:00",
        "☁\n06:00",
        "\uD83C\uDF29\n07:00",
        "☼\n08:00",
        "☼\n09:00",
        "☼\n10:00",
        "☼\n11:00",
        "☼\n12:00",
        "☼\n13:00",
        "☼\n14:00",
        "☼\n15:00",
        "☼\n16:00",
        "☼\n17:00",
        "☼\n18:00",
        "☼\n19:00",
        "☼\n20:00",
        "☼\n21:00",
        "☼\n22:00",
        "☼\n23:00"
    )

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val index = value.toInt()
        val reminder = index.rem(24)
        val symbol = forecastSymbolsArray[index]
        val symbolString = if (symbol != "-") weatherSymbolsMap[forecastSymbolsArray[index]] else ""
        val time = if (reminder == 0) {
            val calendar = Calendar.getInstance()
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH':00:00Z'", Locale.US)
            calendar.time = formatter.parse(forecastTimestampsArray[index])
            "${reminder}:00\n${calendar.get(Calendar.DAY_OF_MONTH)}.${calendar.get(Calendar.MONTH)}"
        } else {
            "${reminder}:00"
        }
        return "${symbolString}\n${time}"
        //return days.getOrNull(value.toInt().rem(24)) ?: value.toString()
    }
}