package com.vittee.poweramp.player

const val PACKAGE_NAME = "com.maxmpz.audioplayer"
const val VERSION: Int = 533

const val ACTION_API_COMMAND = "com.maxmpz.audioplayer.API_COMMAND"
const val COMMAND = "cmd"

const val ACTION_TRACK_POS_SYNC = "com.maxmpz.audioplayer.TPOS_SYNC"
const val ACTION_TRACK_CHANGED = "com.maxmpz.audioplayer.TRACK_CHANGED"

const val TRACK = "track"

enum class Commands(val value: Int) {
    TOGGLE_PLAY_PAUSE(1),
    PAUSE(2),
    RESUME(3),
    NEXT(4),
    PREVIOUS(5)
}

enum class Track(val value: String) {
    TITLE("title")
}