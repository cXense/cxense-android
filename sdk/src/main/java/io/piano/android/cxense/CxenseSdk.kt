package io.piano.android.cxense

import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import io.piano.android.cxense.model.CandidateSegment
import io.piano.android.cxense.model.ContentUser
import io.piano.android.cxense.model.Event
import io.piano.android.cxense.model.EventStatus
import io.piano.android.cxense.model.Impression
import io.piano.android.cxense.model.QueueStatus
import io.piano.android.cxense.model.Segment
import io.piano.android.cxense.model.SegmentLookupRequest
import io.piano.android.cxense.model.SegmentLookupRequest.SegmentContext
import io.piano.android.cxense.model.SegmentLookupResponse
import io.piano.android.cxense.model.User
import io.piano.android.cxense.model.UserDataRequest
import io.piano.android.cxense.model.UserExternalDataRequest
import io.piano.android.cxense.model.UserExternalTypedData
import io.piano.android.cxense.model.UserIdentity
import io.piano.android.cxense.model.UserIdentityMappingRequest
import io.piano.android.cxense.model.UserSegmentRequest
import io.piano.android.cxense.model.WidgetContext
import io.piano.android.cxense.model.WidgetItem
import io.piano.android.cxense.model.WidgetRequest
import io.piano.android.cxense.model.WidgetVisibilityReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds

/**
 * Singleton class used as a facade to the Cxense services
 * Read full documentation <a href="https://wiki.cxense.com/display/cust/Cxense+SDK+for+Android">here</a>
 * @property configuration Cxense SDK configuration, see [CxenseConfiguration]
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public class CxenseSdk internal constructor(
    private val coroutineScope: CoroutineScope,
    // Public API.
    @Suppress("unused", "MemberVisibilityCanBePrivate")
    public val configuration: CxenseConfiguration,
    private val advertisingIdProvider: AdvertisingIdProvider,
    private val userProvider: UserProvider,
    private val cxApi: CxApi,
    private val errorParser: ApiErrorParser,
    private val moshi: Moshi,
    private val eventRepository: EventRepository,
    private val eventSender: EventSender,
) {
    init {
        initSendTaskSchedule()
    }

    /**
     * Flow with event dispatch statuses
     */
    public val eventStatuses: SharedFlow<List<EventStatus>> = eventSender.eventStatuses

    /**
     * The user id used by this SDK. Must be at least 16 characters long.
     * Allowed characters are: A-Z, a-z, 0-9, "_", "-", "+" and ".".
     *
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public var userId: String
        get() = userProvider.userId
        set(value) {
            userProvider.userId = value
        }

    /**
     * Retrieves the default user id for SDK.
     *
     * @return advertising ID (if available)
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public val defaultUserId: String?
        get() = advertisingIdProvider.defaultUserId

    /**
     * Retrieves whether the user has limit ad tracking enabled or not.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public val limitAdTrackingEnabled: Boolean
        get() = advertisingIdProvider.limitAdTrackingEnabled

    // -------- Work with events

    /**
     * Push events to sending queue.
     *
     * @param events the events that should be pushed.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public fun pushEvents(vararg events: Event) {
        coroutineScope.launch {
            eventRepository.putEventsInDatabase(events)
            if (configuration.sendEventsAtPush) {
                flushEventQueue()
            }
        }
    }

    /**
     * Tracks active time for the given page view event.
     *
     * @param eventId the event to report active time for.
     * @param activeTime the active time in seconds.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public fun trackActiveTime(eventId: String, activeTime: Long = 0) {
        coroutineScope.launch {
            eventRepository.putEventTime(eventId, activeTime)
        }
    }

    /**
     * Forces sending events from queue to server.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public fun flushEventQueue() {
        coroutineScope.launch {
            eventSender.send()
        }
    }

    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public suspend fun getQueueStatus(): QueueStatus = QueueStatus(eventRepository.getEventStatuses())

    // -------- Content API
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public val defaultContentUser: ContentUser
        get() = userProvider.defaultUser

    /**
     * Tracks an url click for the given item
     *
     * @param item the item that contains the click-url
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public suspend fun trackClick(item: WidgetItem): Result<Unit> = item.clickUrl?.let { clickUrl ->
        trackClick(clickUrl)
    } ?: Result.failure(BaseException("Can't track this item. Click url is null"))

    /**
     * Tracks a click for the given click-url
     *
     * @param url the click-url
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public suspend fun trackClick(url: String): Result<Unit> = safeApiCall(errorParser) { cxApi.trackUrlClick(url) }

    /**
     * Load widget recommendations
     *
     * @param widgetId the widget id
     * @param widgetContext the WidgetContext
     * @param user custom user
     * @param tag Only display results from the branch with the given tag and ignore other conditions.
     * @param prnd Identifier for the page view where the result of this call will be displayed.
     * @param experienceId Experience identifier for C1X
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public suspend fun loadWidgetRecommendations(
        widgetId: String,
        widgetContext: WidgetContext? = null,
        user: ContentUser? = null,
        tag: String? = null,
        prnd: String? = null,
        experienceId: String? = null,
    ): Result<List<WidgetItem>> = safeApiCall(errorParser) {
        cxApi.getWidgetData(
            WidgetRequest(
                widgetId,
                configuration.consentSettings.consents,
                widgetContext,
                user ?: defaultContentUser,
                tag,
                prnd,
                experienceId,
            ),
        )
    }.map { it.items }

    /**
     * Report visibility of recommendations for a content widget.
     *
     * @param impressions The list of seen recommendations (impressions) you'd like to report visibility of.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public suspend fun reportWidgetVisibilities(
        vararg impressions: Impression,
    ): Result<Unit> = safeApiCall(errorParser) {
        cxApi.reportWidgetVisibility(WidgetVisibilityReport(impressions.toList()))
    }

    // -------- DMP API

    /**
     * Asynchronously retrieves a list of segments for specified site groups, user identifiers, and other provided data.
     *
     * @param siteGroupIds the list of site groups to retrieve segments for
     * @param identities a list of user identifiers for a single user to retrieve segments for
     * @param candidateSegmentIds A list of candidate segments to consider. The response segment matches will be a subset of these candidates.
     * @param shortIds If set the short ids will be included
     * @param segmentContext Segment context information
     */
    public suspend fun lookupSegments(
        siteGroupIds: List<String>,
        identities: List<UserIdentity>? = null,
        candidateSegmentIds: List<String>? = null,
        shortIds: Boolean = false,
        segmentContext: SegmentContext? = null,
    ): Result<SegmentLookupResponse> {
        val siteGroups = siteGroupIds.filterNot { it.isEmpty() }
        val segmentsDenied = with(configuration.consentSettings) {
            consentRequired && !segmentAllowed
        }
        return when {
            siteGroups.isEmpty() -> Result.failure(
                BaseException("You should provide at least one not empty site group id"),
            )

            segmentsDenied -> Result.failure(ConsentRequiredException())

            else -> safeApiCall(errorParser) {
                cxApi.lookupSegments(
                    SegmentLookupRequest(
                        siteGroups,
                        identities,
                        candidateSegmentIds,
                        shortIds,
                        segmentContext,
                    ),
                )
            }
        }
    }

    /**
     * Asynchronously retrieves a list of all segments where the specified user is a member
     *
     * @param identities a list of user identifiers for a single user to retrieve segments for
     * @param siteGroupIds the list of site groups to retrieve segments for
     * @param candidateSegments A list of candidate segments to consider. The response segment matches will be a subset of these candidates.
     * @param segmentFormat The segment format, one of [UserSegmentRequest.SegmentFormat.STANDARD] (the standard segment ID format that can be found in the platform)
     *                      or [UserSegmentRequest.SegmentFormat.SHORT_IDS] (a specially generated shortened version of the segment identifier compatible with some Ad servers).
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public suspend fun getUserSegments(
        identities: List<UserIdentity>,
        siteGroupIds: List<String>,
        candidateSegments: List<CandidateSegment>? = null,
        segmentFormat: UserSegmentRequest.SegmentFormat = UserSegmentRequest.SegmentFormat.STANDARD,
    ): Result<List<Segment>> {
        val siteGroups = siteGroupIds.filterNot { it.isEmpty() }
        val segmentsDenied = with(configuration.consentSettings) {
            consentRequired && !segmentAllowed
        }
        return when {
            identities.isEmpty() -> Result.failure(BaseException("You should provide at least one user identity"))

            siteGroups.isEmpty() -> Result.failure(
                BaseException("You should provide at least one not empty site group id"),
            )

            segmentsDenied -> Result.failure(ConsentRequiredException())

            else -> safeApiCall(errorParser) {
                cxApi.getUserTypedSegments(
                    UserSegmentRequest(
                        identities,
                        siteGroups,
                        candidateSegments,
                        UserSegmentRequest.ResponseFormat.CX_TYPED,
                        segmentFormat,
                    ),
                )
            }.map { it.segments }
        }
    }

    /**
     * Asynchronously retrieves a suitably authorized slice of a given user's interest profile
     *
     * @param identity user identifier with type and id
     * @param groups a collection of strings that specify profile item groups to keep in the returned profile. If not specified, all groups available for the user will be returned
     * @param recent flag whether to only return the most recent user profile information. This can be used to return quickly if response time is important
     * @param identityTypes a collection of external customer identifier types. If an external customer identifier exists for the user, it will be included in the response
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public suspend fun getUser(
        identity: UserIdentity,
        groups: List<String>? = null,
        recent: Boolean? = null,
        identityTypes: List<String>? = null,
    ): Result<User> = safeApiCall(errorParser) {
        cxApi.getUser(UserDataRequest(identity, groups, recent, identityTypes))
    }

    /**
     * Asynchronously retrieves the external data associated with a given user
     *
     * @param id identifier for the user. Use 'null' if you want match all users of provided type.
     * @param type the customer identifier type
     * @param filter a traffic filter of type user-external with required group and optional item/items specified
     * @param groups a list of group names. The result will show a subset of profiles that include these groups.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public suspend fun getUserExternalTypedData(
        type: String,
        id: String? = null,
        filter: String? = null,
        groups: List<String>? = null,
    ): Result<List<UserExternalTypedData>> = safeApiCall(errorParser) {
        cxApi.getUserExternalTypedData(
            UserExternalDataRequest(
                type,
                id,
                filter,
                groups,
                UserExternalDataRequest.ResponseFormat.TYPED,
            ),
        )
    }.map { it.items }

    /**
     * Asynchronously sets the external data associated with a given user
     *
     * @param userExternalData external data associated with a user
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public suspend fun setUserExternalTypedData(userExternalData: UserExternalTypedData): Result<Unit> =
        safeApiCall(errorParser) { cxApi.setUserExternalTypedData(userExternalData) }

    /**
     * Asynchronously deletes the external data associated with a given user
     *
     * @param identity user identifier with type and id
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public suspend fun deleteUserExternalData(identity: UserIdentity): Result<Unit> =
        safeApiCall(errorParser) { cxApi.deleteExternalUserData(identity) }

    /**
     * Asynchronously retrieves a registered external identity mapping for a Cxense identifier
     *
     * @param cxenseId the Cxense identifier of the user.
     * @param type the identity mapping type (customer identifier type) that contains the mapping.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public suspend fun getUserExternalLink(cxenseId: String, type: String): Result<UserIdentity> =
        safeApiCall(errorParser) { cxApi.getUserExternalLink(UserIdentityMappingRequest(cxenseId, type)) }

    /**
     * Asynchronously register a new identity-mapping for the given user
     *
     * @param cxenseId the Cxense identifier to map this user to
     * @param identity user identifier with type and id
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public suspend fun addUserExternalLink(cxenseId: String, identity: UserIdentity): Result<UserIdentity> =
        safeApiCall(errorParser) {
            cxApi.addUserExternalLink(
                UserIdentityMappingRequest(cxenseId, identity.type, identity.id),
            )
        }

    // -------- Persisted API

    /**
     * Executes persisted query. You can find some popular endpoints in {@link CxenseConstants}
     *
     * @param url               API endpoint
     * @param persistentQueryId query id
     * @param data              data for sending as request body
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public suspend inline fun <reified T : Any> executePersistedQuery(
        url: String,
        persistentQueryId: String,
        data: Any? = null,
    ): Result<T> = executePersistedQuery(url, persistentQueryId, T::class.java, data)

    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public suspend fun <T : Any> executePersistedQuery(
        url: String,
        persistentQueryId: String,
        clazz: Class<T>,
        data: Any? = null,
    ): Result<T> = safeApiCall(errorParser) {
        with(cxApi) {
            data?.let { postPersisted(url, persistentQueryId, it) } ?: getPersisted(url, persistentQueryId)
        }
    }.mapCatching { response ->
        val jsonAdapter = moshi.adapter(clazz)
        val reader = JsonReader.of(response.source())
        jsonAdapter.fromJson(reader) ?: throw BaseException(IllegalArgumentException("Result is null"))
    }

    // -------- Internal methods
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun initSendTaskSchedule() {
        coroutineScope.launch {
            configuration.dispatchPeriodFlow.flatMapLatest { interval ->
                flow {
                    delay(DISPATCH_INITIAL_DELAY)
                    while (true) {
                        emit(Unit)
                        delay(interval)
                    }
                }
            }.collect {
                eventSender.send()
            }
        }
    }

    public companion object {
        /**
         * Gets singleton SDK instance.
         */
        public fun getInstance(): CxenseSdk = DependenciesProvider.getInstance().cxenseSdk

        public fun contentUrl(siteId: String, contentId: String): String = "https://$siteId.content.id/$contentId"

        private val DISPATCH_INITIAL_DELAY = 10.seconds
    }
}
