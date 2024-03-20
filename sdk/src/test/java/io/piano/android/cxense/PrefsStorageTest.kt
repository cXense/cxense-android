package io.piano.android.cxense

import android.content.Context
import android.content.SharedPreferences
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.Test
import kotlin.test.assertEquals

class PrefsStorageTest {
    private val prefsEditor: SharedPreferences.Editor = mock {
        on { putString(any(), any()) } doReturn mock
    }
    private val prefs: SharedPreferences = mock {
        on { edit() } doReturn prefsEditor
        on { getString(any(), anyOrNull()) } doReturn DUMMY_STRING
    }
    private val context: Context = mock {
        on { getSharedPreferences(any(), any()) } doReturn prefs
    }
    private val prefsStorage = PrefsStorage(context)

    @Test
    fun getDefaultUserId() {
        assertEquals(DUMMY_STRING, prefsStorage.defaultUserId)
        verify(prefs).getString(PrefsStorage.KEY_DEFAULT_USER_ID, null)
    }

    @Test
    fun setDefaultUserId() {
        prefsStorage.defaultUserId = DUMMY_STRING
        verify(prefsEditor).putString(PrefsStorage.KEY_DEFAULT_USER_ID, DUMMY_STRING)
    }

    companion object {
        const val DUMMY_STRING = "DUMMY"
    }
}
