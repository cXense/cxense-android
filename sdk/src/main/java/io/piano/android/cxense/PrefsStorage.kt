package io.piano.android.cxense

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY)
class PrefsStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var defaultUserId: String?
        get() = prefs.getString(KEY_DEFAULT_USER_ID, null)
        set(value) = prefs.edit().putString(KEY_DEFAULT_USER_ID, value).apply()

    companion object {
        internal const val PREFS_NAME = "io.piano.android.cxense"
        internal const val KEY_DEFAULT_USER_ID = "defaultUserId"
    }
}
