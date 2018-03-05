package com.vittee.powerampapi.sample

import android.app.ListActivity
import android.os.Bundle
import android.support.v4.widget.SimpleCursorAdapter
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.vittee.poweramp.player.EXTRA_ID
import com.vittee.poweramp.player.PARAM_SHUFFLE
import com.vittee.poweramp.player.ShuffleMode
import com.vittee.poweramp.player.TableDefinitions
import com.vittee.poweramp.player.api.Poweramp


class FilesActivity : ListActivity(), AdapterView.OnItemClickListener {
    private val TAG = "FoldersActivity"
    private var mFolderId = 0L

    private val poweramp = Poweramp(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files)

        mFolderId = intent.getLongExtra(EXTRA_ID, 0)

        val cursor = with (TableDefinitions.Files) {
            contentResolver.query(
                    Poweramp.createContentUri {
                        appendEncodedPath("folders")
                        appendEncodedPath(java.lang.Long.toString(mFolderId))
                        appendEncodedPath("files")
                    },
                    arrayOf(
                            "$_ID AS _id",
                            "$NAME AS name",
                            "$TITLE_TAG AS title_tag"
                    ),
                    null,
                    null,
                    "$NAME COLLATE NOCASE"
            )
        }

        startManagingCursor(cursor)

        val adapter = SimpleCursorAdapter(
                this, // Context.
                android.R.layout.two_line_list_item,
                cursor,
                arrayOf("name", "title_tag"),
                intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        listAdapter = adapter

        (findViewById<View>(android.R.id.list) as ListView).onItemClickListener = this
    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, p2: Int, id: Long) {
        Log.w(TAG, "file press=$id")

        Poweramp.createContentUri {
//            appendEncodedPath("folders")
//            appendEncodedPath(mFolderId.toString())
            appendEncodedPath("files")
            appendEncodedPath(id.toString())
            appendQueryParameter(PARAM_SHUFFLE, ShuffleMode.SONGS.value.toString())
        }.let(poweramp::openToPlay)

        finish()
    }
}
