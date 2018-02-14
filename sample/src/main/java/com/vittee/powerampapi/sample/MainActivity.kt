package com.vittee.powerampapi.sample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import com.vittee.poweramp.player.*

class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
    private lateinit var mRemoteTrackTime: RemoteTrackTime

    private lateinit var mDuration: TextView
    private lateinit var mElapsed: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.play).setOnClickListener(this)
        findViewById<Button>(R.id.play).setOnLongClickListener(this)
        findViewById<Button>(R.id.pause).setOnClickListener(this)
        findViewById<Button>(R.id.prev).setOnClickListener(this)
        findViewById<Button>(R.id.prev).setOnLongClickListener(this)
        findViewById<Button>(R.id.next).setOnClickListener(this)
        findViewById<Button>(R.id.next).setOnLongClickListener(this)
        findViewById<Button>(R.id.prev).setOnTouchListener(this)
        findViewById<Button>(R.id.next).setOnTouchListener(this)
        findViewById<Button>(R.id.prev_in_cat).setOnClickListener(this)
        findViewById<Button>(R.id.next_in_cat).setOnClickListener(this)
        findViewById<Button>(R.id.repeat).setOnClickListener(this)
        findViewById<Button>(R.id.shuffle).setOnClickListener(this)
        findViewById<Button>(R.id.repeat_all).setOnClickListener(this)
        findViewById<Button>(R.id.repeat_off).setOnClickListener(this)
        findViewById<Button>(R.id.shuffle_all).setOnClickListener(this)
        findViewById<Button>(R.id.shuffle_off).setOnClickListener(this)
        findViewById<Button>(R.id.eq).setOnClickListener(this)

        mDuration = findViewById(R.id.duration)
        mElapsed = findViewById(R.id.elapsed)

        mRemoteTrackTime = RemoteTrackTime(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregister()

        mRemoteTrackTime.apply {
            trackTimeListener = null
            unregister()
        }

//        mTrackReceiver = null
//        mStatusReceiver = null
//        mPlayingModeReceiver = null
    }


    override fun onResume() {
        super.onResume()

        registerAndLoadStatus()
        mRemoteTrackTime.registerAndLoadStatus()
    }

    override fun onPause() {
        unregister()
        mRemoteTrackTime.unregister()

        super.onPause()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.play -> startService(Intent(ACTION_API_COMMAND).putExtra(COMMAND, Commands.TOGGLE_PLAY_PAUSE.value).setPackage(PACKAGE_NAME))
        }
    }

    override fun onLongClick(p0: View?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun registerAndLoadStatus() {
//        // Note, it's not necessary to set mStatusIntent/mPlayingModeIntent this way here,
//        // but this approach can be used with null receiver to get current sticky intent without broadcast receiver.
//        mAAIntent = registerReceiver(mAAReceiver, IntentFilter(PowerampAPI.ACTION_AA_CHANGED))
        mTrackIntent = registerReceiver(mTrackReceiver, IntentFilter(ACTION_TRACK_CHANGED))
//        mStatusIntent = registerReceiver(mStatusReceiver, IntentFilter(PowerampAPI.ACTION_STATUS_CHANGED))
//        mPlayingModeIntent = registerReceiver(mPlayingModeReceiver, IntentFilter(PowerampAPI.ACTION_PLAYING_MODE_CHANGED))
//
//        val filter = IntentFilter()
//        filter.addAction(PowerampAPI.Scanner.ACTION_DIRS_SCAN_STARTED)
//        filter.addAction(PowerampAPI.Scanner.ACTION_DIRS_SCAN_FINISHED)
//        filter.addAction(PowerampAPI.Scanner.ACTION_TAGS_SCAN_STARTED)
//        filter.addAction(PowerampAPI.Scanner.ACTION_TAGS_SCAN_PROGRESS)
//        filter.addAction(PowerampAPI.Scanner.ACTION_TAGS_SCAN_FINISHED)
//        filter.addAction(PowerampAPI.Scanner.ACTION_FAST_TAGS_SCAN_FINISHED)
//
//        if (registerReceiver(mScanReceiver, filter) == null) { // No any scan progress, hide it (progress might be shown previously, before pause/resume)
//            findViewById<View>(R.id.scan_progress).visibility = View.INVISIBLE
//        }
    }

    private fun unregister() {
//        if (mTrackIntent != null) {
//            try {
//                unregisterReceiver(mTrackReceiver)
//            } catch (ex: Exception) {
//            }
//            // Can throw exception if for some reason broadcast receiver wasn't registered.
//        }
//        if (mAAIntent != null) {
//            try {
//                unregisterReceiver(mAAReceiver)
//            } catch (ex: Exception) {
//            }
//
//        }
//
//        if (mStatusReceiver != null) {
//            try {
//                unregisterReceiver(mStatusReceiver)
//            } catch (ex: Exception) {
//            }
//
//        }
//
//        if (mPlayingModeReceiver != null) {
//            try {
//                unregisterReceiver(mPlayingModeReceiver)
//            } catch (ex: Exception) {
//            }
//
//        }
//
//        if (mScanReceiver != null) {
//            try {
//                unregisterReceiver(mScanReceiver)
//            } catch (ex: Exception) {
//            }
//
//        }

    }

    private var mTrackIntent: Intent? = null

    private val mTrackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mTrackIntent = intent
            processTrackIntent()
            Log.w("Main", "mTrackReceiver " + intent)
        }
    }

    private var mCurrentTrack: Bundle? = null

    private fun processTrackIntent() {
        mCurrentTrack = null

        mTrackIntent?.let {
            mCurrentTrack = it.getBundleExtra(TRACK)
            if (mCurrentTrack != null) {
//                val duration = it.getInt(PowerampAPI.Track.DURATION)
//                mRemoteTrackTime.updateTrackDuration(duration) // Let ReomoteTrackTime know about current song duration.
            }

            updateTrackUI()
        }
    }

    private fun updateTrackUI() {
        mCurrentTrack?.let {
            (findViewById<TextView>(R.id.title)).text = it.getString(Track.TITLE.value)
        }

    }
}
