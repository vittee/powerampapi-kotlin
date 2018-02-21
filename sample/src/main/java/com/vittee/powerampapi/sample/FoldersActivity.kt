package com.vittee.powerampapi.sample

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SimpleCursorAdapter
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.vittee.poweramp.player.PARAM_SHUFFLE
import com.vittee.poweramp.player.ShuffleMode
import com.vittee.poweramp.player.TableDefinitions
import com.vittee.poweramp.player.api.Poweramp


class FoldersActivity : ListActivity(), AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private val poweramp = Poweramp(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folders)

        val cursor = with(TableDefinitions.Folders) {
            contentResolver.query(
                    Poweramp.createContentUri { appendEncodedPath("folders") },
                    arrayOf(
                            "$_ID AS _id",
                            "$PARENT_NAME || '/' || $NAME AS name",
                            "$PARENT_NAME AS parent_name",
                            "$PARENT_ID AS parent_id"
                    ),
                    "$NUM_FILES > 0",
                    null,
                    "name COLLATE NOCASE"
            )
        }

        startManagingCursor(cursor)

        listAdapter = SimpleCursorAdapter(
                this, // Context.
                android.R.layout.two_line_list_item,
                cursor,
                arrayOf("name", "_id"),
                intArrayOf(android.R.id.text1, android.R.id.text2)
        )

        findViewById<ListView>(android.R.id.list).let {
            it.onItemClickListener = this
            it.onItemLongClickListener = this
        }
    }


    override fun onItemLongClick(adapterView: AdapterView<*>?, view: View?, p2: Int, id: Long): Boolean {
        Poweramp.createContentUri {
                appendEncodedPath("folders")
                appendEncodedPath(id.toString())
                appendQueryParameter(PARAM_SHUFFLE, ShuffleMode.SONGS.value.toString())
        }.let(poweramp::openToPlay)

        finish()

        return true
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, id: Long) {
        Intent(this, FilesActivity::class.java).putExtra("id", id).let(::startActivity)
    }
}
