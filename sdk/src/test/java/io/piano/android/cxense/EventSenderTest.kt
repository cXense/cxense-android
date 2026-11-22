package io.piano.android.cxense

import io.piano.android.cxense.db.EventRecord
import io.piano.android.cxense.model.ConsentSettings
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.eq
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import kotlin.test.Test
import kotlin.test.assertEquals

public class EventSenderTest {
    private val credentialsProvider: CredentialsProvider = mock {
        on { getDmpPushPersistentId() } doReturn "persisted"
        on { getUsername() } doReturn "user"
        on { getApiKey() } doReturn "key"
    }

    private val cxApi: CxApi = mock {
        on { pushEvents(any()) } doReturn Response.success(null)
        on { trackDmpEvent(any(), any()) } doReturn Response.success(mock())
        on { trackInsightEvent(any()) } doReturn Response.success(mock())
        on { pushConversionEvents(any()) } doReturn Response.success(null)
    }
    private val eventRepository: EventRepository = mock {
        on { deleteOutdatedEvents(any()) } doReturn 0
        on { getNotSubmittedPvEvents() } doReturn emptyList()
        on { getNotSubmittedDmpEvents() } doReturn emptyList()
        on { getNotSubmittedConversionEvents() } doReturn emptyList()
    }
    private val configuration: CxenseConfiguration = mock {
        on { consentSettings } doReturn ConsentSettings()
        on { credentialsProvider } doReturn credentialsProvider
        on { minimumNetworkStatus } doReturn CxenseConfiguration.NetworkStatus.NONE
        on { outdatePeriod } doReturn CxenseConfiguration.DEFAULT_OUTDATED_PERIOD
    }
    private val deviceInfoProvider: DeviceInfoProvider = mock {
        on { getCurrentNetworkStatus() } doReturn CxenseConfiguration.NetworkStatus.WIFI
    }
    private val userProvider: UserProvider = mock {
        on { userId } doReturn "user-id"
    }
    private val pageViewEventConverter: PageViewEventConverter = mock {
        on { extractQueryData(any(), any()) } doReturn mapOf()
    }
    private val performanceEventConverter: PerformanceEventConverter = mock {
        on { extractQueryData(any()) } doReturn mapOf()
    }
    private val errorParser: ApiErrorParser = mock()

    private val eventSender = EventSender(
        cxApi,
        eventRepository,
        configuration,
        deviceInfoProvider,
        userProvider,
        pageViewEventConverter,
        performanceEventConverter,
        errorParser,
    )

    @Test
    fun sendEventsOneByOne() {
        runTest {
            val event1: EventRecord = mock()
            val event2: EventRecord = mock()
            var calls = 0
            val sendFunc: suspend (EventRecord) -> Response<Unit> = {
                calls++
                if (it == event1) {
                    Response.success(Unit)
                } else {
                    Response.error(400, "".toResponseBody(null))
                }
            }
            eventSender.sendEventsOneByOne(listOf(event1, event2), sendFunc)
            assertEquals(2, calls)
        }
    }

    @Test
    fun send() {
        runTest {
            whenever(credentialsProvider.getDmpPushPersistentId()).thenReturn("")
            whenever(credentialsProvider.getUsername()).thenReturn("user")
            whenever(credentialsProvider.getApiKey()).thenReturn("key")

            val pvEvent = EventRecord("pv", null, "{}", mergeKey = 0)
            val dmpEvent = EventRecord("dmp", null, "{}", mergeKey = 0)
            val conversionEvent = EventRecord("conversion", null, "{}", mergeKey = 0)

            whenever(eventRepository.getNotSubmittedPvEvents()).thenReturn(listOf(pvEvent))
            whenever(eventRepository.getNotSubmittedDmpEvents()).thenReturn(listOf(dmpEvent))
            whenever(eventRepository.getNotSubmittedConversionEvents()).thenReturn(listOf(conversionEvent))

            eventSender.send()

            verify(eventRepository).deleteOutdatedEvents(any())
            verify(configuration).consentSettings
            verify(eventRepository).getNotSubmittedPvEvents()
            verify(cxApi).trackInsightEvent(any())
            verify(configuration).credentialsProvider
            verify(eventRepository).getNotSubmittedDmpEvents()
            verify(credentialsProvider).getDmpPushPersistentId()
            verify(credentialsProvider).getUsername()
            verify(credentialsProvider).getApiKey()
            verify(cxApi).pushEvents(any())
            verify(eventRepository).getNotSubmittedConversionEvents()
            verify(cxApi).pushConversionEvents(any())
        }
    }

    @Test
    fun sendDmpViaPersisted() {
        runTest {
            whenever(credentialsProvider.getDmpPushPersistentId()).thenReturn("persisted")

            val pvEvent = EventRecord("pv", null, "{}", mergeKey = 0)
            val dmpEvent = EventRecord("dmp", null, "{}", mergeKey = 0)
            val conversionEvent = EventRecord("conversion", null, "{}", mergeKey = 0)

            whenever(eventRepository.getNotSubmittedPvEvents()).thenReturn(listOf(pvEvent))
            whenever(eventRepository.getNotSubmittedDmpEvents()).thenReturn(listOf(dmpEvent))
            whenever(eventRepository.getNotSubmittedConversionEvents()).thenReturn(listOf(conversionEvent))

            eventSender.send()

            verify(eventRepository).deleteOutdatedEvents(any())
            verify(configuration).consentSettings
            verify(eventRepository).getNotSubmittedPvEvents()
            verify(cxApi).trackInsightEvent(any())
            verify(configuration, atLeastOnce()).credentialsProvider
            verify(eventRepository).getNotSubmittedDmpEvents()
            verify(credentialsProvider, atLeastOnce()).getDmpPushPersistentId()
            verify(credentialsProvider, never()).getUsername()
            verify(credentialsProvider, never()).getApiKey()
            verify(cxApi).trackDmpEvent(eq("persisted"), any())
            verify(eventRepository).getNotSubmittedConversionEvents()
            verify(cxApi).pushConversionEvents(any())
        }
    }

    @Test
    fun sendOffline() {
        runTest {
            whenever(deviceInfoProvider.getCurrentNetworkStatus()).thenReturn(CxenseConfiguration.NetworkStatus.NONE)
            whenever(configuration.minimumNetworkStatus).thenReturn(CxenseConfiguration.NetworkStatus.WIFI)

            eventSender.send()

            verify(eventRepository).deleteOutdatedEvents(any())
            verify(configuration, never()).consentSettings
            verify(eventRepository, never()).getNotSubmittedPvEvents()
            verify(cxApi, never()).trackInsightEvent(any())
        }
    }

    @Test
    fun sendWithoutConsent() {
        runTest {
            whenever(configuration.consentSettings).thenReturn(ConsentSettings().consentRequired(true))

            eventSender.send()

            verify(eventRepository).deleteOutdatedEvents(any())
            verify(configuration).consentSettings
            verify(eventRepository, never()).getNotSubmittedPvEvents()
            verify(cxApi, never()).trackInsightEvent(any())
        }
    }
}
