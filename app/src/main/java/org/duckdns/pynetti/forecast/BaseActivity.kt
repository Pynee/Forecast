package org.duckdns.pynetti.forecast


import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest

import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.custom_actionbar.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.duckdns.pynetti.forecast.data.AppViewModel
import org.duckdns.pynetti.forecast.data.ForecastData
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseActivity : AppCompatActivity() {
    lateinit var queue: RequestQueue
    lateinit var customActionBar: androidx.appcompat.app.ActionBar
    lateinit var searchView: SearchView
    private lateinit var viewModel: AppViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AppViewModel::class.java)

        queue = MySingleton.getInstance(this.applicationContext).requestQueue
        customActionBar = supportActionBar!!
        customActionBar.setDisplayShowCustomEnabled(true)
        customActionBar.setCustomView(R.layout.custom_actionbar)
        customActionBar.setDisplayShowTitleEnabled(false)
        customActionBar.setDisplayHomeAsUpEnabled(true)
        customActionBar.setHomeButtonEnabled(true)
        //actionBarDrawerToggle = ActionBarDrawerToggle(this,findViewById(R.id.acti) ,R.string.Open, R.string.Close);

        //if (MainActivity.Companion.ForeCastDataObject.value.name != "") {
        //    updateLocationInfo(MainActivity.Companion.ForeCastDataObject.value)
        //}

        //MainActivity.Companion.ForeCastDataObject.refreshListListeners[localClassName] = { updateLocationInfo(MainActivity.Companion.ForeCastDataObject.value) }

        viewModel.allFavorites.observe(this, androidx.lifecycle.Observer { favorites ->
            favorites.forEach { favorite ->
                favoriteButton.setImageResource(R.drawable.ic_star_border_yellow_24dp)
                if (viewModel.forecastData.value?.name != null && favorite.name.startsWith(viewModel.forecastData.value!!.name)) {
                    favoriteButton.setImageResource(R.drawable.ic_star_yellow_24dp)
                }
            }
        })
        viewModel.forecastData.observe(this, androidx.lifecycle.Observer { forecastData ->
            if (forecastData != null)
                updateLocationInfo(forecastData)
        })
    }

    private fun updateLocationInfo(forecastData: ForecastData) {
        stationNameTextView.text = forecastData.name
        //If region name is same that country don't show region
        if (forecastData.region != forecastData.country) {
            stationRegionTextView.text = forecastData.region
        } else {
            stationRegionTextView.text = ""
        }
        stationCountryTextView.text = forecastData.country
        //Clear error text field
        errorTextView.text = ""
        favoriteButton.setImageResource(R.drawable.ic_star_border_yellow_24dp)
        MainActivity.favoritesList.forEach { favorite ->
            if (favorite.startsWith(forecastData.name)) {
                favoriteButton.setImageResource(R.drawable.ic_star_yellow_24dp)
            }
        }
        favoriteButton.setOnClickListener {

            MainActivity.favoritesList.forEach { favorite ->
                if (favorite.startsWith(forecastData.name)) {
                    MainActivity.favoritesList.remove(favorite)
                    favoriteButton.setImageResource(R.drawable.ic_star_border_yellow_24dp)
                    return@setOnClickListener
                }
            }
            MainActivity.favoritesList.add("${forecastData.name}, ${forecastData.region}")
            favoriteButton.setImageResource(R.drawable.ic_star_yellow_24dp)
        }
    }

    override fun onResume() {
        super.onResume()
        MainActivity.locationPermissionManager.requestLocation()

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE
        searchView.queryHint = getString(R.string.search)
        searchView.findViewById<AutoCompleteTextView>(R.id.search_src_text).threshold = 0
        val cursor =
            MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1, "type"))
        filterFavorites("", cursor)
        val cursorAdapter =
            MyCursorAdapter(baseContext, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
        searchView.suggestionsAdapter = cursorAdapter
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val cursor = searchView.suggestionsAdapter.getItem(0) as Cursor
                val firstSuggestion =
                    cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                searchView.setQuery("", false)
                searchItem.collapseActionView()
                searchView.isIconified = true

                getWeatherData(null, firstSuggestion)
                hideKeyboard()
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                val cursor =
                    MatrixCursor(
                        arrayOf(
                            BaseColumns._ID,
                            SearchManager.SUGGEST_COLUMN_TEXT_1,
                            "type"
                        )
                    )
                if (query != null) {
                    filterFavorites(query, cursor)
                    if (query != "") {
                        filterPlaces(query, cursor)
                    }
                    if (cursor.count == 0) {
                        cursor.addRow(
                            arrayOf(
                                MainActivity.favoritesList.size + 1,
                                "No results",
                                "title"
                            )
                        )
                    }
                }

                cursorAdapter.changeCursor(cursor)
                return true
            }
        })

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {

                val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
                if (cursor.getString(cursor.getColumnIndex("type")) != "title") {
                    hideKeyboard()
                    val selection =
                        cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                    searchView.setQuery("", false)
                    searchItem.collapseActionView()
                    searchView.isIconified = true
                    getWeatherData(null, selection)
                    // Do something with selection
                    return true
                }
                return true
            }

        })

        super.onCreateOptionsMenu(menu)
        return true
    }

    fun Activity.hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(findViewById<View>(android.R.id.content).windowToken, 0)
    }

    private fun filterFavorites(query: String, cursor: MatrixCursor) {
        var firstFind = true
        var index = 1
        MainActivity.favoritesList.forEach { favorite ->
            if (favorite.startsWith(query, true)) {
                if (firstFind) {
                    cursor.addRow(arrayOf(0, "Favorites", "title"))
                    firstFind = false
                }
                cursor.addRow(arrayOf(index, favorite, "favorite"))
            }
            index++
        }
    }

    private fun filterPlaces(query: String, cursor: MatrixCursor) {
        var firstFind = true
        val inputStream = resources.openRawResource(R.raw.places)
        val reader = BufferedReader(
            InputStreamReader(inputStream, Charset.forName("UTF-8"))
        )
        var line = reader.readLine()
        var index = MainActivity.favoritesList.size + 2

        try {
            while (line != null) {
                if (line.startsWith(query, true)) {
                    if (firstFind) {
                        cursor.addRow(
                            arrayOf(
                                MainActivity.favoritesList.size + 1,
                                "Search result",
                                "title"
                            )
                        )
                        firstFind = false
                    }
                    cursor.addRow(arrayOf(index, line, "result"))
                }
                line = reader.readLine()
                index++
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * getWeatherData
     * makes request to FMI server when users location information is acquired or when users searches a location
     */
    fun getWeatherData(latLng: LatLng? = null, placeString: String? = null) {
        try {

            val locationString: String = when (true) {
                (latLng != null) -> "&latlon=${latLng.latitude},${latLng.longitude}"

                (placeString != null) ->
                    when (placeString) {
                        "Mänttä-Vilppula" -> "&place=Mänttä"
                        else -> "&place=${placeString.replace("\\s".toRegex(), "")}"
                    }

                else -> return
            }

            // Got last known location. In some rare situations this can be null.
            val calendar = Calendar.getInstance()
            val dateNow: Date = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, 4)
            val dateThreeDaysAfter = calendar.time
            val formatter = SimpleDateFormat("yyyy-MM-dd'T00:00:00'", Locale.US)
            val formattedDateNow: String = formatter.format(dateNow)
            val formattedDateThreeDaysAfter = formatter.format(dateThreeDaysAfter)
            println("Now: $formattedDateNow ThreeDaysAfter: $dateThreeDaysAfter")
            // Send data request to FMI opendata API
            val url =
                "https://opendata.fmi.fi/wfs?request=getFeature&starttime=$formattedDateNow&endtime=$formattedDateThreeDaysAfter${locationString}&storedquery_id=fmi::forecast::harmonie::surface::point::timevaluepair&parameters=Humidity,Temperature,WindSpeedMS,precipitationAmount,precipitation1h,WeatherSymbol3"
            println("Url: $url")
            val getWeatherRequest = StringRequest(Request.Method.GET, url, { response ->
                //If no errors parse XML

                GlobalScope.launch { viewModel.update(FMIForecastXmlParser().parse(response)) }

            }, { error ->
                println(error.toString())
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.errorLoadingData),
                    Toast.LENGTH_LONG
                )
                    .show()
            })

            queue.add(getWeatherRequest)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}