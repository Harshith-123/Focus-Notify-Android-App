package com.focusnotify.app.util

import android.content.Context
import android.content.SharedPreferences

object PrefsHelper {

    private const val PREFS_NAME = "focusnotify_prefs"
    private const val KEY_FOMO_SCORE = "fomo_score"
    private const val KEY_MODE = "study_mode"
    private const val KEY_SESSION_ACTIVE = "session_active"
    private const val KEY_PARTICIPANT_ID = "participant_id"

    const val MODE_BASELINE = "BASELINE"
    const val MODE_BATCHING = "BATCHING"
    const val MODE_ADAPTIVE = "ADAPTIVE"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getFomoScore(context: Context): Int =
        prefs(context).getInt(KEY_FOMO_SCORE, 25)

    fun setFomoScore(context: Context, score: Int) =
        prefs(context).edit().putInt(KEY_FOMO_SCORE, score).apply()

    fun getMode(context: Context): String =
        prefs(context).getString(KEY_MODE, MODE_ADAPTIVE) ?: MODE_ADAPTIVE

    fun setMode(context: Context, mode: String) =
        prefs(context).edit().putString(KEY_MODE, mode).apply()

    fun isSessionActive(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SESSION_ACTIVE, false)

    fun setSessionActive(context: Context, active: Boolean) =
        prefs(context).edit().putBoolean(KEY_SESSION_ACTIVE, active).apply()

    fun getParticipantId(context: Context): String =
        prefs(context).getString(KEY_PARTICIPANT_ID, "P00") ?: "P00"

    fun setParticipantId(context: Context, id: String) =
        prefs(context).edit().putString(KEY_PARTICIPANT_ID, id).apply()
}
