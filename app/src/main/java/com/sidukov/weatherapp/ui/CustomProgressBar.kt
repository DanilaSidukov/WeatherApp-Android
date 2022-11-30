package com.sidukov.weatherapp.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // remove
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    val generalPaint = Paint().apply {
        // TODO: extract from attribute - это всё потом, когда все стил и цвета будут вынесены в styles.xml и themes.xml
        color = Color.rgb(220,223,230)
        strokeWidth = 7f
        style = Paint.Style.STROKE
    }
    private val generalPath: DashPathEffect = DashPathEffect (floatArrayOf(40f, 20f),0f )

    val conditionPaint = Paint().apply {
        color = Color.rgb(0, 179, 122)
        strokeWidth = 7f
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        generalPaint.pathEffect = generalPath
        canvas.drawArc(
            this.left.toFloat() + 60f,
            this.top.toFloat() + 60f,
            this.right.toFloat() - 60f,
            this.bottom.toFloat(),
            200f,
            140f,
            false,
            generalPaint
        )
        canvas.drawArc(
            this.left.toFloat() + 60f,
            this.top.toFloat() + 60f,
            this.right.toFloat() - 60f,
            this.bottom.toFloat(),
            200f,
            90f,
            false,
            conditionPaint
        )
    }

}