package com.vittee.poweramp.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import com.vittee.poweramp.player.api.Poweramp


private const val UPDATE_DELAY = 1000L

class RemoteTrackTime(private val context: Context) {
    private val mHandler = Handler()
    var trackTimeListener: TrackTimeListener? = null

    private var mPosition: Int = 0
    private var mPlaying: Boolean = false
    private var mStartTimeMs: Long = 0
    private var mStartPosition: Int = 0


    private val mTickRunnable = object : Runnable {
        override fun run() {
            mPosition = (System.currentTimeMillis() - mStartTimeMs + 500).toInt() / 1000 + mStartPosition
            Log.w("RemoteTrackTime", "mTickRunnable mPosition=$mPosition")

            trackTimeListener?.onTrackPositionChanged(mPosition)

            mHandler.removeCallbacks(this)
            mHandler.postDelayed(this, UPDATE_DELAY)
        }
    }

    private val mTrackPosSyncReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pos = intent.getIntExtra(Track.POSITION.value, 0)
            Log.w("RemoteTrackTime", "mTrackPosSyncReceiver sync=$pos")
            updateTrackPosition(pos)
        }

    }

    fun registerAndLoadStatus() {
        Poweramp.synchronizePosition(context, mTrackPosSyncReceiver)

        if (mPlaying) {
            mHandler.removeCallbacks(mTickRunnable)
            mHandler.postDelayed(mTickRunnable, 0)
        }
    }

    fun unregister() {
        try {
            context.unregisterReceiver(mTrackPosSyncReceiver)
        } catch (ex: Exception) {

        }

        mHandler.removeCallbacks(mTickRunnable)
    }

    interface TrackTimeListener {
        fun onTrackDurationChanged(duration: Int)
        fun onTrackPositionChanged(position: Int)
    }

    fun updateTrackPosition(pos: Int) {
        mPosition = pos
        if (mPlaying) {
            mStartTimeMs = System.currentTimeMillis()
            mStartPosition = mPosition
        }


        trackTimeListener?.onTrackPositionChanged(pos)

    }

    fun startSongProgress() {
        if (mPlaying) return

        mStartTimeMs = System.currentTimeMillis()
        mStartPosition = mPosition
        mHandler.removeCallbacks(mTickRunnable)
        mHandler.postDelayed(mTickRunnable, UPDATE_DELAY)
        mPlaying = true
    }

    fun stopSongProgress() {
        if (!mPlaying) return

        mHandler.removeCallbacks(mTickRunnable)
        mPlaying = false

    }

    fun updateTrackDuration(duration: Int) {
        //mDuration = duration;
        trackTimeListener?.onTrackDurationChanged(duration)
    }
}