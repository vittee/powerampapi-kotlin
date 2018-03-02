@file:Suppress("unused")

package com.vittee.poweramp.player

import android.content.ComponentName
import android.net.Uri


/**
 * Defines PowerampAPI version, which could be also 200 and 210 for older Poweramps.
 */
const val POWERAMP_VERSION: Int = 533

/**
 * No id flag.
 */
const val NO_ID = 0L

const val AUTHORITY = "com.maxmpz.audioplayer.data"

val POWERAMP_ROOT_URI: Uri = Uri.Builder().scheme("content").authority(AUTHORITY).build()

/**
 * Uri query parameter - filter.
 */
const val PARAM_FILTER = "flt"
/**
 * Uri query parameter - shuffle mode.
 */
const val PARAM_SHUFFLE = "shf"

/**
 * Poweramp Control action.
 * Should be sent with sendBroadcast().
 * Extras:
 * 	- cmd - int - command to execute.
 */
const val ACTION_API_COMMAND = "com.maxmpz.audioplayer.API_COMMAND"

/**
 * ACTION_API_COMMAND extra.
 * Int.
 */
const val EXTRA_COMMAND = "cmd"

/**
 * STATUS_CHANGED track extra.
 * Bundle.
 */
const val EXTRA_TRACK = "track"

/**
 * Extra.
 * String.
 */
const val EXTRA_ALBUM_ART_PATH = "aaPath"

/**
 * Extra.
 * Bitmap.
 */
const val EXTRA_ALBUM_ART_BITMAP = "aaBitmap"

/**
 * Extra.
 * boolean.
 */
const val EXTRA_DELAYED = "delayed"

/**
 * Extra.
 * long.
 */
const val EXTRA_TIMESTAMP = "ts"

/**
 * STATUS_CHANGED extra. See Status class for values.
 * Int.
 */
const val EXTRA_STATUS = "status"

enum class Status(val value: Int) {
    TRACK_PLAYING(1),
    TRACK_ENDED(2),
    PLAYING_ENDED(3)
}

enum class Commands(val value: Int) {
    /**
     * Extras:
     * - keepService - boolean - (optional) if true, Poweramp won't unload player service. Notification will be appropriately updated.
     */
    TogglePlayPause(1),

    /**
     * Extras:
     * - keepService - boolean - (optional) if true, Poweramp won't unload player service. Notification will be appropriately updated.
     */
    Pause(2),
    Resume(3),

    /**
     * NOTE: subject to 200ms throttling.
     */
    Next(4),

    /**
     * NOTE: subject to 200ms throttling.
     */
    Previous(5),

    /**
     * NOTE: subject to 200ms throttling.
     */
    NextInCat(6),

    /**
     * NOTE: subject to 200ms throttling.
     */
    PreviousInCat(7),

    /**
     * Extras:
     * - showToast - boolean - (optional) if false, no toast will be shown. Applied for cycle only.
     * - repeat - int - (optional) if exists, appropriate mode will be directly selected, otherwise modes will be cycled, see Repeat class.
     */
    Repeat(8),

    /**
     * Extras:
     * - showToast - boolean - (optional) if false, no toast will be shown. Applied for cycle only.
     * - shuffle - int - (optional) if exists, appropriate mode will be directly selected, otherwise modes will be cycled, see Shuffle class.
     */
    Shuffle(9),

    BeginFastForward(10),
    EndFastForward(11),
    BeginRewind(12),
    EndRewind(13),
    Stop(14),

    /**
     * Extras:
     * - pos - int - seek position in seconds.
     */
    Seek(15),
    PosSync(16),

    /**
     * Extras:
     * - paused - boolean - (optional) default false. OPEN_TO_PLAY command starts playing the file immediately, unless "paused" extra is true.
     *                       (see PowerampAPI.PAUSED)
     *
     * - pos - int - (optional) seek to this position in song before playing (see PowerampAPI.Track.POSITION)
     */
    OpenToPlay(20),

    /**
     * Extras:
     * - id - long - preset ID
     */
    SetEQPreset(50),

    /**
     * Extras:
     * - value - string - equalizer values, see ACTION_EQU_CHANGED description.
     */
    SetEQString(51),

    /**
     * Extras:
     * - name - string - equalizer band (bass/treble/preamp/31/62../8K/16K) name
     * - value - float - equalizer band value (bass/treble/, 31/62../8K/16K => -1.0...1.0, preamp => 0..2.0)
     */
    SetEQBand(52),

    /**
     * Extras:
     * - equ - boolean - if exists and true, equalizer is enabled
     * - tone - boolean - if exists and true, tone is enabled
     */
    SetEQEnabled(53),

    /**
     * Used by Notification controls to stop pending/paused service/playback and unload/remove notification.
     * Since 2.0.6
     */

    StopService(100)
}

/**
 * Extra.
 * Mixed.
 */
const val EXTRA_API_VERSION = "api"

/**
 * Extra.
 * Mixed.
 */
const val EXTRA_CONTENT = "content"

/**
 * Extra.
 * String.
 */
const val EXTRA_PACKAGE = "pak"

/**
 * Extra.
 * String.
 */
const val EXTRA_LABEL = "label"

/**
 * Extra.
 * Boolean.
 */
const val EXTRA_AUTO_HIDE = "autoHide"

/**
 * Extra.
 * Bitmap.
 */
const val EXTRA_ICON = "icon"

/**
 * Extra.
 * Boolean.
 */
const val EXTRA_MATCH_FILE = "matchFile"

/**
 * Extra.
 * Boolean
 */
const val EXTRA_SHOW_TOAST = "showToast"

/**
 * Extra.
 * Long.
 */
const val EXTRA_ID = "id"

/**
 * Extra.
 * String.
 */
const val EXTRA_NAME = "name"

/**
 * Extra.
 * Mixed.
 */
const val EXTRA_VALUE = "value"

/**
 * Extra.
 * Boolean.
 */
const val EXTRA_EQU = "equ"

/**
 * Extra.
 * Boolean.
 */
const val EXTRA_TONE = "tone"

/**
 * Extra.
 * Boolean.
 * Since 2.0.6
 */
const val EXTRA_KEEP_SERVICE = "keepService"

/**
 * Extra.
 * Boolean
 * Since build 533
 */
const val EXTRA_BEEP = "beep"

/**
 * Poweramp track changed.
 * Sticky intent.
 * Extras:
 * - track - bundle - Track bundle, see Track class.
 * - ts - long - timestamp of the event (System.currentTimeMillis()).
 *  Note, that by default Poweramp won't search/download album art when screen is OFF, but will do that on next screen ON event.
 */
const val ACTION_TRACK_CHANGED = "com.maxmpz.audioplayer.TRACK_CHANGED"

/**
 * Album art was changed. Album art can be the same for whole album/folder, thus usually it will be updated less frequently comparing to TRACK_CHANGE.
 * If both aaPath and aaBitmap extras are missing that means no album art exists for the current track(s).
 * Note that there is no direct Album Art to track relation, i.e. both track and album art can change independently from each other -
 * for example - when new album art asynchronously downloaded from internet or selected by user.
 * Sticky intent.
 * Extras:
 * - aaPath - String - (optional) if exists, direct path to the cached album art is available.
 * - aaBitmap - Bitmap - (optional)	if exists, some rescaled up to 500x500 px album art bitmap is available.
 *              There will be aaBitmap if aaPath is available, but image is bigger than 600x600 px.
 * - delayed - boolean - (optional) if true, this album art was downloaded or selected later by user.

 * - ts - long - timestamp of the event (System.currentTimeMillis()).
 */
const val ACTION_AA_CHANGED = "com.maxmpz.audioplayer.AA_CHANGED"

/**
 * Poweramp playing status changed (track started/paused/resumed/ended, playing ended).
 * Sticky intent.
 * Extras:
 * - status - string - one of the STATUS_* values
 * - pos - int - (optional) current in-track position in seconds.
 * - ts - long - timestamp of the event (System.currentTimeMillis()).
 * - additional extras - depending on STATUS_ value (see STATUS_* description below).
 */
const val ACTION_STATUS_CHANGED = "com.maxmpz.audioplayer.STATUS_CHANGED"

/**
 * NON sticky intent.
 * - pos - int - current in-track position in seconds.
 */
const val ACTION_TRACK_POS_SYNC = "com.maxmpz.audioplayer.TPOS_SYNC"

/**
 * Poweramp repeat or shuffle mode changed.
 * Sticky intent.
 * Extras:
 * - repeat - int - new repeat mode. See RepeatMode class.
 * - shuffle - int - new shuffle mode. See ShuffleMode class.
 * - ts - long - timestamp of the event (System.currentTimeMillis()).	 *
 */
const val ACTION_PLAYING_MODE_CHANGED = "com.maxmpz.audioplayer.PLAYING_MODE_CHANGED"

/**
 * Poweramp equalizer settings changed.
 * Sticky intent.
 * Extras:
 * - name - string - preset name. If no name extra exists, it's not a preset.
 * - id - long - preset id. If no id extra exists, it's not a preset.
 * - value - string - equalizer and tone values in format:
 *   	bass=pos_float|treble=pos_float|31=float|62=float|....|16K=float|preamp=0.0 ... 2.0
 *      where float = -1.0 ... 1.0, pos_float = 0.0 ... 1.0
 * - equ - boolean - true if equalizer bands are enabled
 * - tone - boolean - truel if tone bands are enabled
 * - ts - long - timestamp of the event (System.currentTimeMillis()).
 */

const val ACTION_EQU_CHANGED = "com.maxmpz.audioplayer.EQU_CHANGED"

/**
 * Special actions for com.maxmpz.audioplayer.PlayerUIActivity only.
 */
const val ACTION_SHOW_CURRENT = "com.maxmpz.audioplayer.ACTION_SHOW_CURRENT"
const val ACTION_SHOW_LIST = "com.maxmpz.audioplayer.ACTION_SHOW_LIST"

const val POWERAMP_PACKAGE_NAME = "com.maxmpz.audioplayer"
const val POWERAMP_PLAYER_SERVICE_NAME = "com.maxmpz.audioplayer.player.PlayerService"

val POWERAMP_PLAYER_SERVICE_COMPONENT_NAME = ComponentName(POWERAMP_PACKAGE_NAME, POWERAMP_PLAYER_SERVICE_NAME)

const val ACTIVITY_PLAYER_UI = "com.maxmpz.audioplayer.PlayerUIActivity"
const val ACTIVITY_EQ = "com.maxmpz.audioplayer.EqActivity"

/**
 * If com.maxmpz.audioplayer.ACTION_SHOW_LIST action is sent to this activity, it will react to some extras.
 * Extras:
 * Data:
 * - uri - uri of the list to display.
 */
const val ACTIVITY_PLAYLIST = "com.maxmpz.audioplayer.PlayListActivity"
const val ACTIVITY_SETTINGS = "com.maxmpz.audioplayer.preference.SettingsActivity"

/**
 * STATUS_CHANGED trackEnded extra.
 * Boolean. True if track failed to play.
 */
const val EXTRA_FAILED = "failed"

/**
 * STATUS_CHANGED trackStarted/trackPausedResumed extra.
 * Boolean. True if track is paused.
 */
const val EXTRA_PAUSED = "paused"

/**
 * PLAYING_MODE_CHANGED extra. See ShuffleMode class.
 * Integer.
 */
const val EXTRA_SHUFFLE = "shuffle"

/**
 * PLAYING_MODE_CHANGED extra. See RepeatMode class.
 * Integer.
 */
const val EXTRA_REPEAT = "repeat"

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
    UNKNOWN(-1),
    NONE(0),
    ALL(1),
    SONGS(2),
    CATS(3), // Songs in order.
    SONGS_AND_CATS(4) // Songs shuffled.
    ;

    companion object {
        private val map = values().associateBy(ShuffleMode::value)
        fun fromInt(v: Int) = if (map.containsKey(v)) map[v] else null
    }
}

enum class RepeatMode(val value: Int) {
    UNKNOWN(-1),
    NONE(0),
    ON(1),
    ADVANCE(2),
    SONG(3)
    ;

    companion object {
        private val map = values().associateBy(RepeatMode::value)
        fun fromInt(v: Int) = if (map.containsKey(v)) map[v] else null
    }
}

interface Scanner {
    companion object {
        const val ACTION_SCAN_DIRS = "com.maxmpz.audioplayer.ACTION_SCAN_DIRS"
        const val ACTION_SCAN_TAGS = "com.maxmpz.audioplayer.ACTION_SCAN_TAGS"
        const val ACTION_DIRS_SCAN_STARTED = "com.maxmpz.audioplayer.ACTION_DIRS_SCAN_STARTED"
        const val ACTION_DIRS_SCAN_FINISHED = "com.maxmpz.audioplayer.ACTION_DIRS_SCAN_FINISHED"
        const val ACTION_TAGS_SCAN_STARTED = "com.maxmpz.audioplayer.ACTION_TAGS_SCAN_STARTED"
        const val ACTION_TAGS_SCAN_PROGRESS = "com.maxmpz.audioplayer.ACTION_TAGS_SCAN_PROGRESS"
        const val ACTION_TAGS_SCAN_FINISHED = "com.maxmpz.audioplayer.ACTION_TAGS_SCAN_FINISHED"
        const val ACTION_FAST_TAGS_SCAN_FINISHED = "com.maxmpz.audioplayer.ACTION_FAST_TAGS_SCAN_FINISHED"

        /**
         * Extra.
         * Boolean.
         */
        const val EXTRA_FAST_SCAN = "fastScan"
        /**
         * Extra.
         * Int.
         */
        const val EXTRA_PROGRESS = "progress"
        /**
         * Extra.
         * Boolean.
         */
        const val EXTRA_TRACK_CONTENT_CHANGED = "trackContentChanged"

        /**
         * Extra.
         * Boolean.
         */
        const val EXTRA_ERASE_TAGS = "eraseTags"

        /**
         * Extra.
         * Boolean.
         */
        const val EXTRA_FULL_RESCAN = "fullRescan"

        /**
         * Extra.
         * String.
         */
        const val EXTRA_CAUSE = "cause"
    }
}

enum class Cats(val value: Int) {
    ROOT(0),
    FOLDERS(10),
    GENRES_ID_ALBUMS(210),
    ALBUMS(200),
    GENRES(320),
    ARTISTS(500),
    ARTISTS_ID_ALBUMS(220),
    ARTISTS__ALBUMS(250),
    COMPOSERS(600),
    COMPOSERS_ID_ALBUMS(230),
    PLAYLISTS(100),
    QUEUE(800),
    MOST_PLAYED(43),
    TOP_RATED(48),
    RECENTLY_ADDED(53),
    RECENTLY_PLAYED(58),
}

interface Settings {
    companion object {
        const val ACTION_EXPORT_SETTINGS = "com.maxmpz.audioplayer.ACTION_EXPORT_SETTINGS"

        const val ACTION_IMPORT_SETTINGS = "com.maxmpz.audioplayer.ACTION_IMPORT_SETTINGS"

        const val EXTRA_UI = "ui"
    }
}

