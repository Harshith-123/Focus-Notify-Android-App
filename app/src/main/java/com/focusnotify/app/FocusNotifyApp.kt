package com.focusnotify.app

import android.app.Application
import com.focusnotify.app.data.db.AppDatabase

class FocusNotifyApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
