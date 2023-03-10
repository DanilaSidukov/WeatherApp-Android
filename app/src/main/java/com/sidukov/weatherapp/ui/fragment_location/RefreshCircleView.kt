package com.sidukov.weatherapp.ui.fragment_location

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.withRotation
import com.sidukov.weatherapp.R
import com.simform.refresh.SSAnimationView
import java.time.LocalDateTime

class RefreshCircleView(context: Context) : SSAnimationView(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 12f
        style = Paint.Style.STROKE
        color =
            if (LocalDateTime.now().hour in 22.. 23 || LocalDateTime.now().hour in 0..6) ContextCompat.getColor(context, R.color.grey_2)
            else ContextCompat.getColor(context, R.color.black)
    }
    private var animator: ValueAnimator? = null
    private var angle = 0f

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        canvas.withRotation(
            angle,
            width / 2f,
            height / 2f
        ) {
            drawArc(
                width / 2f - 30f,
                height / 2f - 30f,
                width / 2f + 30f,
                height / 2f + 30f,
                0f,
                310f,
                false,
                paint
            )
        }
    }

    private fun createAnimation(): ValueAnimator {
        return ValueAnimator.ofFloat(0f, 360f).apply {
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                angle = it.animatedValue as Float
                duration = 700L
                interpolator = LinearInterpolator()
                invalidate()
            }
        }
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }

    override fun pullToRefresh() {
        animator = createAnimation().apply {
            start()
        }
    }

    override fun pullProgress(pullDistance: Float, pullProgress: Float) {
    }

    override fun refreshComplete() {
        animator?.cancel()
    }

    override fun refreshing() {

    }

    override fun releaseToRefresh() {

    }

    override fun reset() {

    }

}