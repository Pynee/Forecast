package org.duckdns.pynetti.forecast

/**
 * MainActivity
 * Shows Applications "Main body" that is shown on every page
 * Also handles
 *
 *
 * Location Strings source:https://kartat.kapsi.fi/
 */
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.BaseColumns
import android.transition.Slide
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.custom_actionbar.*
import kotlinx.android.synthetic.main.settings_window.*
import org.duckdns.pynetti.forecast.pagerview.PagerViewAdapter
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet
import kotlin.properties.Delegates
import kotlin.properties.Delegates.observable


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    //Companion object for sharing data to other classes
    companion object {
        lateinit var activityResources: Resources
        lateinit var locationPermissionManager: LocationPermissionManager
        lateinit var sharedPreferences: SharedPreferences
        //lateinit var favoritesList:MutableSet<String>
        var favoritesList: MutableSet<String> by observable(mutableSetOf<String>()) { _, _, newValue ->
            sharedPreferences.edit().putStringSet("Favorites", newValue).apply()
        }
        //lateInit var foreCastData: ForeCastData
        /**object ForeCastDataObject {
        var refreshListListeners = HashMap<String, (forecastData: ForecastData) -> Unit>()

        // fires off every time value of the property changes
        var value: ForecastData by observable(
        ForecastData(
        "",
        "",
        "",
        "",
        HashMap()
        )
        ) { property, oldValue, newValue ->
        // do your stuff here
        refreshListListeners.forEach {
        it.value(this.value)
        }
        }
        }**/


    }


    private lateinit var pagerViewAdapter: PagerViewAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var mainContext: Context
    private lateinit var adapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResources = resources
        mainContext = this
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        favoritesList = sharedPreferences.getStringSet("Favorites", LinkedHashSet<String>())!!
        setContentView(R.layout.activity_main)

        // = findViewById(R.id.drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        nav_view.setNavigationItemSelectedListener(this)


        //Requesting location access until user grants it
        locationPermissionManager = LocationPermissionManager(this, ::callback)
        locationPermissionManager.requestLocation()

        findViewById<Button>(R.id.setLocationButton).setOnClickListener {
            val intent = Intent(this, MapActivity()::class.java)
            startActivity(intent)
        }
        //Setup ViewPager
        pagerViewAdapter =
            PagerViewAdapter(supportFragmentManager)
        viewPager.adapter = pagerViewAdapter
        viewPager.offscreenPageLimit = 2
        viewPager.currentItem = 0
        tabLayout.setupWithViewPager(this.viewPager) //Binding ViewPager with TabLayout

    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        actionBarDrawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.home -> Toast.makeText(this, "Clicked item one", Toast.LENGTH_SHORT).show()
            R.id.favorites -> showFavorites()
            R.id.settings -> showSettings()
            R.id.about -> showAbout()
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /**
     * callBack
     * Callback funtion that is called when users location information is acquired
     */
    private fun callback(latLng: LatLng) {
        getWeatherData(latLng)
    }


    /**
     * onForecastData
     * Updates location name,region and country on UI
     */

    /**
     * Dim the background when PopupWindow shows
     */
    private fun PopupWindow.addOnDismissListener(listener: () -> Unit) {
        this.setOnDismissListener {
            val viewParent = window.decorView.rootView as ViewGroup
            val overlay = viewParent.overlay
            overlay.clear()
            listener()
        }
    }


    private fun showPopUpWindows(layout: Int): PopupWindow {
        // Initialize a new layout inflater instance
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val root = findViewById<View>(android.R.id.content).rootView as ViewGroup
        // Inflate a custom view using layout inflater
        val view = inflater.inflate(layout, root, false)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            (displayMetrics.widthPixels * 85 / 100), // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        popupWindow.isOutsideTouchable = true

        val viewParent = window.decorView.rootView as ViewGroup
        val dim = ColorDrawable(Color.BLACK)
        dim.setBounds(0, 0, viewParent.width, viewParent.height)
        dim.alpha = 127
        val overlay = viewParent.overlay
        overlay.add(dim)

        // Set an elevation for the popup window
        popupWindow.elevation = 20.0F


        // If API level 23 or higher then execute the code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Create a new slide animation for popup window enter transition
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.START
            popupWindow.enterTransition = slideIn

            // Slide animation for popup window exit transition
            val slideOut = Slide()
            slideOut.slideEdge = Gravity.START
            popupWindow.exitTransition = slideOut

        }
        popupWindow.setOnDismissListener {
            overlay.clear()
        }

        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(drawer_layout)
        popupWindow.showAtLocation(
            drawer_layout, // Location to display popup window
            Gravity.TOP, // Exact position of layout to display popup
            0, // X offset
            200 // Y offset
        )

        return popupWindow

    }


    private fun showFavorites() {
        searchView.isIconified = false
        customActionBar.openOptionsMenu()
    }

    private fun showSettings() {
        val popupWindow = showPopUpWindows(R.layout.settings_window)
        // Get the widgets reference from custom view
        val cancelButton = popupWindow.contentView.findViewById<Button>(R.id.settingsCancelButton)


        // Set a click listener for popup's button widget
        cancelButton.setOnClickListener {
            // Dismiss the popup window
            popupWindow.dismiss()
        }

        // Set a dismiss listener for popup window
        popupWindow.addOnDismissListener {
            Toast.makeText(
                applicationContext,
                "Popup closed",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private fun showAbout() {
        showPopUpWindows(R.layout.about_window)
    }
}
