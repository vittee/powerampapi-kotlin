package com.vittee.poweramp.player

import android.content.ComponentName
import android.content.Intent
import android.net.Uri


const val PACKAGE_NAME = "com.maxmpz.audioplayer"
const val PLAYER_SERVICE_NAME = "com.maxmpz.audioplayer.player.PlayerService"

const val VERSION: Int = 533

/**
 * No id flag.
 */
const val NO_ID = 0L

const val AUTHORITY = "com.maxmpz.audioplayer.data"

val ROOT_URI: Uri = Uri.Builder().scheme("content").authority(AUTHORITY).build()

val PLAYER_SERVICE_COMPONENT_NAME = ComponentName(PACKAGE_NAME, PLAYER_SERVICE_NAME)

const val ACTION_API_COMMAND = "com.maxmpz.audioplayer.API_COMMAND"
const val COMMAND = "cmd"

const val ACTION_TRACK_CHANGED = "com.maxmpz.audioplayer.TRACK_CHANGED"
const val ACTION_AA_CHANGED = "com.maxmpz.audioplayer.AA_CHANGED"
const val ACTION_STATUS_CHANGED = "com.maxmpz.audioplayer.STATUS_CHANGED"
const val ACTION_TRACK_POS_SYNC = "com.maxmpz.audioplayer.TPOS_SYNC"
const val ACTION_PLAYING_MODE_CHANGED = "com.maxmpz.audioplayer.PLAYING_MODE_CHANGED"

const val ACTION_EQU_CHANGED = "com.maxmpz.audioplayer.EQU_CHANGED"
const val ACTION_SHOW_CURRENT = "com.maxmpz.audioplayer.ACTION_SHOW_CURRENT"
const val ACTION_SHOW_LIST = "com.maxmpz.audioplayer.ACTION_SHOW_LIST"

const val TRACK = "track"

const val ALBUM_ART_PATH = "aaPath"
const val ALBUM_ART_BITMAP = "aaBitmap"

const val DELAYED = "delayed"
const val TIMESTAMP = "ts"

const val STATUS = "status"
const val FAILED = "failed"
const val PAUSED = "paused"
const val SHUFFLE = "shuffle"
const val REPEAT = "repeat"


enum class Status(val value: Int) {
    TRACK_PLAYING(1),
    TRACK_ENDED(2),
    PLAYING_ENDED(3)
}

enum class Commands(val value: Int) {
    TOGGLE_PLAY_PAUSE(1),
    PAUSE(2),
    RESUME(3),
    NEXT(4),
    PREVIOUS(5),
    NEXT_IN_CAT(6),
    PREVIOUS_IN_CAT(7),
    REPEAT(8),
    SHUFFLE(9),
    BEGIN_FAST_FORWARD(10),
    END_FAST_FORWARD(11),
    BEGIN_REWIND(12),
    END_REWIND(13),
    STOP(14),
    SEEK(15),
    POS_SYNC(16),
    OPEN_TO_PLAY(20),
    SET_EQU_PRESET(50),
    SET_EQU_STRING(51),
    SET_EQU_BAND(52),
    SET_EQU_ENABLED(53),
    STOP_SERVICE(100)
}

const val ID = "id"
const val NAME = "name"
const val VALUE = "value"
const val EQU = "equ"
const val TONE = "tone"

enum class Track(val value: String) {
    ID("id"),
    REAL_ID("realId"),
    TYPE("type"),
    CAT("cat"),
    IS_CUE("isCue"),
    CAT_URI("catUri"),
    FILE_TYPE("fileType"),
    PATH("path"),
    TITLE("title"),
    ALBUM("album"),
    ARTIST("artist"),
    DURATION("dur"),
    POSITION("pos"),
    POS_IN_LIST("posInList"),
    LIST_SIZE("listSize"),
    SAMPLE_RATE("sampleRate"),
    CHANNELS("channels"),
    BITRATE("bitRate"),
    CODEC("codec"),
    FLAGS("flags");

    enum class FileType(val value: Int) {
        mp3(0),
        flac(1),
        m4a(2),
        mp4(3),
        ogg(4),
        wma(5),
        wav(6),
        tta(7),
        ape(8),
        wv(9),
        aac(10),
        mpga(11),
        amr(12),
        _3gp(13),
        mpc(14),
        aiff(15),
        aif(16)
    }

    enum class Flags(val value: Int) {
        FLAG_ADVANCE_NONE(0),
        FLAG_ADVANCE_FORWARD(1),
        FLAG_ADVANCE_BACKWARD(2),
        FLAG_ADVANCE_FORWARD_CAT(3),
        FLAG_ADVANCE_BACKWARD_CAT(4),
        FLAG_ADVANCE_MASK(0b111),
        FLAG_NOTIFICATION_UI(0x20),
        FLAG_FIRST_IN_PLAYER_SESSION(0x40)
    }
}

enum class ShuffleMode(val value: Int) {
    SHUFFLE_NONE(0),
    SHUFFLE_ALL(1),
    SHUFFLE_SONGS(2),
    SHUFFLE_CATS(3), // Songs in order.
    SHUFFLE_SONGS_AND_CATS(4) // Songs shuffled.
}

enum class RepeatMode(val value: Int) {
    REPEAT_NONE(0),
    REPEAT_ON(1),
    REPEAT_ADVANCE(2),
    REPEAT_SONG(3)
}

class Scanner {
    companion object {
        @JvmStatic
        val ACTION_SCAN_DIRS = "com.maxmpz.audioplayer.ACTION_SCAN_DIRS"
        @JvmStatic
        val ACTION_SCAN_TAGS = "com.maxmpz.audioplayer.ACTION_SCAN_TAGS"
        @JvmStatic
        val ACTION_DIRS_SCAN_STARTED = "com.maxmpz.audioplayer.ACTION_DIRS_SCAN_STARTED"
        @JvmStatic
        val ACTION_DIRS_SCAN_FINISHED = "com.maxmpz.audioplayer.ACTION_DIRS_SCAN_FINISHED"
        @JvmStatic
        val ACTION_TAGS_SCAN_STARTED = "com.maxmpz.audioplayer.ACTION_TAGS_SCAN_STARTED"
        @JvmStatic
        val ACTION_TAGS_SCAN_PROGRESS = "com.maxmpz.audioplayer.ACTION_TAGS_SCAN_PROGRESS"
        @JvmStatic
        val ACTION_TAGS_SCAN_FINISHED = "com.maxmpz.audioplayer.ACTION_TAGS_SCAN_FINISHED"
        @JvmStatic
        val ACTION_FAST_TAGS_SCAN_FINISHED = "com.maxmpz.audioplayer.ACTION_FAST_TAGS_SCAN_FINISHED"

        /**
         * Extra.
         * Boolean.
         */
        @JvmStatic
        val EXTRA_FAST_SCAN = "fastScan"
        /**
         * Extra.
         * Int.
         */
        @JvmStatic
        val EXTRA_PROGRESS = "progress"
        /**
         * Extra.
         * Boolean.
         */
        @JvmStatic
        val EXTRA_TRACK_CONTENT_CHANGED = "trackContentChanged"

        /**
         * Extra.
         * Boolean.
         */
        @JvmStatic
        val EXTRA_ERASE_TAGS = "eraseTags"

        /**
         * Extra.
         * Boolean.
         */
        @JvmStatic
        val EXTRA_FULL_RESCAN = "fullRescan"

        /**
         * Extra.
         * String.
         */
        @JvmStatic
        val EXTRA_CAUSE = "cause"
    }
}

fun newAPIIntent(): Intent {
    return Intent(ACTION_API_COMMAND).setComponent(PLAYER_SERVICE_COMPONENT_NAME)
}