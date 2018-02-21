package com.vittee.powerampapi.sample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.os.Bundle
import android.support.v4.widget.SimpleCursorAdapter
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.vittee.poweramp.player.*
import com.vittee.poweramp.player.api.Poweramp
import java.util.regex.Pattern


class EqActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {
    private var mEquIntent: Intent? = null
    private var mEquBuilt: Boolean = false

    private var mSettingEqu: Boolean = false
    private var mSettingTone: Boolean = false
    private var mSettingPreset: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eq)

        (findViewById<View>(R.id.dynamic) as CheckBox).setOnCheckedChangeListener(this)
        findViewById<View>(R.id.commit_eq).setOnClickListener(this)

        // Create and bind spinner which binds to available PowerAMP presets.
        val presetSpinner = findViewById<View>(R.id.preset_spinner) as Spinner
        val c = contentResolver.query(Poweramp.createContentUri {  appendEncodedPath("eq_presets") },
                arrayOf("_id", "name", "preset"), null, null, "preset, name")
        startManagingCursor(c)
        // Add first empty item to the merged cursor via matrix cursor with single row.
        val mc = MatrixCursor(arrayOf("_id", "name", "preset"))
        mc.addRow(arrayOf<Any?>(NO_ID, "", null))
        val mrgc = MergeCursor(arrayOf<Cursor>(mc, c))

        val adapter = SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                mrgc,
                arrayOf("name"),
                intArrayOf(android.R.id.text1))

        adapter.viewBinder = SimpleCursorAdapter.ViewBinder { view, cursor, _ ->
            (view as TextView).text = getPresetName(this@EqActivity, cursor.getString(1), cursor.getInt(2))
            true
        }

        presetSpinner.adapter = adapter
        presetSpinner.onItemSelectedListener = this

        (findViewById<View>(R.id.eq) as CheckBox).setOnCheckedChangeListener(this)
        (findViewById<View>(R.id.tone) as CheckBox).setOnCheckedChangeListener(this)
    }

    private fun getPresetName(context: Context, name: String?, preset: Int): String {
        val eqPresetsLabels = context.resources.getTextArray(R.array.eq_preset_labels)
        return when {
            !TextUtils.isEmpty(name) -> name ?: context.getString(R.string.unknown)
            eqPresetsLabels != null && preset >= 0 && preset < eqPresetsLabels.size -> eqPresetsLabels[preset].toString()
            else -> context.getString(R.string.unknown)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.commit_eq -> commitEq()
        }
    }

    private fun commitEq() {
        val presetString = StringBuilder()

        val equLayout = findViewById<View>(R.id.equ_layout) as TableLayout
        val count = equLayout.childCount
        for (i in count - 1 downTo 0) {
            val bar = (equLayout.getChildAt(i) as ViewGroup).getChildAt(1) as SeekBar
            val name = bar.tag as String
            val value = seekBarToValue(name, bar.progress)
            presetString.append(name).append("=").append(value).append(";")
        }

        startService(Intent(ACTION_API_COMMAND).putExtra(EXTRA_COMMAND, Commands.SetEQString.value).putExtra(EXTRA_VALUE, presetString.toString()).setPackage(POWERAMP_PACKAGE_NAME))
    }

    private fun seekBarToValue(name: String, progress: Int): Float {
        return when (name) {
            "preamp", "bass", "treble" -> progress.toFloat()
            else -> (progress - 100).toFloat()
        } / 100f
    }

    override fun onResume() {
        super.onResume()
        registerAndLoadStatus()
    }

    private fun registerAndLoadStatus() {
        mEquIntent = registerReceiver(mEquReceiver, IntentFilter(ACTION_EQU_CHANGED))
    }

    override fun onDestroy() {
        unregister()
        super.onDestroy()
    }

    private fun unregister() {
        try {
            unregisterReceiver(mEquReceiver)
        } catch (ex: Exception) {
        }
    }

    private val mEquReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mEquIntent = intent

            debugDumpEquIntent(intent)

            updateEqu()
        }

    }

    private fun updateEqu() {
        mEquIntent?.let {

            val eq = findViewById<View>(R.id.eq) as CheckBox
            val equEnabled = it.getBooleanExtra(EXTRA_EQU, false)
            if (eq.isChecked != equEnabled) {
                mSettingEqu = true
                eq.isChecked = equEnabled
            }

            val tone = findViewById<View>(R.id.tone) as CheckBox
            val toneEnabled = it.getBooleanExtra(EXTRA_TONE, false)
            if (tone.isChecked != toneEnabled) {
                mSettingTone = true
                tone.isChecked = toneEnabled
            }

            val presetString = it.getStringExtra(EXTRA_VALUE)
            if (presetString == null || presetString.length == 0) {
                return
            }

            if (!mEquBuilt) {
                buildEquUI(presetString)
                mEquBuilt = true
            } else {
                updateEquUI(presetString)
            }

            //String presetName = mEquIntent.getStringExtra(PowerampAPI.NAME);

            val id = it.getLongExtra(EXTRA_ID, NO_ID)
            Log.w("EQ", "updateEqu id=" + id)

            val presetSpinner = findViewById<View>(R.id.preset_spinner) as Spinner
            val count = presetSpinner.adapter.count
            for (i in 0 until count) {
                if (presetSpinner.adapter.getItemId(i) == id) {
                    if (presetSpinner.selectedItemPosition != i) {
                        mSettingPreset = true
                        presetSpinner.setSelection(i)
                    }
                    break
                }
            }
        }
    }

    private fun updateEquUI(string: String?) {
        Log.w("TAG", "updateEquUI!")
        val pairs = sSemicolonSplitRe.split(string)
        val equLayout = findViewById<View>(R.id.equ_layout) as TableLayout

        var i = 0
        val pairsLength = pairs.size
        while (i < pairsLength) {
            val nameValue = sEqualSplitRe.split(pairs[i], 2)
            if (nameValue.size == 2) {
                val name = nameValue[0]
                try {
                    val value = java.lang.Float.parseFloat(nameValue[1])

                    val bar = (equLayout.getChildAt(i) as ViewGroup).getChildAt(1) as SeekBar
                    //SeekBar bar = (SeekBar)equLayout.findViewWithTag(name);
                    setBandValue(name, value, bar)
                } catch (ex: NumberFormatException) {
                    ex.printStackTrace()
                    Log.e("EQ", "failed to parse eq value=" + nameValue[1])
                }

            }
            i++
        }
    }

    private val sSemicolonSplitRe = Pattern.compile(";")
    private val sEqualSplitRe = Pattern.compile("=")

    // This method parses the equalizer serialized "presetString" and creates appropriate seekbars.
    private fun buildEquUI(string: String?) {
        val pairs = sSemicolonSplitRe.split(string)
        val equLayout = findViewById<View>(R.id.equ_layout) as TableLayout

        var i = 0
        val pairsLength = pairs.size
        while (i < pairsLength) {
            val nameValue = sEqualSplitRe.split(pairs[i], 2)
            if (nameValue.size == 2) {
                val name = nameValue[0]

                try {
                    val value = java.lang.Float.parseFloat(nameValue[1])

                    val row = TableRow(this)

                    val label = TextView(this)
                    label.text = name
                    val lp = TableRow.LayoutParams()
                    lp.width = TableRow.LayoutParams.WRAP_CONTENT
                    lp.height = lp.width
                    row.addView(label, lp)

                    val bar = SeekBar(this)
                    bar.setOnSeekBarChangeListener(this)
                    bar.tag = name
                    setBandValue(name, value, bar)
                    row.addView(bar, lp)

                    equLayout.addView(row)

                } catch (ex: NumberFormatException) {
                    ex.printStackTrace()
                    Log.e("EQ", "failed to parse eq value=" + nameValue[1])
                }

            }
            i++
        }
    }

    private fun setBandValue(name: String?, value: Float, bar: SeekBar) {
        when (name) {
            "preamp" -> {
                bar.max = 200
                bar.progress = (value * 100f).toInt()
            }
            "bass", "treble" -> {
                bar.max = 100
                bar.progress = (value * 100f).toInt()
            }
            else -> {
                bar.max = 200
                bar.progress = (value * 100f + 100f).toInt()
            }
        }
    }

    private fun debugDumpEquIntent(intent: Intent) {
        val presetName = intent.getStringExtra(EXTRA_NAME)
        val presetString = intent.getStringExtra(EXTRA_VALUE)
        val id = mEquIntent?.getLongExtra(EXTRA_ID, NO_ID)
        Log.w("EQ", "debugDumpEquIntent presetName=$presetName presetString=$presetString id=$id")

    }

    override fun onCheckedChanged(view: CompoundButton, isChecked: Boolean) {
        Log.w("EQ", "onCheckedChanged=" + view)
        when (view.id) {
            R.id.dynamic -> findViewById<View>(R.id.commit_eq).isEnabled = !isChecked

            R.id.eq -> {
                if (!mSettingEqu) {
                    startService(Intent(ACTION_API_COMMAND).putExtra(EXTRA_COMMAND, Commands.SetEQEnabled.value).putExtra(EXTRA_EQU, isChecked).setPackage(POWERAMP_PACKAGE_NAME))
                }
                mSettingEqu = false
            }

            R.id.tone -> {
                if (!mSettingTone) {
                    startService(Intent(ACTION_API_COMMAND).putExtra(EXTRA_COMMAND, Commands.SetEQEnabled.value).putExtra(EXTRA_TONE, isChecked).setPackage(POWERAMP_PACKAGE_NAME))
                }
                mSettingTone = false
            }
        }
    }

    override fun onNothingSelected(adapter: AdapterView<*>?) {

    }

    override fun onItemSelected(adapter: AdapterView<*>, item: View?, pos: Int, id: Long) {
        when {
            !mSettingPreset -> startService(Intent(ACTION_API_COMMAND).putExtra(EXTRA_COMMAND, Commands.SetEQPreset.value).putExtra(EXTRA_ID, id).setPackage(POWERAMP_PACKAGE_NAME))
            else -> mSettingPreset = false
        }
    }

    override fun onProgressChanged(bar: SeekBar, progress: Int, fromUser: Boolean) {
        // Process Eq band change.
        if ((findViewById<View>(R.id.dynamic) as CheckBox).isChecked) {
            val name = bar.tag as String
            val value = seekBarToValue(name, bar.progress)
            startService(Intent(ACTION_API_COMMAND).putExtra(EXTRA_COMMAND, Commands.SetEQBand.value).putExtra(EXTRA_NAME, name).putExtra(EXTRA_VALUE, value).setPackage(POWERAMP_PACKAGE_NAME))
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }
}
