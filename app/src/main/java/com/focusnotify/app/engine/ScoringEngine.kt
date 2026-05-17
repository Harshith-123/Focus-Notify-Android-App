package com.focusnotify.app.engine

object ScoringEngine {

    private val PRODUCTIVITY_APPS = setOf(
        "com.google.android.gm",            // Gmail
        "com.microsoft.office.outlook",     // Outlook
        "com.instructure.candroid",          // Canvas
        "com.slack",                         // Slack
        "com.microsoft.teams",               // Teams
        "com.google.android.apps.docs",     // Google Docs
        "com.google.android.calendar",      // Google Calendar
        "com.microsoft.office.word",
        "com.microsoft.office.excel",
        "com.google.android.apps.classroom" // Google Classroom
    )

    private val MESSAGING_APPS = setOf(
        "com.whatsapp",                     // WhatsApp
        "org.telegram.messenger",            // Telegram
        "com.facebook.orca",                // Messenger
        "com.google.android.talk",          // Google Chat
        "com.whatsapp.w4b",                 // WhatsApp Business
        "com.google.android.apps.messaging" // Google Messages
    )

    private val SOCIAL_APPS = setOf(
        "com.instagram.android",
        "com.snapchat.android",
        "com.zhiliaoapp.musically",         // TikTok
        "com.twitter.android",
        "com.reddit.frontpage",
        "com.discord",
        "com.linkedin.android",
        "com.facebook.katana",              // Facebook
        "com.pinterest"
    )

    private val PROMOTIONAL_APPS = setOf(
        "com.amazon.mShop.android.shopping",
        "in.swiggy.android",
        "com.application.zomato",
        "com.netflix.mediaclient",
        "com.spotify.music",
        "com.flipkart.android",
        "com.myntra.android",
        "com.bigbasket.android"
    )

    private val URGENCY_KEYWORDS = listOf(
        "urgent", "exam", "due", "deadline", "assignment",
        "emergency", "grade", "cancelled", "rescheduled",
        "quiz", "marks", "submit", "important", "meeting",
        "test tomorrow", "due tonight", "due today",
        "final", "midterm", "submission", "result",
        "attendance", "lecture cancelled", "class cancelled",
        "reschedule", "reminder", "overdue"
    )

    private const val THRESHOLD = 5.0f

    fun shouldDeliver(pkg: String, title: String, text: String, fomoScore: Int): Boolean {
        return calculateScore(pkg, title, text, fomoScore) >= THRESHOLD
    }

    fun calculateScore(pkg: String, title: String, text: String, fomoScore: Int): Float {
        val fullText = "$title $text".lowercase()

        // w1: App category (0.5 – 4.0)
        val appScore = when (pkg) {
            in PRODUCTIVITY_APPS -> 4.0f
            in MESSAGING_APPS    -> 2.0f
            in SOCIAL_APPS       -> 1.0f
            in PROMOTIONAL_APPS  -> 0.5f
            else                 -> 1.5f
        }

        // w2: Urgency keyword (0 or 3)
        val keywordScore = if (URGENCY_KEYWORDS.any { fullText.contains(it) }) 3.0f else 0.0f

        // w3: Time of day
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val timeScore = when (hour) {
            in 8..11  -> 1.5f
            in 12..17 -> 2.0f
            in 18..22 -> 1.5f
            else      -> 0.5f
        }

        // w4: FoMO weight — higher FoMO lowers effective threshold
        val fomoWeight = ((fomoScore - 10f) / 40f) * 1.5f

        return appScore + keywordScore + timeScore + fomoWeight
    }

    fun getThreshold() = THRESHOLD
}
