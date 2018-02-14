package com.vittee.poweramp.player

import android.content.Context
import android.content.IntentFilter
import android.os.Handler


class RemoteTrackTime(private val context: Context) {

    private val mHandler = Handler()
    var trackTimeListener: TrackTimeListener? = null

    private var mPlaying: Boolean = false

//    private val mTickRunnable = object : Runnable {
//        override fun run() {
//            mPosition = (System.currentTimeMillis() - mStartTimeMs + 500) as Int / 1000 + mStartPosition
//            if (LOG) Log.w(TAG, "mTickRunnable mPosition=" + mPosition)
//            if (mTrackTimeListener != null) {
//                mTrackTimeListener.onTrackPositionChanged(mPosition)
//            }
//            mHandler.removeCallbacks(this)
//            mHandler.postDelayed(this, UPDATE_DELAY)
//        }
//    }

    fun registerAndLoadStatus() {
        val filter = IntentFilter(ACTION_TRACK_POS_SYNC)

//        context.registerReceiver(mTrackPosSyncReceiver, filter)
//        context.startService(PowerampAPI.newAPIIntent().putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.POS_SYNC))

        if (mPlaying) {
//            mHandler.removeCallbacks(mTickRunnable)
//            mHandler.postDelayed(mTickRunnable, 0)
        }
    }

    fun unregister() {
//        if (mTrackPosSyncReceiver != null) {
//            try {
//                context.unregisterReceiver(mTrackPosSyncReceiver)
//            } catch (ex: Exception) {
//
//            }
//
//        }
//
//        mHandler.removeCallbacks(mTickRunnable)
    }

    interface TrackTimeListener {
        fun onTrackDurationChanged(duration: Int)
        fun onTrackPositionChanged(position: Int)
    }
}