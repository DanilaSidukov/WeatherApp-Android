package com.sidukov.weatherapp.ui

import android.animation.ValueAnimator
import android.icu.text.ListFormatter.Width
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd

class HeaderAnimationImage(view: View) {

    private val animationDuration = 5000L
    val startWidth = view.measuredWidth
    var marginFlow = view.measuredWidth

    lateinit var animationFirst: ValueAnimator
    lateinit var animationSecond: ValueAnimator
    lateinit var animationThird: ValueAnimator
    lateinit var animationForth: ValueAnimator

    fun checkRunning(): Boolean{
        return animationFirst.isRunning || animationSecond.isRunning || animationThird.isRunning || animationForth.isRunning
    }

    fun stopRunning(){
        animationFirst.cancel()
        animationSecond.cancel()
        animationThird.cancel()
        animationForth.cancel()
    }

    fun bigAnimationWidth(newWidth: Int){
        animationFirst = ValueAnimator.ofFloat(0f, newWidth.toFloat() / 2).also {
            it.duration = animationDuration
            it.interpolator = LinearInterpolator()
        }
        animationSecond =ValueAnimator.ofFloat(newWidth.toFloat() / 2, 0f).also {
            it.duration = animationDuration
            it.interpolator = LinearInterpolator()
        }
        animationThird =ValueAnimator.ofFloat(0f, -newWidth.toFloat() / 2).also {
            it.duration = animationDuration
            it.interpolator = LinearInterpolator()
        }
        animationForth =ValueAnimator.ofFloat(-newWidth.toFloat() / 2, 0f).also {
            it.duration = animationDuration
            it.interpolator = LinearInterpolator()
        }
    }

    fun smallAnimationWidth(smallWidth: Int){
        animationFirst = ValueAnimator.ofFloat(0f, smallWidth.toFloat() / 2).also {
            it.duration = animationDuration
            it.interpolator = LinearInterpolator()
        }
        animationSecond =ValueAnimator.ofFloat(smallWidth.toFloat() / 2, 0f).also {
            it.duration = animationDuration
            it.interpolator = LinearInterpolator()
        }
        animationThird =ValueAnimator.ofFloat(0f, -smallWidth.toFloat()/4).also {
            it.duration = animationDuration
            it.interpolator = LinearInterpolator()
        }
        animationForth =ValueAnimator.ofFloat(-smallWidth.toFloat() /4, 0f).also {
            it.duration = animationDuration
            it.interpolator = LinearInterpolator()
        }
    }

    init {
        bigAnimationWidth(startWidth)
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
            if (marginFlow == startWidth) {
                performAnimation(this)
            } else {
                performAnimation(this)
            }
            doOnEnd {
                animationSecond.start()
            }
        }

        animationSecond.apply {
            if (marginFlow == startWidth){
                start()
                performAnimation(this)
            } else {
                start()
                performAnimation(this)
            }
            doOnEnd {
                animationThird.start()
            }
        }

        animationThird.apply {
            if (marginFlow == startWidth){
                start()
                performAnimation(this)
            } else {
                start()
                performAnimation(this)
            }
            doOnEnd {
                animationForth.start()
            }
        }

        animationForth.apply {
            if (marginFlow == startWidth){
                start()
                performAnimation(this)
            } else {
                start()
                performAnimation(this)
            }
            doOnEnd {
                animationFirst.start()
            }
        }
    }


}