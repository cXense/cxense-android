package io.piano.android.cxense

import android.util.DisplayMetrics
import io.piano.android.cxense.db.EventRecord
import io.piano.android.cxense.model.ConsentSettings
import io.piano.android.cxense.model.PageViewEvent
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.squareup.moshi.JsonAdapter
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PageViewEventConverterTest {
    private val jsonAdapter: JsonAdapter<Map<String, String>> = mock {
        on { fromJson(any<String>()) } doReturn mapOf()
        on { toJson(any()) } doReturn "{}"
    }
    private val configuration: CxenseConfiguration = mock {
        on { autoMetaInfoTrackingEnabled } doReturn true
        on { consentSettings } doReturn ConsentSettings()
    }
    private val deviceInfoProvider: DeviceInfoProvider = mock {
        on { displayMetrics } doReturn DisplayMetrics().apply {
            density = 0f
            widthPixels = 1
            heightPixels = 2
        }
    }

    private val converter = PageViewEventConverter(jsonAdapter, configuration, deviceInfoProvider)
    private val event: PageViewEvent = mock()
    private val fixUserIdFunc: () -> String = spy()

    @Test
    fun canConvertConversionEvent() {
        assertTrue {
            converter.canConvert(event)
        }
    }

    @Test
    fun canConvertOtherEvent() {
        assertFalse {
            converter.canConvert(mock())
        }
    }

    @Test
    fun extractQueryData() {
        assertNotNull(converter.extractQueryData(EventRecord("type", "", "{}", mergeKey = 0), fixUserIdFunc))
        verify(jsonAdapter).fromJson(any<String>())
        verify(fixUserIdFunc).invoke()
    }

    @Test
    fun extractQueryDataWithCkp() {
        whenever(jsonAdapter.fromJson(any<String>())).thenReturn(
            mutableMapOf(
                PageViewEventConverter.CKP to "123",
            ),
        )
        assertNotNull(converter.extractQueryData(EventRecord("type", "", "{}", mergeKey = 0), fixUserIdFunc))
        verify(jsonAdapter).fromJson(any<String>())
        verify(fixUserIdFunc, never()).invoke()
    }

    @Test
    fun toEventRecord() {
        assertNotNull(converter.toEventRecord(event))
        verify(deviceInfoProvider).displayMetrics
        verify(configuration).autoMetaInfoTrackingEnabled
        verify(jsonAdapter).toJson(any())
    }

    @Test
    fun toEventRecordNotPvEvent() {
        assertNull(converter.toEventRecord(mock()))
    }

    @Test
    fun updateActiveTimeData() {
        assertNotNull(converter.updateActiveTimeData("{}", 0))
        verify(jsonAdapter).fromJson(any<String>())
        verify(jsonAdapter).toJson(any())
    }
}
