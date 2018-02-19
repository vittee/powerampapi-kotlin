package com.vittee.poweramp.player.api

import android.content.Context
import android.content.Intent
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

    companion object {
        fun togglePlay(context: Context, keepService: Boolean? = null) {
            Commands.TOGGLE_PLAY_PAUSE.createIntent().apply {
                keepService?.let {
                    putExtra(EXTRA_KEEP_SERVICE, it)
                }

                context.startService(this)
            }
        }

        fun pause(context: Context, keepService: Boolean? = null) {
            Commands.PAUSE.createIntent().apply {
                keepService?.let {
                    putExtra(EXTRA_KEEP_SERVICE, it)
                }

                context.startService(this)
            }
        }

        fun resume(context: Context) {
            Commands.PAUSE.execute(context)
        }

        fun previous(context: Context) {
            Commands.PREVIOUS.execute(context)
        }

        fun next(context: Context) {
            Commands.NEXT.execute(context)
        }

        fun previousInCategory(context: Context) {
            Commands.PREVIOUS_IN_CAT.execute(context)
        }

        fun nextInCategory(context: Context) {
            Commands.NEXT_IN_CAT.execute(context)
        }

        fun repeat(context: Context, mode: RepeatMode) {
            Commands.REPEAT.createIntent().apply {
                putExtra(EXTRA_REPEAT, mode.value)
                //
                context.startService(this)
            }
        }

        fun cycleRepeatMode(context: Context, showToast: Boolean = true) {
            Commands.REPEAT.createIntent().apply {
                putExtra(EXTRA_SHOW_TOAST, showToast)
                //
                context.startService(this)
            }
        }

        private fun Commands.createIntent() = Intent(ACTION_API_COMMAND).putExtra(EXTRA_COMMAND, value).setPackage(POWERAMP_PACKAGE_NAME)

        private fun Commands.execute(context: Context) = createIntent().let(context::startService)
    }
}
