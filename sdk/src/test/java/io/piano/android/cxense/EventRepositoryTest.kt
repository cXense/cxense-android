package io.piano.android.cxense

import io.piano.android.cxense.db.DatabaseHelper
import io.piano.android.cxense.db.EventRecord
import io.piano.android.cxense.model.ConsentSettings
import io.piano.android.cxense.model.ConversionEvent
import io.piano.android.cxense.model.PageViewEvent
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EventRepositoryTest {
    private val configuration: CxenseConfiguration = mock {
        on { consentSettings } doReturn ConsentSettings()
    }
    private val databaseHelper: DatabaseHelper = mock {
        on { query(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()) } doReturn
            emptyList()
    }
    private val converter: PageViewEventConverter = mock {
        on { updateActiveTimeData(any(), any()) } doReturn "{}"
    }
    private val eventConverters = listOf(converter)

    private lateinit var repository: EventRepository

    @BeforeTest
    fun setUp() {
        repository = spy(EventRepository(configuration, databaseHelper, eventConverters))
    }

    @Test
    fun putEventRecordInDatabase() {
        runTest {
            repository.putEventRecordInDatabase(mock())
            verify(databaseHelper).save(any())
        }
    }

    @Test
    fun deleteOutdatedEvents() {
        runTest {
            repository.deleteOutdatedEvents(1)
            verify(databaseHelper).delete(anyOrNull(), anyOrNull())
        }
    }

    @Test
    fun getNotSubmittedPvEvents() {
        runTest {
            doAnswer {
                listOf(mock<EventRecord>())
            }.whenever(repository).getEvents(anyOrNull(), anyOrNull(), anyOrNull())
            assertEquals(1, repository.getNotSubmittedPvEvents().size)
            verify(repository).getEvents(anyOrNull(), eq(arrayOf(PageViewEvent.EVENT_TYPE)), anyOrNull())
        }
    }

    @Test
    fun getNotSubmittedDmpEvents() {
        runTest {
            doAnswer {
                listOf(mock<EventRecord>())
            }.whenever(repository).getEvents(anyOrNull(), anyOrNull(), anyOrNull())
            assertEquals(1, repository.getNotSubmittedDmpEvents().size)
            verify(repository).getEvents(
                anyOrNull(),
                eq(arrayOf(PageViewEvent.EVENT_TYPE, ConversionEvent.EVENT_TYPE)),
                anyOrNull(),
            )
        }
    }

    @Test
    fun getNotSubmittedConversionEvents() {
        runTest {
            doAnswer {
                listOf(mock<EventRecord>())
            }.whenever(repository).getEvents(anyOrNull(), anyOrNull(), anyOrNull())
            assertEquals(1, repository.getNotSubmittedConversionEvents().size)
            verify(repository).getEvents(anyOrNull(), eq(arrayOf(ConversionEvent.EVENT_TYPE)), anyOrNull())
        }
    }

    @Test
    fun getEvents() {
        runTest {
            repository.getEvents(null, null, null)
            verify(
                databaseHelper,
            ).query(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
        }
    }

    @Test
    fun getPvEventFromDatabase() {
        runTest {
            repository.getPvEventFromDatabase("")
            verify(
                databaseHelper,
            ).query(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
        }
    }

    @Test
    fun getEventStatuses() {
        runTest {
            repository.getEventStatuses()
            verify(
                databaseHelper,
            ).query(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
        }
    }

    @Test
    fun putEventTime() {
        runTest {
            val record = EventRecord("type", null, "", mergeKey = 0)
            doAnswer { record }.whenever(repository).getPvEventFromDatabase(any())
            repository.putEventTime("", 0)
            verify(repository).getPvEventFromDatabase(any())
            verify(converter).updateActiveTimeData(any(), any())
            verify(repository).putEventRecordInDatabase(any())
        }
    }

    @Test
    fun putEventTimeNoRecord() {
        runTest {
            doAnswer { null }.whenever(repository).getPvEventFromDatabase(any())
            repository.putEventTime("", 0)
            verify(repository).getPvEventFromDatabase(any())
            verify(repository, never()).putEventRecordInDatabase(any())
        }
    }
}
