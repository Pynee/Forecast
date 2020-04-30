package org.duckdns.pynetti.forecast.forecastview

/*
ForecastDayItemView is Layout that includes Morning, Afternoon and evening forecasts

 */
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.day_item.view.*
import org.duckdns.pynetti.forecast.R


class ForecastDayItemView : FrameLayout {

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
        inflate(context, R.layout.day_item, this)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                it,
                R.styleable.ForecastDayItemView, 0, 0
            )
            val title = resources.getText(
                typedArray
                    .getResourceId(
                        R.styleable.ForecastDayItemView_dateString,
                        R.string.today
                    )
            )
            dayTextView.text = title
            typedArray.recycle()
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}
