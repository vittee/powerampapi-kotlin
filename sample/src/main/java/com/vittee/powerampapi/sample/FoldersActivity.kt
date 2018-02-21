package com.vittee.powerampapi.sample

import android.app.ListActivity
import android.os.Bundle
import android.support.v4.widget.SimpleCursorAdapter
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.vittee.poweramp.player.TableDefs
import com.vittee.poweramp.player.api.Poweramp
import android.content.Intent

class FoldersActivity : ListActivity(), AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folders)

        val cursor = contentResolver.query(
                Poweramp.createContentUri { appendEncodedPath("folders") },
                arrayOf(
                        "${TableDefs.Folders._ID} AS _id",
                        "${TableDefs.Folders.PARENT_NAME} || '/' || ${TableDefs.Folders.NAME} AS name",
                        "${TableDefs.Folders.PARENT_NAME} AS parent_name",
                        "${TableDefs.Folders.PARENT_ID} AS parent_id"
                ),
                "${TableDefs.Folders.NUM_FILES} > 0",
                null,
                "name COLLATE NOCASE"
        )

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

    override fun onItemLongClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long): Boolean {
        return false
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, id: Long) {
        Intent(this, FilesActivity::class.java).putExtra("id", id).let(::startActivity)
    }
}
