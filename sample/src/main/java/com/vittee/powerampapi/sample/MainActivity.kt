package com.vittee.powerampapi.sample

import android.annotation.SuppressLint
import android.content.*
import android.database.CharArrayBuffer
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.vittee.poweramp.player.*
import com.vittee.poweramp.player.TableDefs
import com.vittee.poweramp.widget.WidgetUtilsLite
import java.io.File
import android.content.Intent



class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener, RemoteTrackTime.TrackTimeListener {
    private lateinit var mRemoteTrackTime: RemoteTrackTime

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

        mRemoteTrackTime = RemoteTrackTime(this)
        mRemoteTrackTime.trackTimeListener = this

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
                if (fileName.regionMatches(fileName.length - "mp3".length, "mp3", 0, "mp3".length, ignoreCase = true)) {
                    throw FileFoundException(child)
                }
            }
            false
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        unregister()

        mRemoteTrackTime.apply {
            trackTimeListener = null
            unregister()
        }
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

    private fun sendCommand(command: Commands) {
        startService(createCommandIntent(command))
    }

    private fun createCommandIntent(command: Commands) = Intent(ACTION_API_COMMAND).putExtra(COMMAND, command.value).setPackage(PACKAGE_NAME)

    override fun onClick(v: View) {
        val command = when (v.id) {
            R.id.play -> Commands.TOGGLE_PLAY_PAUSE
            R.id.pause -> Commands.PAUSE
            R.id.prev -> Commands.PREVIOUS
            R.id.next -> Commands.NEXT
            R.id.prev_in_cat -> Commands.PREVIOUS_IN_CAT
            R.id.next_in_cat -> Commands.NEXT_IN_CAT
            R.id.repeat, R.id.repeat_all, R.id.repeat_off -> Commands.REPEAT
            R.id.shuffle, R.id.shuffle_all, R.id.shuffle_off -> Commands.SHUFFLE
            R.id.play_file -> Commands.OPEN_TO_PLAY

            R.id.play_album -> {
                playAlbum()
                return
            }

            R.id.play_first_folder -> {
                playFirstFolder()
                return
            }

            R.id.play_all_songs -> {
                playAllSongs()
                return
            }

            R.id.play_second_artist_first_album -> {
                playSecondArtistFirstAlbum()
                return
            }

            R.id.pa_current_list -> {
                startActivity(Intent(ACTION_SHOW_CURRENT))
                return
            }

            R.id.eq -> {
                startActivity(Intent(this, EqActivity::class.java))
                return
            }

            R.id.pa_folders -> {
                startActivity(Intent(ACTION_SHOW_LIST).setData(ROOT_URI.buildUpon().appendEncodedPath("folders").build()))
                return
            }

            R.id.pa_all_songs -> {
                startActivity(Intent(ACTION_SHOW_LIST).setData(ROOT_URI.buildUpon().appendEncodedPath("files").build()))
                return
            }

            R.id.pa_fast_scan -> {
                startService(Intent(Scanner.ACTION_SCAN_DIRS).putExtra(Scanner.EXTRA_FAST_SCAN, true))
                return
            }

            R.id.pa_scan -> {
                startService(Intent(Scanner.ACTION_SCAN_DIRS))
                return
            }

            R.id.pa_full_scan -> {
                startService(Intent(Scanner.ACTION_SCAN_DIRS).putExtra(Scanner.EXTRA_FULL_RESCAN, true).putExtra(Scanner.EXTRA_ERASE_TAGS, true))
                return
            }

            R.id.pa_song_scan -> {
                val allFilesUri = ROOT_URI.buildUpon().appendEncodedPath("files").build()

                // Find just any first available track
                val c = contentResolver.query(allFilesUri, arrayOf(TableDefs.Files._ID, TableDefs.Files.FULL_PATH), null, null, TableDefs.Files.NAME + " COLLATE NOCASE LIMIT 1")
                if (c != null) {
                    if (c.moveToNext()) {
                        mLastScannedFile = c.getString(1)
                        Log.e("Main", "Got file to scan=" + mLastScannedFile)
                        val id = c.getLong(0)

                        // Now set is as TAG_NOT_SCANNED
                        val values = ContentValues()
                        values.put(TableDefs.Files.TAG_STATUS, TableDefs.Files.TAG_NOT_SCANNED)
                        val updated = contentResolver.update(allFilesUri, values, TableDefs.Files._ID + "=" + id, null)

                        if (updated > 0) {

                            // And call scanner
                            startService(Intent(Scanner.ACTION_SCAN_TAGS).putExtra(Scanner.EXTRA_FAST_SCAN, true))

                        } else {
                            Toast.makeText(this, "File not updated", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(this, "No tracks in DB", Toast.LENGTH_SHORT).show()
                    }
                    c.close()
                }

                return
            }

            R.id.eq -> {
                startActivity(Intent(this, EqActivity::class.java))
                return
            }

            else -> null
        }

        command?.let {
            val intent = createCommandIntent(it)

            when (v.id) {
                R.id.repeat_all -> intent.putExtra(REPEAT, RepeatMode.REPEAT_ON.value)
                R.id.repeat_off -> intent.putExtra(REPEAT, RepeatMode.REPEAT_NONE.value)
            //
                R.id.shuffle_all -> intent.putExtra(SHUFFLE, ShuffleMode.SHUFFLE_ALL.value)
                R.id.shuffle_off -> intent.putExtra(SHUFFLE, ShuffleMode.SHUFFLE_NONE.value)
            //
                R.id.play_file -> intent.putExtra(Track.POSITION.value, 10).data = Uri.parse("file://" + (findViewById<TextView>(R.id.play_file_path)).text)
            //
                else -> {

                }
            }

            startService(intent)
        }
    }

    private fun playSecondArtistFirstAlbum() {
        // Get first artist.
        val c = contentResolver.query(ROOT_URI.buildUpon().appendEncodedPath("artists").build(),
                arrayOf("artists._id", "artist"), null, null, "artist_sort COLLATE NOCASE")
        if (c != null) {
            c.moveToNext() // First artist.
            if (c.moveToNext()) { // Second artist.
                val artistId = c.getLong(0)
                val artist = c.getString(1)
                val c2 = contentResolver.query(ROOT_URI.buildUpon().appendEncodedPath("artists_albums").build(),
                        arrayOf("albums._id", "album"),
                        "artists._id=?", arrayOf(java.lang.Long.toString(artistId)), "album_sort COLLATE NOCASE")
                if (c2 != null) {
                    if (c2.moveToNext()) {
                        val albumId = c2.getLong(0)
                        val album = c2.getString(1)

                        Toast.makeText(this, "Playing artist: $artist album: $album", Toast.LENGTH_SHORT).show()

                        startService(createCommandIntent(Commands.OPEN_TO_PLAY)
                                .setData(ROOT_URI.buildUpon()
                                        .appendEncodedPath("artists")
                                        .appendEncodedPath(java.lang.Long.toString(artistId))
                                        .appendEncodedPath("albums")
                                        .appendEncodedPath(java.lang.Long.toString(albumId))
                                        .appendEncodedPath("files")
                                        .build()
                                ))
                    }
                    c2.close()
                }

            }
            c.close()
        }


    }

    private fun playAllSongs() {
        startService(createCommandIntent(Commands.OPEN_TO_PLAY)
                .setData(ROOT_URI.buildUpon().appendEncodedPath("files").build()))

    }

    private fun playFirstFolder() {
        // Get first folder id with some tracks.
        val c = contentResolver.query(ROOT_URI.buildUpon().appendEncodedPath("folders").build(),
                arrayOf(TableDefs.Folders._ID, TableDefs.Folders.PATH),
                TableDefs.Folders.NUM_FILES + ">0", null, TableDefs.Folders.PATH)
        if (c != null) {
            if (c.moveToNext()) {
                val id = c.getLong(0)
                val path = c.getString(1)
                Toast.makeText(this, "Playing folder: " + path, Toast.LENGTH_SHORT).show()

                startService(createCommandIntent(Commands.OPEN_TO_PLAY)
                        .setData(ROOT_URI.buildUpon()
                                .appendEncodedPath("folders")
                                .appendEncodedPath(java.lang.Long.toString(id))
                                .appendEncodedPath("files")
                                .build()))
            }
            c.close()
        }
    }

    private fun playAlbum() {
        // Get first album id.
        val c = contentResolver.query(ROOT_URI.buildUpon().appendEncodedPath("albums").build(), arrayOf("albums._id", "album"), null, null, "album")
        if (c != null) {
            if (c.moveToNext()) {
                val albumId = c.getLong(0)
                val name = c.getString(1)
                Toast.makeText(this, "Playing album: " + name, Toast.LENGTH_SHORT).show()

                startService(createCommandIntent(Commands.OPEN_TO_PLAY)
                        .setData(ROOT_URI.buildUpon()
                                .appendEncodedPath("albums")
                                .appendEncodedPath(java.lang.Long.toString(albumId))
                                .appendEncodedPath("files")
                                .build()))
            }
            c.close()
        }

    }

    override fun onLongClick(v: View?): Boolean {
        val command: Commands? = when (v?.id) {
            R.id.play -> Commands.STOP
            R.id.next -> Commands.BEGIN_FAST_FORWARD
            R.id.prev -> Commands.BEGIN_REWIND
            else -> null
        }

        command?.let(::sendCommand)

        return command != null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            when (v.id) {
                R.id.next -> sendCommand(Commands.END_FAST_FORWARD)
                R.id.prev -> sendCommand(Commands.END_REWIND)
            }
        }

        return false
    }

    private fun registerAndLoadStatus() {
        // Note, it's not necessary to set mStatusIntent/mPlayingModeIntent this way here,
        // but this approach can be used with null receiver to get current sticky intent without broadcast receiver.
        mAAIntent = registerReceiver(mAAReceiver, IntentFilter(ACTION_AA_CHANGED))
        mTrackIntent = registerReceiver(mTrackReceiver, IntentFilter(ACTION_TRACK_CHANGED))
        mStatusIntent = registerReceiver(mStatusReceiver, IntentFilter(ACTION_STATUS_CHANGED))
        mPlayingModeIntent = registerReceiver(mPlayingModeReceiver, IntentFilter(ACTION_PLAYING_MODE_CHANGED))

        val filter = IntentFilter().apply {
            addAction(Scanner.ACTION_DIRS_SCAN_STARTED)
            addAction(Scanner.ACTION_DIRS_SCAN_FINISHED)
            addAction(Scanner.ACTION_TAGS_SCAN_STARTED)
            addAction(Scanner.ACTION_TAGS_SCAN_PROGRESS)
            addAction(Scanner.ACTION_TAGS_SCAN_FINISHED)
            addAction(Scanner.ACTION_FAST_TAGS_SCAN_FINISHED)
        }

        if (registerReceiver(mScanReceiver, filter) == null) { // No any scan progress, hide it (progress might be shown previously, before pause/resume)
            findViewById<View>(R.id.scan_progress).visibility = View.INVISIBLE
        }
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

    private fun unregister() {
        if (mTrackIntent != null) {
            try {
                unregisterReceiver(mTrackReceiver)
            } catch (ex: Exception) {
            }
            // Can throw exception if for some reason broadcast receiver wasn't registered.
        }

        if (mAAIntent != null) {
            try {
                unregisterReceiver(mAAReceiver)
            } catch (ex: Exception) {
            }

        }

        try {
            unregisterReceiver(mStatusReceiver)
        } catch (ex: Exception) {
        }

        try {
            unregisterReceiver(mPlayingModeReceiver)
        } catch (ex: Exception) {

        }

        try {
            unregisterReceiver(mScanReceiver)
        } catch (ex: Exception) {
        }

    }

    private var mTrackIntent: Intent? = null

    private val mTrackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mTrackIntent = intent
            processTrackIntent()
            Log.w("Main", "mTrackReceiver " + intent)
        }
    }

    private var mAAIntent: Intent? = null

    private val mAAReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mAAIntent = intent

            updateAlbumArt()

            Log.w("Main", "mAAReceiver " + intent)
        }
    }

    private fun updateAlbumArt() {
        mAAIntent?.let {
            Log.w("Main", "updateAlbumArt")
            val directAAPath = it.getStringExtra(ALBUM_ART_PATH)

            if (!TextUtils.isEmpty(directAAPath)) {
                Log.w("Main", "has AA, albumArtPath=" + directAAPath)
                findViewById<ImageView>(R.id.album_art).setImageURI(Uri.parse(directAAPath))
            } else if (it.hasExtra(ALBUM_ART_BITMAP)) {
                val albumArtBitmap = it.getParcelableExtra<Bitmap>(ALBUM_ART_BITMAP)
                if (albumArtBitmap != null) {
                    Log.w("Main", "has AA, bitmap")
                    findViewById<ImageView>(R.id.album_art).setImageBitmap(albumArtBitmap)
                }
            } else {
                Log.w("Main", "no AA")
                findViewById<ImageView>(R.id.album_art).setImageBitmap(null)
            }
        }

    }

    private var mStatusIntent: Intent? = null

    private val mStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mStatusIntent = intent

            mStatusIntent?.let(::debugDumpStatusIntent)

            updateStatusUI()
        }
    }

    private fun updateStatusUI() {
        mStatusIntent?.let {
            var paused = true

            val status = it.getIntExtra(STATUS, -1)

            // Each status update can contain track position update as well.
            val pos = it.getIntExtra(Track.POSITION.value, -1)
            if (pos != -1) {
                mRemoteTrackTime.updateTrackPosition(pos)
            }

            when (status) {
                Status.TRACK_PLAYING.value -> {
                    paused = it.getBooleanExtra(PAUSED, false)
                    startStopRemoteTrackTime(paused)
                }

                Status.TRACK_ENDED.value, Status.PLAYING_ENDED.value -> {
                    mRemoteTrackTime.stopSongProgress()
                }

                Status.TRACK_ENDED.value, Status.PLAYING_ENDED.value -> mRemoteTrackTime.stopSongProgress()
            }

            (findViewById<Button>(R.id.play)).text = if (paused) ">" else "||"
        }
    }

    private fun startStopRemoteTrackTime(paused: Boolean) {
        when {
            paused -> mRemoteTrackTime.stopSongProgress()
            else -> mRemoteTrackTime.startSongProgress()
        }
    }

    private fun debugDumpStatusIntent(intent: Intent) {
        val status = intent.getIntExtra(STATUS, -1)
        val paused = intent.getBooleanExtra(PAUSED, false)
        val failed = intent.getBooleanExtra(FAILED, false)
        Log.w("Main", "statusIntent status=$status paused=$paused failed=$failed")
    }

    private fun debugDumpPlayingModeIntent(intent: Intent) {
        val shuffle = intent.getIntExtra(SHUFFLE, -1)
        val repeat = intent.getIntExtra(REPEAT, -1)
        Log.w("Main", "debugDumpPlayingModeIntent shuffle=$shuffle repeat=$repeat")
    }

    private var mCurrentTrack: Bundle? = null

    private fun processTrackIntent() {
        mCurrentTrack = null

        mTrackIntent?.let { intent ->
            mCurrentTrack = intent.getBundleExtra(TRACK)
            mCurrentTrack?.let { track ->
                val duration = track.getInt(Track.DURATION.value)
                mRemoteTrackTime.updateTrackDuration(duration) // Let ReomoteTrackTime know about current song duration.
            }

            updateTrackUI()
        }
    }

    private var mPlayingModeIntent: Intent? = null

    private val mPlayingModeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mPlayingModeIntent = intent

            debugDumpPlayingModeIntent(intent)

            updatePlayingModeUI()
        }
    }

    private fun updatePlayingModeUI() {
        Log.w("Main", "updatePlayingModeUI")
        mPlayingModeIntent?.let {
            val shuffle = it.getIntExtra(SHUFFLE, -1)
            (findViewById<Button>(R.id.shuffle)).text = when (shuffle) {
                ShuffleMode.SHUFFLE_ALL.value -> "Shuffle All"
                ShuffleMode.SHUFFLE_CATS.value -> "Shuffle Categories"
                ShuffleMode.SHUFFLE_SONGS.value -> "Shuffle Songs"
                ShuffleMode.SHUFFLE_SONGS_AND_CATS.value -> "Shuffle Songs And Categories"
                else -> "Shuffle OFF"
            }

            val repeat = it.getIntExtra(REPEAT, -1)
            (findViewById<Button>(R.id.repeat)).text = when (repeat) {
                RepeatMode.REPEAT_ON.value -> "Repeat List"
                RepeatMode.REPEAT_ADVANCE.value -> "Advance List"
                RepeatMode.REPEAT_SONG.value -> "Repeat Song"
                else -> "Repeat OFF"
            }
        }

    }


    private fun updateTrackUI() {
        mCurrentTrack?.let {
            (findViewById<TextView>(R.id.cat)).text = it.getInt(Track.CAT.value).toString()
            (findViewById<TextView>(R.id.uri)).text = it.getParcelable<Parcelable>(Track.CAT_URI.value).toString()
            (findViewById<TextView>(R.id.id)).text = it.getLong(Track.ID.value).toString()
            (findViewById<TextView>(R.id.title)).text = it.getString(Track.TITLE.value)
            (findViewById<TextView>(R.id.album)).text = it.getString(Track.ALBUM.value)
            (findViewById<TextView>(R.id.artist)).text = it.getString(Track.ARTIST.value)
            (findViewById<TextView>(R.id.path)).text = it.getString(Track.PATH.value)

            val info = StringBuilder()
            info.append("Codec: ").append(it.getString(Track.CODEC.value)).append(" ")
            info.append("Bitrate: ").append(it.getInt(Track.BITRATE.value, -1)).append(" ")
            info.append("Sample Rate: ").append(it.getInt(Track.SAMPLE_RATE.value, -1)).append(" ")
            info.append("Channels: ").append(it.getInt(Track.CHANNELS.value, -1)).append(" ")
            info.append("Duration: ").append(it.getInt(Track.DURATION.value, -1)).append("sec ")

            (findViewById<TextView>(R.id.info)).text = info

            return
        }

        // Else clean everything.
        (findViewById<TextView>(R.id.info)).text = ""
        (findViewById<TextView>(R.id.title)).text = ""
        (findViewById<TextView>(R.id.album)).text = ""
        (findViewById<TextView>(R.id.artist)).text = ""
        (findViewById<TextView>(R.id.path)).text = ""
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

    private val mDurationBuffer = CharArrayBuffer(16)
    private val mElapsedBuffer = CharArrayBuffer(16)

    private fun sendSeek(ignoreThrottling: Boolean) {
        val position = mSongSeekBar.progress
        mRemoteTrackTime.updateTrackPosition(position)

        // Apply some throttling to avoid too many intents to be generated.
        if (ignoreThrottling || mLastSeekSentTime == 0L || System.currentTimeMillis() - mLastSeekSentTime > SEEK_THROTTLE) {
            mLastSeekSentTime = System.currentTimeMillis()
            startService(Intent(ACTION_API_COMMAND).putExtra(COMMAND, Commands.SEEK.value).putExtra(Track.POSITION.value, position).setPackage(PACKAGE_NAME))
            Log.w("Main", "sent")
        } else {
            Log.w("Main", "throttled")
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        sendSeek(false) // Force seek when user ends seeking.
    }

    override fun onTrackDurationChanged(duration: Int) {
        WidgetUtilsLite.formatTimeBuffer(mDurationBuffer, duration, true)
        mDuration.setText(mDurationBuffer.data, 0, mDurationBuffer.sizeCopied)

        mSongSeekBar.max = duration
    }

    override fun onTrackPositionChanged(position: Int) {
        WidgetUtilsLite.formatTimeBuffer(mElapsedBuffer, position, false)
        mElapsed.setText(mElapsedBuffer.data, 0, mElapsedBuffer.sizeCopied)

        if (!mSongSeekBar.isPressed) {
            mSongSeekBar.progress = position
        }
    }
}
