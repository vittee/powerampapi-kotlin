@file:Suppress("unused")

package com.vittee.poweramp.player.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import com.vittee.poweramp.player.*

open class Poweramp(val context: Context) {
    fun togglePlay() = Poweramp.togglePlay(context)

    fun pause() = Poweramp.pause(context)

    fun previous() = Poweramp.previous(context)

    fun next() = Poweramp.next(context)

    fun previousInCategory() = Poweramp.previousInCategory(context)

    fun nextInCategory() = Poweramp.nextInCategory(context)

    fun repeat(mode: RepeatMode) = Poweramp.repeat(context, mode)

    fun cycleRepeatMode(showToast: Boolean = true) = Poweramp.cycleRepeatMode(context, showToast)

    fun shuffle(mode: ShuffleMode) = Poweramp.shuffle(context, mode)

    fun cycleShuffleMode(showToast: Boolean = true) = Poweramp.cycleShuffleMode(context, showToast)

    fun beginFastForward() = Poweramp.beginFastForward(context)

    fun endFastForward() = Poweramp.endFastForward(context)

    fun beginRewind() = Poweramp.beginRewind(context)

    fun endRewind() = Poweramp.endRewind(context)

    fun stop() = Poweramp.stop(context)

    fun seek(position: Int) {
        remoteTrackTime.updateTrackPosition(position)
        Poweramp.seek(context, position)
    }

    fun synchronizePosition(receiver: BroadcastReceiver? = null) = Poweramp.synchronizePosition(context, receiver)

    fun openToPlay(uri: Uri) = Poweramp.openToPlay(context, uri)

    inline fun createContentUri(f: Uri.Builder.() -> Unit): Uri = Poweramp.createContentUri(f)

    private val listeners = ArrayList<Listener>()

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    private val remoteTrackTime: RemoteTrackTime = RemoteTrackTime(context).also {
        it.trackTimeListener = object : RemoteTrackTime.TrackTimeListener {
            override fun onTrackDurationChanged(duration: Int) {
                listeners.forEach { it.onTrackDurationChanged(duration) }
            }

            override fun onTrackPositionChanged(position: Int) {
                listeners.forEach { it.onTrackPositionChanged(position) }
            }
        }
    }

    private val statusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var paused = true

            // Each status update can contain track position update as well.
            intent.getIntExtra(Track.POSITION.value, -1).let { pos ->
                if (pos != -1) {
                    remoteTrackTime.updateTrackPosition(pos)
                }
            }

            when (intent.getIntExtra(EXTRA_STATUS, -1)) {
                Status.TRACK_PLAYING.value -> {
                    paused = intent.getBooleanExtra(EXTRA_PAUSED, false)
                    when {
                        paused -> remoteTrackTime.stopSongProgress()
                        else -> remoteTrackTime.startSongProgress()
                    }
                }

                Status.TRACK_ENDED.value, Status.PLAYING_ENDED.value -> {
                    remoteTrackTime.stopSongProgress()
                }

                Status.TRACK_ENDED.value, Status.PLAYING_ENDED.value -> remoteTrackTime.stopSongProgress()
            }

            listeners.forEach {
                it.onPlaybackStatusChanged(paused)
            }
        }
    }

    private val trackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            TrackInfo(intent.getBundleExtra(EXTRA_TRACK)).let { info ->
                remoteTrackTime.updateTrackDuration(info.duration ?: 0)
                listeners.forEach { it.onTrackChanged(info) }
            }
        }
    }

    private val playingModeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            PlayingMode(intent).let { mode ->
                listeners.forEach { it.onPlayingModeChanged(mode) }
            }
        }
    }

    private val albumArtReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bitmap = when {
                intent.hasExtra(EXTRA_ALBUM_ART_BITMAP) -> intent.getParcelableExtra<Bitmap>(EXTRA_ALBUM_ART_BITMAP)
                else -> null
            }

            listeners.forEach {
                it.onAlbumArt(bitmap)
            }
        }
    }

    fun register() {
        context.registerStatusReceiver(statusReceiver)
        context.registerTrackReceiver(trackReceiver)
        context.registerPlayingModeReceiver(playingModeReceiver)
        context.registerAlbumArtReceiver(albumArtReceiver)

        remoteTrackTime.registerAndLoadStatus()
    }

    fun unregister() {

        with(context) {
            val receivers = arrayOf(
                    statusReceiver,
                    trackReceiver,
                    playingModeReceiver,
                    albumArtReceiver
            )

            receivers.forEach {
                try {
                    unregisterReceiver(it)
                } catch (e: Exception) {
                }
            }
        }

        remoteTrackTime.unregister()
    }

    companion object {
        fun togglePlay(context: Context, keepService: Boolean? = null) {
            Commands.TogglePlayPause.createIntent().apply {
                keepService?.let {
                    putExtra(EXTRA_KEEP_SERVICE, it)
                }

                context.startService(this)
            }
        }

        fun pause(context: Context, keepService: Boolean? = null) {
            Commands.Pause.createIntent().apply {
                keepService?.let {
                    putExtra(EXTRA_KEEP_SERVICE, it)
                }

                context.startService(this)
            }
        }

        fun resume(context: Context) {
            Commands.Pause.execute(context)
        }

        fun previous(context: Context) {
            Commands.Previous.execute(context)
        }

        fun next(context: Context) {
            Commands.Next.execute(context)
        }

        fun previousInCategory(context: Context) {
            Commands.PreviousInCat.execute(context)
        }

        fun nextInCategory(context: Context) {
            Commands.NextInCat.execute(context)
        }

        fun repeat(context: Context, mode: RepeatMode) {
            Commands.Repeat.createIntent().apply {
                putExtra(EXTRA_REPEAT, mode.value)
                //
                context.startService(this)
            }
        }

        fun cycleRepeatMode(context: Context, showToast: Boolean = true) {
            Commands.Repeat.createIntent().apply {
                putExtra(EXTRA_SHOW_TOAST, showToast)
                //
                context.startService(this)
            }
        }

        fun shuffle(context: Context, mode: ShuffleMode) {
            Commands.Shuffle.createIntent().apply {
                putExtra(EXTRA_SHUFFLE, mode.value)
                //
                context.startService(this)
            }
        }

        fun cycleShuffleMode(context: Context, showToast: Boolean = true) {
            Commands.Shuffle.createIntent().apply {
                putExtra(EXTRA_SHOW_TOAST, showToast)
                //
                context.startService(this)
            }
        }

        fun beginFastForward(context: Context) {
            Commands.BeginFastForward.execute(context)
        }

        fun endFastForward(context: Context) {
            Commands.EndFastForward.execute(context)
        }

        fun beginRewind(context: Context) {
            Commands.BeginRewind.execute(context)
        }

        fun endRewind(context: Context) {
            Commands.EndRewind.execute(context)
        }

        fun stop(context: Context) {
            Commands.Stop.execute(context)
        }

        fun seek(context: Context, position: Int) {
            Commands.Seek.createIntent().apply {
                putExtra(Track.POSITION.value, position)
                //
                context.startService(this)
            }
        }

        fun synchronizePosition(context: Context, receiver: BroadcastReceiver?) {
            receiver?.let {
                context.registerReceiver(it, IntentFilter(ACTION_TRACK_POS_SYNC))
            }

            Commands.PosSync.execute(context)
        }

        fun openToPlay(context: Context, uri: Uri) {
            Commands.OpenToPlay.createIntent().apply {
                data = uri
                //
                context.startService(this)
            }
        }

        private fun Commands.createIntent(): Intent {
            return Intent(ACTION_API_COMMAND)
                    .putExtra(EXTRA_COMMAND, value)
                    .setComponent(POWERAMP_PLAYER_SERVICE_COMPONENT_NAME)
        }

        private fun Commands.execute(context: Context) = createIntent().let(context::startService)

        inline fun createContentUri(f: Uri.Builder.() -> Unit): Uri {
            return POWERAMP_ROOT_URI.buildUpon().apply(f).build()
        }

        fun Context.registerTrackReceiver(receiver: BroadcastReceiver): Intent? =
                registerReceiver(receiver, IntentFilter(ACTION_TRACK_CHANGED))

        fun Context.registerAlbumArtReceiver(receiver: BroadcastReceiver): Intent? =
                registerReceiver(receiver, IntentFilter(ACTION_AA_CHANGED))

        fun Context.registerStatusReceiver(receiver: BroadcastReceiver): Intent? =
                registerReceiver(receiver, IntentFilter(ACTION_STATUS_CHANGED))

        fun Context.registerPlayingModeReceiver(receiver: BroadcastReceiver): Intent? =
                registerReceiver(receiver, IntentFilter(ACTION_PLAYING_MODE_CHANGED))
    }

    class TrackInfo(private val bundle: Bundle?) {
        val valid = bundle != null

        val id
            get() = bundle?.getLong(Track.ID.value)

        val cat
            get() = bundle?.getInt(Track.CAT.value)

        val uri: Uri?
            get() = bundle?.getParcelable(Track.CAT_URI.value)

        val title
            get() = bundle?.getString(Track.TITLE.value)

        val album
            get() = bundle?.getString(Track.ALBUM.value)

        val artist
            get() = bundle?.getString(Track.ARTIST.value)

        val path
            get() = bundle?.getString(Track.PATH.value)

        val codec
            get() = bundle?.getString(Track.CODEC.value)

        val bitrate
            get() = bundle?.getInt(Track.BITRATE.value)

        val samplerate
            get() = bundle?.getInt(Track.SAMPLE_RATE.value)

        val channels
            get() = bundle?.getInt(Track.CHANNELS.value)

        val duration
            get() = bundle?.getInt(Track.DURATION.value)
    }

    class PlayingMode(private val intent: Intent) {
        val shuffleMode
            get() = ShuffleMode.fromInt(intent.getIntExtra(EXTRA_SHUFFLE, -1))

        val repeatMode
            get() = RepeatMode.fromInt(intent.getIntExtra(EXTRA_REPEAT, -1))

    }

    interface Listener : RemoteTrackTime.TrackTimeListener {
        fun onPlaybackStatusChanged(paused: Boolean)

        fun onTrackChanged(track: TrackInfo)

        fun onPlayingModeChanged(mode: PlayingMode)

        fun onAlbumArt(bitmap: Bitmap?)
    }
}


