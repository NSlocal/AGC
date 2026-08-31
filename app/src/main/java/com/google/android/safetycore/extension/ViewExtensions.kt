package com.google.android.safetycore.extension

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

fun View.fadeIn(durationMs: Long = 300) {
    val anim = AlphaAnimation(0f, 1f)
    anim.duration = durationMs
    anim.fillAfter = true
    startAnimation(anim)
    visibility = View.VISIBLE
}

fun View.fadeOut(durationMs: Long = 300, onEnd: (() -> Unit)? = null) {
    val anim = AlphaAnimation(1f, 0f)
    anim.duration = durationMs
    anim.fillAfter = true
    anim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            visibility = View.GONE
            onEnd?.invoke()
        }
        override fun onAnimationRepeat(animation: Animation?) {}
    })
    startAnimation(anim)
}

fun View.setGoneIf(condition: Boolean) {
    visibility = if (condition) View.GONE else View.VISIBLE
}
