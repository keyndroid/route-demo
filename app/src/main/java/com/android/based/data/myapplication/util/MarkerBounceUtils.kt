package com.android.based.data.myapplication.util

import android.R
import android.os.Handler
import android.os.SystemClock
import android.view.animation.BounceInterpolator
import android.view.animation.Interpolator
import com.google.android.gms.maps.model.Marker
import java.util.*

class MarkerBounceUtils {
    private var mHandler: Handler? = null
    private var mAnimation: Runnable? = null
    private var marker:Marker? = null

    public constructor(marker:Marker){
        this.marker=marker
        mHandler = Handler()
    }

    public fun startAnimation(){
        val calender=Calendar.getInstance();
        val current=calender.time.time
        calender.add(Calendar.HOUR_OF_DAY, 1)

        mAnimation = BounceAnimation(
            0,
           1000*60*5,
            marker!!,
            mHandler!!
        )
        mHandler?.post(mAnimation)
    }
    public fun stopAnimation(){
        mHandler?.removeCallbacks(mAnimation)
    }
    /**
     * Performs a bounce animation on a [Marker].
     */
    private class BounceAnimation constructor(
        private val mStart: Long,
        private val mDuration: Long,
        private val mMarker: Marker,
        handler: Handler
    ) :
        Runnable {
        private val mInterpolator: Interpolator
        private val mHandler: Handler
        override fun run() {
            val elapsed = SystemClock.uptimeMillis() - mStart
            val t = Math.max(
                1 - mInterpolator.getInterpolation(elapsed.toFloat() / mDuration),
                0f
            )
            mMarker.setAnchor(0.5f, 1.0f + 1.2f * t)
            if (t > 0.0) {
                // Post again 16ms later.
                mHandler.postDelayed(this, 16L)
            }
        }

        init {
            mHandler = handler
            mInterpolator = BounceInterpolator()
        }
    }
}