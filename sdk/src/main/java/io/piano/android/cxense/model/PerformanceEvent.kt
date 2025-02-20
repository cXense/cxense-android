package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.piano.android.cxense.DependenciesProvider
import io.piano.android.cxense.UserProvider
import java.util.Collections
import java.util.Date
import java.util.Objects
import java.util.concurrent.TimeUnit

/**
 * Performance event description.
 * @property rnd alternative specification for eventId.
 * @property siteId The analytics site identifier to be associated with the events.
 * @property origin Differentiates various DMP applications used by the customer.
 * @property eventType Differentiates various event types, e.g., "click", "impression", "conversion", etc.
 * @property identities List of known user identities to identify the user.
 * @property eventId custom event id, that used for tracking locally.
 * @property prnd an alternative specification for page view event id.
 * @property time the exact datetime of an event
 * @property customParameters optional collection of customer-defined parameters to event.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JsonClass(generateAdapter = true)
public class PerformanceEvent internal constructor(
    eventId: String? = null,
    @Json(name = USER_IDS) public val identities: List<UserIdentity>,
    @Json(name = SITE_ID) public val siteId: String,
    @Json(name = ORIGIN) public val origin: String,
    @Json(name = TYPE) public val eventType: String,
    @Json(name = PRND) public val prnd: String?,
    @Json(name = TIME) public val time: Long,
    @Json(name = CUSTOM_PARAMETERS) public val customParameters: List<CustomParameter>,
    @Json(name = "consent") public val consentOptions: List<String>,
    @Json(name = RND) public val rnd: String,
) : Event(eventId) {
    override val mergeKey = Objects.hash(eventType, origin)

    /**
     * @constructor Initialize Builder with required parameters
     * @property siteId The analytics site identifier to be associated with the events.
     * @property origin Differentiates various DMP applications used by the customer. Must be prefixed by the customer prefix.
     * @property eventType Differentiates various event types, e.g., "click", "impression", "conversion", etc.
     * @property identities List of known user identities to identify the user. Note that different users must be fed as different events.
     * @property eventId custom event id, that used for tracking locally.
     * @property prnd an alternative specification for page view event id. In order to link DMP events to page views this value
     * must be identical to the rnd value of the page view event.
     * @property time the exact datetime of an event in milliseconds
     * @property customParameters optional collection of customer-defined parameters to event.
     */
    public data class Builder internal constructor(
        private val userProvider: UserProvider,
        var siteId: String,
        var origin: String,
        var eventType: String,
        var identities: MutableList<UserIdentity> = mutableListOf(),
        var eventId: String? = null,
        var prnd: String? = null,
        var time: Long = System.currentTimeMillis(),
        var customParameters: MutableList<CustomParameter> = mutableListOf(),
    ) {

        @JvmOverloads
        public constructor(
            siteId: String,
            origin: String,
            eventType: String,
            identities: MutableList<UserIdentity> = mutableListOf(),
            eventId: String? = null,
            prnd: String? = null,
            time: Long = System.currentTimeMillis(),
            customParameters: MutableList<CustomParameter> = mutableListOf(),
        ) : this(
            DependenciesProvider.getInstance().userProvider,
            siteId,
            origin,
            eventType,
            identities,
            eventId,
            prnd,
            time,
            customParameters,
        )

        /**
         * Adds known user identities to identify the user.
         * @param identities one or many [UserIdentity] objects
         */
        public fun addIdentities(vararg identities: UserIdentity): Builder = apply {
            this.identities.addAll(identities)
        }

        /**
         * Adds known user identities to identify the user.
         * @param identities [Iterable] with [UserIdentity] objects
         */
        public fun addIdentities(identities: Iterable<UserIdentity>): Builder = apply {
            this.identities.addAll(identities)
        }

        /**
         * Sets site identifier
         * @param siteId The analytics site identifier to be associated with the events.
         */
        public fun siteId(siteId: String): Builder = apply { this.siteId = siteId }

        /**
         * Sets event origin
         * @param origin Differentiates various DMP applications used by the customer. Must be prefixed by the customer prefix.
         */
        public fun origin(origin: String): Builder = apply { this.origin = origin }

        /**
         * Sets event type
         * @param type Differentiates various event types, e.g., "click", "impression", "conversion", etc.
         */
        public fun eventType(type: String): Builder = apply { this.eventType = type }

        /**
         * Sets custom event id
         * @param eventId custom event id, that used for tracking locally.
         */
        public fun eventId(eventId: String?): Builder = apply { this.eventId = eventId }

        /**
         * Sets page view event id
         * @param prnd an alternative specification for page view event id. In order to link DMP events to page views this value
         */
        public fun prnd(prnd: String?): Builder = apply { this.prnd = prnd }

        /**
         * Sets current datetime as datetime of an event
         */
        public fun currentTime(): Builder = apply { this.time = System.currentTimeMillis() }

        /**
         * Sets datetime of an event
         * @param date the exact datetime of an event
         */
        public fun time(date: Date): Builder = apply { this.time = date.time }

        /**
         * Adds custom parameters.
         * @param customParameters one or many [CustomParameter] objects
         */
        public fun addCustomParameters(vararg customParameters: CustomParameter): Builder =
            apply { this.customParameters.addAll(customParameters) }

        /**
         * Adds custom parameters.
         * @param customParameters [Iterable] with [CustomParameter] objects
         */
        public fun addCustomParameters(customParameters: Iterable<CustomParameter>): Builder =
            apply { this.customParameters.addAll(customParameters) }

        /**
         * Builds performance event
         * @throws [IllegalArgumentException] if constraints failed
         */
        public fun build(): PerformanceEvent {
            check(siteId.isNotEmpty()) {
                "Site id can't be empty"
            }
            check(eventType.isNotEmpty()) {
                "Event type can't be empty"
            }
            check(origin.matches(ORIGIN_REGEX.toRegex())) {
                "Origin must be prefixed by the customer prefix."
            }

            val userIds = identities.firstOrNull {
                it.type == UserIdentity.CX_USER_TYPE
            }?.let {
                identities
            } ?: (identities + UserIdentity(UserIdentity.CX_USER_TYPE, userProvider.userId))

            return with(DependenciesProvider.getInstance().cxenseConfiguration) {
                PerformanceEvent(
                    eventId,
                    Collections.unmodifiableList(userIds),
                    siteId,
                    origin,
                    eventType,
                    prnd,
                    TimeUnit.MILLISECONDS.toSeconds(time),
                    Collections.unmodifiableList(customParameters),
                    consentSettings.consents,
                    randomIdProvider(time),
                )
            }
        }
    }

    internal companion object {
        internal const val ORIGIN_REGEX = "\\w{3}-[\\w-]+"
        internal const val TIME = "time"
        internal const val USER_IDS = "userIds"
        internal const val PRND = "prnd"
        internal const val RND = "rnd"
        internal const val SITE_ID = "siteId"
        internal const val ORIGIN = "origin"
        internal const val TYPE = "type"
        internal const val CUSTOM_PARAMETERS = "customParameters"
    }
}
