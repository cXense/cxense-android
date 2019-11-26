package com.cxense.cxensesdk

import com.cxense.cxensesdk.db.EventRecord
import com.cxense.cxensesdk.model.ConsentOption
import com.cxense.cxensesdk.model.EventDataRequest
import com.cxense.cxensesdk.model.EventStatus
import timber.log.Timber

class SendTask(
    private val cxApi: CxApi,
    private val eventRepository: EventRepository,
    private val configuration: CxenseConfiguration,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val userProvider: UserProvider,
    private val pageViewEventConverter: PageViewEventConverter,
    private val performanceEventConverter: PerformanceEventConverter,
    private val errorParser: ApiErrorParser,
    var sendCallback: DispatchEventsCallback?
) : Runnable {

    private fun EventRecord.toEventStatus(e: Exception? = null) =
        EventStatus(
            customId,
            isSent,
            e
        )

    private fun List<EventRecord>.notifyCallback(e: Exception? = null) = map { it.toEventStatus(e) }.notifyCallback()

    private fun List<EventStatus>.notifyCallback() = sendCallback?.invoke(this)


    private fun sendDmpEventsViaApi(events: List<EventRecord>) {
        try {
            events.map { it.data }
                .takeUnless { it.isEmpty() }
                ?.let { data ->
                    events.notifyCallback(
                        with(cxApi.pushEvents(EventDataRequest(data)).execute()) {
                            if (isSuccessful) {
                                events.forEach { r ->
                                    r.isSent = true
                                }
                            }
                            errorParser.parseError(this)
                        }
                    )
                }
        } catch (e: Exception) {
            events.notifyCallback(e)
        }
    }

    private fun sendEventsOneByOne(events: List<EventRecord>, sendFunc: (EventRecord) -> Exception?) {
        events.map { record ->
            try {
                record.toEventStatus(sendFunc(record))
            } catch (e: Exception) {
                record.toEventStatus(e)
            }
        }.notifyCallback()
    }

    private fun sendDmpEventsViaPersisted(events: List<EventRecord>) = sendEventsOneByOne(events) { record ->
        performanceEventConverter.extractQueryData(record)?.let { (segments, data) ->
            with(
                cxApi.trackDmpEvent(
                    configuration.credentialsProvider.getDmpPushPersistentId(),
                    segments ?: listOf(),
                    data
                ).execute()
            ) {
                if (isSuccessful) {
                    record.isSent = true
                    eventRepository.putEventRecordInDatabase(record)
                }
                errorParser.parseError(this)
            }
        }
    }

    private fun sendPageViewEvents(events: List<EventRecord>) = sendEventsOneByOne(events) { record ->
        pageViewEventConverter.extractQueryData(record, userProvider::userId)?.let { data ->
            with(cxApi.trackInsightEvent(data).execute()) {
                if (isSuccessful) {
                    record.isSent = true
                    eventRepository.putEventRecordInDatabase(record)
                }
                errorParser.parseError(this)
            }
        }
    }

    private fun sendConversionEvents(events: List<EventRecord>) = sendEventsOneByOne(events) { record ->
        with(cxApi.pushConversionEvents(EventDataRequest(listOf(record.data))).execute()) {
            if (isSuccessful) {
                record.isSent = true
                eventRepository.putEventRecordInDatabase(record)
            }
            errorParser.parseError(this)
        }
    }

    override fun run() {
        try {
            eventRepository.deleteOutdatedEvents(configuration.outdatePeriod)
            if (configuration.dispatchMode == CxenseConfiguration.DispatchMode.OFFLINE
                || deviceInfoProvider.getCurrentNetworkStatus() < configuration.minimumNetworkStatus
            )
                return
            val consentOptions = configuration.consentOptions
            if (ConsentOption.CONSENT_REQUIRED in consentOptions && ConsentOption.PV_ALLOWED !in consentOptions)
                return
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
        } catch (e: Exception) {
            Timber.e(e, "Error at sending data")
        }
    }
}