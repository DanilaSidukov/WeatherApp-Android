package com.sidukov.weatherapp.ui.fragment_weather

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd

class HeaderImageAnimation(view: View) {

    private val animationDuration = 5000L
    var marginFlow = view.measuredWidth

    private val animationFirst = ValueAnimator.ofFloat(0f, marginFlow.toFloat() / 4).also {
        it.duration = animationDuration
        it.interpolator = LinearInterpolator()
    }
    private val animationSecond =ValueAnimator.ofFloat(marginFlow.toFloat() / 4, 0f).also {
        it.duration = animationDuration
        it.interpolator = LinearInterpolator()
    }
    private val animationThird =ValueAnimator.ofFloat(0f, -marginFlow.toFloat() / 8).also {
        it.duration = animationDuration
        it.interpolator = LinearInterpolator()
    }
    private val animationForth =ValueAnimator.ofFloat(-marginFlow.toFloat() / 8, 0f).also {
        it.duration = animationDuration
        it.interpolator = LinearInterpolator()
    }
    init{
        executeAnimation()
    }


    private val animatedImage = view

    private fun performAnimation(action: ValueAnimator) {
        action.apply {
            addUpdateListener { moveObject ->
                animatedImage.translationX = moveObject.animatedValue as Float
            }
        }
    }

    fun executeAnimation() {
        animationFirst.apply {
            start()
            performAnimation(this)
            doOnEnd {
                animationSecond.start()
            }
        }

        animationSecond.apply {
            performAnimation(this)
            doOnEnd {
                animationThird.start()
            }
        }

        animationThird.apply {
            performAnimation(this)
            doOnEnd {
                animationForth.start()
            }
        }

        animationForth.apply {
            performAnimation(this)
            doOnEnd {
                animationFirst.start()
            }
        }
    }


}