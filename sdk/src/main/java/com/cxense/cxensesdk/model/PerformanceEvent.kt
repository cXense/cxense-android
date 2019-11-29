package com.cxense.cxensesdk.model

import com.cxense.cxensesdk.DependenciesProvider
import com.google.gson.annotations.SerializedName
import java.util.Collections
import java.util.Date
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
 * @property segments optional collection of matching segments to be reported.
 * @property customParameters optional collection of customer-defined parameters to event.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
class PerformanceEvent(
    eventId: String?,
    @SerializedName(USER_IDS) val identities: List<UserIdentity>,
    @SerializedName(SITE_ID) val siteId: String,
    @SerializedName(ORIGIN) val origin: String,
    @SerializedName(TYPE) val eventType: String,
    @SerializedName(PRND) val prnd: String?,
    @SerializedName(TIME) val time: Long,
    @SerializedName(SEGMENT_IDS) val segments: List<String>?,
    @SerializedName(CUSTOM_PARAMETERS) val customParameters: List<CustomParameter>,
    @SerializedName("consent") val consentOptions: List<String>
) : Event(eventId) {

    @SerializedName(RND)
    val rnd: String = "${System.currentTimeMillis()}${(Math.random() * 10E8).toInt()}"

    /**
     * @constructor Initialize Builder with required parameters
     * @property siteId The analytics site identifier to be associated with the events.
     * @property origin Differentiates various DMP applications used by the customer. Must be prefixed by the customer prefix.
     * @property eventType Differentiates various event types, e.g., "click", "impression", "conversion", etc.
     * @property identities List of known user identities to identify the user. Note that different users must be fed as different events.
     * @property eventId custom event id, that used for tracking locally.
     * @property prnd an alternative specification for page view event id. In order to link DMP events to page views this value
     * must be identical to the rnd value of the page view event.
     * @property time the exact datetime of an event
     * @property segments optional collection of matching segments to be reported.
     * @property customParameters optional collection of customer-defined parameters to event.
     */
    data class Builder(
        var siteId: String,
        var origin: String,
        var eventType: String,
        var identities: MutableList<UserIdentity> = mutableListOf(),
        var eventId: String? = null,
        var prnd: String? = null,
        var time: Long = System.currentTimeMillis(),
        var segments: MutableList<String> = mutableListOf(),
        var customParameters: MutableList<CustomParameter> = mutableListOf()
    ) {

        /**
         * Adds known user identities to identify the user.
         * @param identities one or many [UserIdentity] objects
         */
        fun addIdentities(vararg identities: UserIdentity) = apply { this.identities.addAll(identities) }

        /**
         * Adds known user identities to identify the user.
         * @param identities [Iterable] with [UserIdentity] objects
         */
        fun addIdentities(identities: Iterable<UserIdentity>) = apply { this.identities.addAll(identities) }

        /**
         * Sets site identifier
         * @param siteId The analytics site identifier to be associated with the events.
         */
        fun siteId(siteId: String) = apply { this.siteId = siteId }

        /**
         * Sets event origin
         * @param origin Differentiates various DMP applications used by the customer. Must be prefixed by the customer prefix.
         */
        fun origin(origin: String) = apply { this.origin = origin }

        /**
         * Sets event type
         * @param type Differentiates various event types, e.g., "click", "impression", "conversion", etc.
         */
        fun eventType(type: String) = apply { this.eventType = type }

        /**
         * Sets custom event id
         * @param eventId custom event id, that used for tracking locally.
         */
        fun eventId(eventId: String?) = apply { this.eventId = eventId }

        /**
         * Sets page view event id
         * @param prnd an alternative specification for page view event id. In order to link DMP events to page views this value
         */
        fun prnd(prnd: String?) = apply { this.prnd = prnd }

        /**
         * Sets current datetime as datetime of an event
         */
        fun currentTime() = apply { this.time = System.currentTimeMillis() }

        /**
         * Sets datetime of an event
         * @param date the exact datetime of an event
         */
        fun time(date: Date) = apply { this.time = TimeUnit.MILLISECONDS.toSeconds(date.time) }

        /**
         * Adds matching segments to be reported.
         * @param segments one or many segment ids
         */
        fun addSegments(vararg segments: String) = apply { this.segments.addAll(segments) }

        /**
         * Adds matching segments to be reported.
         * @param segments [Iterable] with segment ids
         */
        fun addSegments(segments: Iterable<String>) = apply { this.segments.addAll(segments) }

        /**
         * Adds custom parameters.
         * @param customParameters one or many [CustomParameter] objects
         */
        fun addCustomParameters(vararg customParameters: CustomParameter) =
            apply { this.customParameters.addAll(customParameters) }

        /**
         * Adds custom parameters.
         * @param customParameters [Iterable] with [CustomParameter] objects
         */
        fun addCustomParameters(customParameters: Iterable<CustomParameter>) =
            apply { this.customParameters.addAll(customParameters) }

        /**
         * Builds performance event
         * @throws [IllegalArgumentException] if constraints failed
         */
        fun build(): PerformanceEvent {
            check(identities.isNotEmpty()) {
                "You should supply at least one user identity"
            }
            check(siteId.isNotEmpty()) {
                "Site id can't be empty"
            }
            check(eventType.isNotEmpty()) {
                "Event type can't be empty"
            }
            check(origin.matches(ORIGIN_REGEX.toRegex())) {
                "Origin must be prefixed by the customer prefix."
            }

            return PerformanceEvent(
                eventId,
                Collections.unmodifiableList(identities),
                siteId,
                origin,
                eventType,
                prnd,
                time,
                segments.takeUnless { it.isEmpty() }?.let { Collections.unmodifiableList(it) },
                Collections.unmodifiableList(customParameters),
                DependenciesProvider.getInstance().cxenseConfiguration.consentOptionsValues
            )
        }
    }

    companion object {
        const val ORIGIN_REGEX = "\\w{3}-[\\w-]+"
        const val TIME = "time"
        const val USER_IDS = "userIds"
        const val PRND = "prnd"
        const val RND = "rnd"
        const val SITE_ID = "siteId"
        const val ORIGIN = "origin"
        const val TYPE = "type"
        const val SEGMENT_IDS = "segmentIds"
        const val CUSTOM_PARAMETERS = "customParameters"
    }
}
