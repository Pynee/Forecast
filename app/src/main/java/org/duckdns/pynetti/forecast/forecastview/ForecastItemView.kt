package org.duckdns.pynetti.forecast.forecastview

/*
ForecastItemView is layout that includes single forecast datapoint (Temperature,Precipication,WindSpeed and Humidity)

 */

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.weather_item.view.*
import org.duckdns.pynetti.forecast.R


class ForecastItemView : FrameLayout {

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        // Load attributes
        LayoutInflater.from(context).inflate(R.layout.weather_item, this, true)
        setPadding(2, 0, 2, 0)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                it,
                R.styleable.ForecastItemView, 0, 0
            )
            val title = resources.getText(
                typedArray
                    .getResourceId(
                        R.styleable.ForecastItemView_timeString,
                        R.string.morning
                    )
            )
            DateView.text = title
            typedArray.recycle()
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}
