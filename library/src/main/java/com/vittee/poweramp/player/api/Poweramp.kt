package com.vittee.poweramp.player.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
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
    fun seek(position: Int) = Poweramp.seek(context, position)
    fun synchronizePosition(receiver: BroadcastReceiver? = null) = Poweramp.synchronizePosition(context, receiver)
    fun openToPlay(uri: Uri) = Poweramp.openToPlay(context, uri)

    inline fun createContentUri(f: Uri.Builder.() -> Unit): Uri = Poweramp.createContentUri(f)

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
    }
}


