package io.piano.android.cxense

import io.piano.android.cxense.db.EventRecord
import io.piano.android.cxense.model.EventDataRequest
import io.piano.android.cxense.model.EventStatus
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import retrofit2.Response
import timber.log.Timber

/**
 * Class which sends all events from local database to server using suspending calls
 */
internal class EventSender(
    private val cxApi: CxApi,
    private val eventRepository: EventRepository,
    private val configuration: CxenseConfiguration,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val userProvider: UserProvider,
    private val pageViewEventConverter: PageViewEventConverter,
    private val performanceEventConverter: PerformanceEventConverter,
    private val errorParser: ApiErrorParser,
) {

    private val eventStatusesFlow = MutableSharedFlow<List<EventStatus>>(
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_LATEST,
    )
    val eventStatuses: SharedFlow<List<EventStatus>> = eventStatusesFlow.asSharedFlow()

    suspend fun send() {
        runSuspendCatching {
            eventRepository.deleteOutdatedEvents(configuration.outdatePeriod.inWholeMilliseconds)
            if (deviceInfoProvider.getCurrentNetworkStatus() < configuration.minimumNetworkStatus) {
                return@runSuspendCatching
            }
            val pvDenied = with(configuration.consentSettings) {
                consentRequired && !pvAllowed
            }
            if (pvDenied) {
                return@runSuspendCatching
            }
            sendPageViewEvents(eventRepository.getNotSubmittedPvEvents())
            with(configuration.credentialsProvider) {
                eventRepository.getNotSubmittedDmpEvents().let { events ->
                    if (getDmpPushPersistentId().isNotEmpty()) {
                        sendDmpEventsViaPersisted(events)
                    } else if (getUsername().isNotEmpty() && getApiKey().isNotEmpty()) {
                        sendDmpEventsViaApi(events)
                    }
                }
            }
            sendConversionEvents(eventRepository.getNotSubmittedConversionEvents())
        }.onFailure {
            Timber.e(it, "Error at sending data")
        }
    }

    internal suspend fun sendDmpEventsViaApi(events: List<EventRecord>) {
        runSuspendCatching {
            events.map { it.data }
                .takeUnless { it.isEmpty() }
                ?.let { data ->
                    val exception = safeApiCall(errorParser) {
                        cxApi.pushEvents(EventDataRequest(data))
                    }.onSuccess {
                        events.forEach { r ->
                            r.isSent = true
                            eventRepository.putEventRecordInDatabase(r)
                        }
                    }.exceptionOrNull()
                    notify(events, exception)
                }
        }.onFailure {
            notify(events, it)
        }
    }

    internal suspend fun sendEventsOneByOne(
        events: List<EventRecord>,
        sendFunc: suspend (EventRecord) -> Response<Unit>,
    ) {
        val processed = events.map { record ->
            val exception = safeApiCall(errorParser) {
                sendFunc(record)
            }.onSuccess {
                record.isSent = true
                eventRepository.putEventRecordInDatabase(record)
            }.exceptionOrNull()
            record.toEventStatus(exception)
        }

        notify(processed)
    }

    internal suspend fun sendDmpEventsViaPersisted(events: List<EventRecord>) = sendEventsOneByOne(events) { record ->
        cxApi.trackDmpEvent(
            configuration.credentialsProvider.getDmpPushPersistentId(),
            requireNotNull(performanceEventConverter.extractQueryData(record)) {
                "Error at parsing event data"
            },
        )
    }

    internal suspend fun sendPageViewEvents(events: List<EventRecord>) = sendEventsOneByOne(events) { record ->
        cxApi.trackInsightEvent(pageViewEventConverter.extractQueryData(record, userProvider::userId))
    }

    internal suspend fun sendConversionEvents(events: List<EventRecord>) = sendEventsOneByOne(events) { record ->
        cxApi.pushConversionEvents(EventDataRequest(listOf(record.data)))
    }

    private fun notify(events: List<EventRecord>, throwable: Throwable?) {
        notify(
            events.map { it.toEventStatus(throwable) },
        )
    }

    private fun notify(eventStatuses: List<EventStatus>) {
        eventStatusesFlow.tryEmit(eventStatuses)
    }

    private fun EventRecord.toEventStatus(e: Throwable? = null) = EventStatus(customId, isSent, e)
}
