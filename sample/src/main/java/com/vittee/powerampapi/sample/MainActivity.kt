package com.vittee.powerampapi.sample

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.vittee.poweramp.player.*
import com.vittee.poweramp.player.TableDefinitions
import com.vittee.poweramp.player.api.Poweramp
import java.io.File


class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener, Poweramp.Listener {
    private lateinit var mDuration: TextView
    private lateinit var mElapsed: TextView

    private lateinit var mSongSeekBar: SeekBar

    private var mLastScannedFile: String? = null

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

        mSongSeekBar = findViewById(R.id.song_seekbar)
        mSongSeekBar.setOnSeekBarChangeListener(this)

        mDuration = findViewById(R.id.duration)
        mElapsed = findViewById(R.id.elapsed)

        (findViewById<TextView>(R.id.play_file_path)).text = Environment.getExternalStorageDirectory()?.let(::findFirstMP3)
        findViewById<Button>(R.id.play_file).setOnClickListener(this)

        findViewById<Button>(R.id.folders).setOnClickListener(this)

        findViewById<Button>(R.id.play_album).setOnClickListener(this)
        findViewById<Button>(R.id.play_all_songs).setOnClickListener(this)
        findViewById<Button>(R.id.play_second_artist_first_album).setOnClickListener(this)

        findViewById<Button>(R.id.pa_current_list).setOnClickListener(this)
        findViewById<Button>(R.id.pa_folders).setOnClickListener(this)
        findViewById<Button>(R.id.pa_all_songs).setOnClickListener(this)
    }

    private fun findFirstMP3(dir: File): CharSequence? {
        try {
            findFirstMP3InFolder(dir)
        } catch (ex: FileFoundException) {
            return ex.file.path
        }

        return null

    }

    private class FileFoundException(val file: File) : RuntimeException()

    private fun findFirstMP3InFolder(dir: File) {
        dir.listFiles { child ->
            if (child.isDirectory) {
                findFirstMP3InFolder(child)
            } else {
                val fileName = child.name
                if (fileName.regionMatches(fileName.length - "flac".length, "flac", 0, "flac".length, ignoreCase = true)) {
                    throw FileFoundException(child)
                }
            }
            false
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        try {
            unregisterReceiver(mScanReceiver)
        } catch (ex: Exception) {
        }

        poweramp.unregister()
    }


    override fun onResume() {
        super.onResume()

        val filter = with(Scanner) {
            IntentFilter().also {
                arrayListOf(
                        ACTION_DIRS_SCAN_STARTED,
                        ACTION_DIRS_SCAN_FINISHED,
                        ACTION_TAGS_SCAN_STARTED,
                        ACTION_TAGS_SCAN_PROGRESS,
                        ACTION_TAGS_SCAN_FINISHED,
                        ACTION_FAST_TAGS_SCAN_FINISHED
                ).forEach(it::addAction)
            }
        }

        if (registerReceiver(mScanReceiver, filter) == null) { // No any scan progress, hide it (progress might be shown previously, before pause/resume)
            findViewById<View>(R.id.scan_progress).visibility = View.INVISIBLE
        }

        poweramp.register()
    }

    override fun onPause() {
        try {
            unregisterReceiver(mScanReceiver)
        } catch (ex: Exception) {
        }

        poweramp.unregister()

        super.onPause()
    }

    private val poweramp = Poweramp(this).also { it.addListener(this) }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.play -> poweramp.togglePlay()

            R.id.pause -> poweramp.pause()

            R.id.prev -> poweramp.previous()

            R.id.next -> poweramp.next()

            R.id.prev_in_cat -> poweramp.previousInCategory()

            R.id.next_in_cat -> poweramp.nextInCategory()

            R.id.repeat -> poweramp.cycleRepeatMode()

            R.id.repeat_all -> poweramp.repeat(RepeatMode.ON)

            R.id.repeat_off -> poweramp.repeat(RepeatMode.NONE)

            R.id.shuffle -> poweramp.cycleShuffleMode()

            R.id.shuffle_all -> poweramp.shuffle(ShuffleMode.ALL)

            R.id.shuffle_off -> poweramp.shuffle(ShuffleMode.NONE)


            R.id.play_file -> {
                poweramp.openToPlay(Uri.parse("file://" + (findViewById<TextView>(R.id.play_file_path)).text))
            }

            R.id.folders -> {
                Intent(this, FoldersActivity::class.java).let(::startActivity)
            }

            R.id.play_album -> playAlbum()

            R.id.play_first_folder -> playFirstFolder()

            R.id.play_all_songs -> playAllSongs()

            R.id.play_second_artist_first_album -> playSecondArtistFirstAlbum()

            R.id.pa_current_list -> Intent(ACTION_SHOW_CURRENT).let(::startActivity)

            R.id.eq -> {
                startActivity(Intent(this, EqActivity::class.java))
            }

            R.id.pa_folders -> {
                Intent(ACTION_SHOW_LIST).apply {
                    data = poweramp.createContentUri {
                        appendEncodedPath("folders")
                    }
                }.let(::startActivity)
            }

            R.id.pa_all_songs -> {
                Intent(ACTION_SHOW_LIST).apply {
                    data = poweramp.createContentUri {
                        appendEncodedPath("files")
                    }
                }.let(::startActivity)
            }

            R.id.pa_fast_scan -> {
                Intent(Scanner.ACTION_SCAN_DIRS)
                        .putExtra(Scanner.EXTRA_FAST_SCAN, true)
                        .let(::startService)
            }

            R.id.pa_scan -> {
                Intent(Scanner.ACTION_SCAN_DIRS).let(::startService)
            }

            R.id.pa_full_scan -> {
                Intent(Scanner.ACTION_SCAN_DIRS)
                        .putExtra(Scanner.EXTRA_FULL_RESCAN, true)
                        .putExtra(Scanner.EXTRA_ERASE_TAGS, true)
                        .let(::startService)
            }

            R.id.pa_song_scan -> {
                val allFilesUri = poweramp.createContentUri { appendEncodedPath("files") }

                // Find just any first available track
                val cursor = with(TableDefinitions.Files) {
                    contentResolver.query(allFilesUri,
                            arrayOf(_ID, FULL_PATH),
                            null, null,
                            "$NAME COLLATE NOCASE LIMIT 1"
                    )
                }

                cursor.use {
                    if (!cursor.moveToNext()) {
                        Toast.makeText(this, "No tracks in DB", Toast.LENGTH_SHORT).show()
                        return@use
                    }

                    mLastScannedFile = cursor.getString(1)
                    Log.e("Main", "Got file to scan=$mLastScannedFile")

                    with(TableDefinitions.Files) {
                        val id = cursor.getLong(0)

                        val updated = contentResolver.update(allFilesUri, ContentValues().apply { put(TAG_STATUS, TAG_NOT_SCANNED) }, "$_ID=$id", null) > 0
                        if (!updated) {
                            Toast.makeText(this@MainActivity, "File not updated", Toast.LENGTH_SHORT).show()
                            return@with
                        }

                        Intent(Scanner.ACTION_SCAN_TAGS)
                                .putExtra(Scanner.EXTRA_FAST_SCAN, true)
                                .let(this@MainActivity::startService)
                    }
                }
            }
        }
    }

    private fun playSecondArtistFirstAlbum() {
        // Get first artist.
        val cursor = with(TableDefinitions.Artists) {
            contentResolver.query(
                    Poweramp.createContentUri { appendEncodedPath("artists") },
                    arrayOf(_ID, ARTIST), null, null, "$ARTIST_SORT COLLATE NOCASE"
            )
        }

        cursor.use {
            cursor.moveToNext()
            if (!cursor.moveToNext()) {
                return
            }

            val artistId = cursor.getLong(0)
            val artist = cursor.getString(1)

            val cursor2 = with(TableDefinitions.Albums) {
                contentResolver.query(Poweramp.createContentUri { appendEncodedPath("artists_albums") },
                        arrayOf(_ID, ALBUM),
                        "artists._id=?", arrayOf(artistId.toString()), "$ALBUM_SORT COLLATE NOCASE")
            }

            cursor2.use c2@ {
                if (!cursor2.moveToNext()) {
                    return@c2
                }

                val albumId = cursor2.getLong(0)
                val album = cursor2.getString(1)

                Toast.makeText(this, "Playing artist: $artist album: $album", Toast.LENGTH_SHORT).show()

                poweramp.createContentUri {
                    appendEncodedPath("artists")
                    appendEncodedPath(artistId.toString())
                    appendEncodedPath("albums")
                    appendEncodedPath(albumId.toString())
                    appendEncodedPath("files")
                }.let(poweramp::openToPlay)
            }
        }
    }

    private fun playAllSongs() {
        poweramp.openToPlay(Poweramp.createContentUri { appendEncodedPath("files") })

    }

    private fun playFirstFolder() {
        // Get first folder id with some tracks.
        val cursor = with(TableDefinitions.Folders) {
            contentResolver.query(
                    Poweramp.createContentUri { appendEncodedPath("folders") },
                    arrayOf(_ID, PATH),
                    "$NUM_FILES > 0", null,
                    PATH
            )
        }

        cursor.use {
            if (!cursor.moveToNext()) {
                return
            }

            val id = cursor.getLong(0)
            val path = cursor.getString(1)
            Toast.makeText(this, "Playing folder: " + path, Toast.LENGTH_SHORT).show()

            Poweramp.createContentUri {
                appendEncodedPath("folders")
                appendEncodedPath(id.toString())
                appendEncodedPath("files")
            }.let(poweramp::openToPlay)
        }
    }

    private fun playAlbum() {
        // Get first album id.
        val cursor = with(TableDefinitions.Albums) {
            contentResolver.query(
                    Poweramp.createContentUri { appendEncodedPath("albums") },
                    arrayOf(_ID, ALBUM), null, null, ALBUM
            )
        }

        cursor.use {
            if (!cursor.moveToNext()) {
                return
            }

            Toast.makeText(this, "Playing album: ${cursor.getString(1)}", Toast.LENGTH_SHORT).show()

            Poweramp.createContentUri {
                appendEncodedPath("albums")
                appendEncodedPath(cursor.getLong(0).toString())
                appendEncodedPath("files")
            }.let(poweramp::openToPlay)
        }

    }

    override fun onLongClick(v: View?): Boolean {
        when (v?.id) {
            R.id.play -> poweramp.stop()
            R.id.next -> poweramp.beginFastForward()
            R.id.prev -> poweramp.beginRewind()
            else -> return false
        }

        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            when (v.id) {
                R.id.next -> poweramp.endFastForward()
                R.id.prev -> poweramp.endRewind()
            }
        }

        return false
    }

    private val mScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.w("Main", "Received intent=" + intent)

            val action = intent.action
            val progress = findViewById<ProgressBar>(R.id.scan_progress)

            when (action) {
                Scanner.ACTION_DIRS_SCAN_STARTED -> {
                    progress.isIndeterminate = true
                    progress.visibility = View.VISIBLE
                }

                Scanner.ACTION_DIRS_SCAN_FINISHED -> {
                    // Don't hide progress here as ACTION_TAGS_SCAN_STARTED will arrive right away.
                }

                Scanner.ACTION_TAGS_SCAN_STARTED -> {
                    progress.visibility = View.VISIBLE
                }

                Scanner.ACTION_TAGS_SCAN_PROGRESS -> {
                    progress.isIndeterminate = false
                    progress.max = 100
                    progress.progress = intent.getIntExtra(Scanner.EXTRA_PROGRESS, 0)
                }

                Scanner.ACTION_TAGS_SCAN_FINISHED -> {
                    progress.visibility = View.INVISIBLE
                    Toast.makeText(this@MainActivity, "Tags scanned: " + intent.getBooleanExtra(Scanner.EXTRA_TRACK_CONTENT_CHANGED, false), Toast.LENGTH_SHORT).show()
                }

                Scanner.ACTION_FAST_TAGS_SCAN_FINISHED -> {
                    Toast.makeText(this@MainActivity, "File rescanned: $mLastScannedFile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onProgressChanged(bar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (bar?.id) {
            R.id.song_seekbar -> if (fromUser) {
                sendSeek(false)
            }
        }
    }

    private var mLastSeekSentTime: Long = 0
    private val SEEK_THROTTLE = 500

    private fun sendSeek(ignoreThrottling: Boolean) {
        val position = mSongSeekBar.progress

        // Apply some throttling to avoid too many intents to be generated.
        if (!ignoreThrottling && mLastSeekSentTime != 0L && System.currentTimeMillis() - mLastSeekSentTime <= SEEK_THROTTLE) {
            Log.w("Main", "throttled")
            return
        }

        mLastSeekSentTime = System.currentTimeMillis()
        poweramp.seek(position)
        Log.w("Main", "sent")
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        sendSeek(false) // Force seek when user ends seeking.
    }

    private fun formatTime(secs: Int): String {
        var result = ""
        val seconds = secs % 60

        if (secs < 3600) { // min:sec
            result += (secs / 60).toString()
            result += ':'
        } else { // hour:min:sec
            val hours = secs / 3600
            val minutes = secs / 60 % 60

            result += hours.toString()
            result += ':'

            if (minutes < 10) {
                result += '0'
            }

            result += minutes.toString()
            result += ':'
        }

        if (seconds < 10) {
            result += '0'
        }
        result += seconds.toString()

        return result

    }

    override fun onTrackChanged(track: Poweramp.TrackInfo) {
        if (track.valid) {
            (findViewById<TextView>(R.id.cat)).text = track.cat.toString()
            (findViewById<TextView>(R.id.uri)).text = track.uri.toString()
            (findViewById<TextView>(R.id.id)).text = track.id.toString()
            (findViewById<TextView>(R.id.title)).text = track.title
            (findViewById<TextView>(R.id.album)).text = track.album
            (findViewById<TextView>(R.id.artist)).text = track.artist
            (findViewById<TextView>(R.id.path)).text = track.path
            //
            (findViewById<TextView>(R.id.info)).text = StringBuilder()
                    .append("Codec: ").append(track.codec).append(" ")
                    .append("Bitrate: ").append(track.bitrate).append(" ")
                    .append("Sample Rate: ").append(track.samplerate).append(" ")
                    .append("Channels: ").append(track.channels).append(" ")
                    .append("Duration: ").append(track.duration).append("sec ")

            return
        }

        // Else clean everything.
        (findViewById<TextView>(R.id.info)).text = ""
        (findViewById<TextView>(R.id.title)).text = ""
        (findViewById<TextView>(R.id.album)).text = ""
        (findViewById<TextView>(R.id.artist)).text = ""
        (findViewById<TextView>(R.id.path)).text = ""
    }

    override fun onTrackDurationChanged(duration: Int) {
        mDuration.text = formatTime(duration)
        mSongSeekBar.max = duration
    }

    override fun onTrackPositionChanged(position: Int) {
        mElapsed.text = formatTime(position)

        if (!mSongSeekBar.isPressed) {
            mSongSeekBar.progress = position
        }
    }

    override fun onPlaybackStatusChanged(paused: Boolean) {
        (findViewById<Button>(R.id.play)).text = when {
            paused -> ">"
            else -> "||"
        }
    }

    override fun onPlayingModeChanged(mode: Poweramp.PlayingMode) {
        (findViewById<Button>(R.id.shuffle)).text = when (mode.shuffleMode) {
            ShuffleMode.ALL -> "Shuffle All"
            ShuffleMode.CATS -> "Shuffle Categories"
            ShuffleMode.SONGS -> "Shuffle Songs"
            ShuffleMode.SONGS_AND_CATS -> "Shuffle Songs And Categories"
            else -> "Shuffle OFF"
        }

        (findViewById<Button>(R.id.repeat)).text = when (mode.repeatMode) {
            RepeatMode.ON -> "Repeat List"
            RepeatMode.ADVANCE -> "Advance List"
            RepeatMode.SONG -> "Repeat Song"
            else -> "Repeat OFF"
        }
    }

    override fun onAlbumArt(bitmap: Bitmap?) {
        findViewById<ImageView>(R.id.album_art).setImageBitmap(bitmap)
    }
}
