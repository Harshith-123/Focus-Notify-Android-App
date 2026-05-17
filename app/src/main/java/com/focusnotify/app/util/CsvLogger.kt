package com.focusnotify.app.util

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object CsvLogger {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val dayFormat  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun getLogDir(context: Context): File {
        val dir = File(context.getExternalFilesDir(null), "logs")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun getLogFile(context: Context): File {
        val participantId = PrefsHelper.getParticipantId(context)
        val today = dayFormat.format(Date())
        return File(getLogDir(context), "focusnotify_${participantId}_${today}.csv")
    }

    private fun ensureHeader(file: File) {
        if (!file.exists() || file.length() == 0L) {
            FileWriter(file, true).use { w ->
                w.appendLine("timestamp,app_package,app_name,title,text_snippet,score,decision,mode,fomo_score")
            }
        }
    }

    fun log(
        context: Context,
        appPackage: String,
        appName: String,
        title: String,
        text: String,
        score: Float,
        decision: String,
        mode: String,
        fomoScore: Int
    ) {
        try {
            val file = getLogFile(context)
            ensureHeader(file)
            fun clean(s: String) = s.replace(",", ";").replace("\n", " ").take(100)
            FileWriter(file, true).use { w ->
                w.appendLine(
                    "${dateFormat.format(Date())}," +
                    "${clean(appPackage)}," +
                    "${clean(appName)}," +
                    "${clean(title)}," +
                    "${clean(text)}," +
                    "${"%.2f".format(score)}," +
                    "$decision," +
                    "$mode," +
                    "$fomoScore"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLogFile(context: Context, forSharing: Boolean = true): File = getLogFile(context)

    fun getAllLogFiles(context: Context): List<File> {
        return getLogDir(context)
            .listFiles { f -> f.name.endsWith(".csv") }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    fun getLatestLogPath(context: Context): String = getLogFile(context).absolutePath
}
