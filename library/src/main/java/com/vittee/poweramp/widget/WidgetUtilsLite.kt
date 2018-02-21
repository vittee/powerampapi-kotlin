package com.vittee.poweramp.widget

import android.database.CharArrayBuffer

@Deprecated("No longer used")
object WidgetUtilsLite {
    private val sFormatBuilder2 = StringBuilder()

    @Deprecated("No longer used")
    fun formatTimeBuffer(buffer: CharArrayBuffer, secs: Int, showPlaceholderForZero: Boolean) {
        if (secs == 0 && showPlaceholderForZero) {
            buffer.data = charArrayOf('-', ':', '-', '-')
            buffer.sizeCopied = buffer.data.size
            return
        }

        sFormatBuilder2.setLength(0)
        val seconds = secs % 60

        if (secs < 3600) { // min:sec
            val minutes = secs / 60
            sFormatBuilder2.append(minutes).append(':')
        } else { // hour:min:sec
            val hours = secs / 3600
            val minutes = secs / 60 % 60

            sFormatBuilder2.append(hours).append(':')
            if (minutes < 10) {
                sFormatBuilder2.append('0')
            }
            sFormatBuilder2.append(minutes).append(':')
        }

        if (seconds < 10) {
            sFormatBuilder2.append('0')
        }
        sFormatBuilder2.append(seconds)

        buffer.sizeCopied = if (buffer.data.size > sFormatBuilder2.length) sFormatBuilder2.length else buffer.data.size
        val len = buffer.sizeCopied
        sFormatBuilder2.getChars(0, len, buffer.data, 0)

    }
}
