package com.sidukov.weatherapp.ui.fragment_weather

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.location.Geocoder
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.remote.WeatherRepository
import com.sidukov.weatherapp.data.remote.api.APIClient
import com.sidukov.weatherapp.domain.WeatherShort
import com.sidukov.weatherapp.ui.fragment_location.LocationViewAdapter
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoField

class ArcProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.style.ArcProgressBar
) : View(context, attrs, defStyleAttr) {

    var sunGetCondition: Float = 0f
    set(value) {
        field = value
        invalidate()
    }

    private val backgroundPaint = Paint().apply {
        strokeWidth = 7f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(40f, 20f), 0f)
    }
    private val progressPaint = Paint().apply {
        strokeWidth = 7f
        style = Paint.Style.STROKE
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.ArcProgressBar) {
            backgroundPaint.apply {
                color = getColor(R.styleable.ArcProgressBar_backgroundTrackColor, Color.GREEN)
            }
            progressPaint.apply {
                color = getColor(R.styleable.ArcProgressBar_progressTrackColor, Color.GRAY)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(
            this.left.toFloat() + 60f,
            this.top.toFloat() + 60f,
            this.right.toFloat() - 60f,
            this.bottom.toFloat(),
            200f,
            140f,
            false,
            backgroundPaint
        )
        canvas.drawArc(
            this.left.toFloat() + 60f,
            this.top.toFloat() + 60f,
            this.right.toFloat() - 60f,
            this.bottom.toFloat(),
            200f,
            // start - 0, end - 140
            sunGetCondition,
            false,
            progressPaint
        )
    }
}