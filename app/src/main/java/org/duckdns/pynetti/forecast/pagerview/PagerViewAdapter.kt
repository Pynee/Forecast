package org.duckdns.pynetti.forecast.pagerview

/*
PagerViewAdapter handles view Fragments

 */


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.duckdns.pynetti.forecast.graphview.GraphView
import org.duckdns.pynetti.forecast.MainActivity.Companion.activityResources
import org.duckdns.pynetti.forecast.R
import org.duckdns.pynetti.forecast.forecastview.ForecastView


internal class PagerViewAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                ForecastView()
            }
            1 -> {
                GraphView()
            }

            else -> ForecastView()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        if (position == 0) {
            title = activityResources.getString(R.string.forecast)
        } else if (position == 1) {
            title = activityResources.getString(R.string.graph)
        }
        return title
    }
}

