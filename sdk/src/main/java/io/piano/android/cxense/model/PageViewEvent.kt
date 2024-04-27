package io.piano.android.cxense.model

import io.piano.android.cxense.DependenciesProvider
import io.piano.android.cxense.UserProvider
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.util.Collections
import java.util.Objects

/**
 * Tracking page view event description.
 * Page view event has support for two modes: standard page view event and URL-less mode for content view event
 * @property eventType Predefined event type
 * @property time Event timestamp
 * @property rnd Event rnd, uniquely identifies a page-view request.
 * @property siteId The Cxense site identifier.
 * @property location The URL of the page.
 * @property contentId The content id for URL-less mode.
 * @property referrer The URL of the referring page.
 * @property eventId Custom event id, that used for tracking locally.
 * @property accountId The Cxense account identifier.
 * @property pageName The page name.
 * @property newUser Hint to indicate if this looks like a new user.
 * @property customParameters Custom parameters.
 * @property customUserParameters Custom user profile parameters.
 * @property externalUserIds External user ids.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public class PageViewEvent private constructor(
    eventId: String?,
    public val userId: String,
    public val siteId: String,
    public val location: String?,
    public val contentId: String?,
    public val referrer: String?,
    public val accountId: Int?,
    public val pageName: String?,
    public val newUser: Boolean?,
    public val customParameters: MutableList<CustomParameter>,
    public val customUserParameters: MutableList<CustomParameter>,
    public val externalUserIds: MutableList<ExternalUserId>,
    public val time: Long,
    public val rnd: String,
) : Event(eventId) {
    public val eventType: String = EVENT_TYPE
    override val mergeKey = Objects.hash(eventType, siteId, location, contentId, referrer)

    /**
     * @property siteId the Cxense site identifier.
     * @property location Sets the URL of the page. Must be a syntactically valid URL, or else the event will be dropped.
     * This value will be ignored, if you setup content id.
     * @property contentId Sets content id for URL-less mode. Forces to ignore page location value.
     * @property referrer Sets the URL of the referring page. Must be a syntactically valid URL
     * @property eventId custom event id, that used for tracking locally.
     * @property accountId the Cxense account identifier.
     * @property pageName the page name.
     * @property newUser hint to indicate if this looks like a new user.
     * @property customParameters custom parameters.
     * @property customUserParameters custom user profile parameters.
     * @property externalUserIds external user ids.
     */
    public data class Builder internal constructor(
        private val userProvider: UserProvider,
        var siteId: String,
        var location: String? = null,
        var contentId: String? = null,
        var referrer: String? = null,
        var eventId: String? = null,
        var accountId: Int? = null,
        var pageName: String? = null,
        var newUser: Boolean? = null,
        var customParameters: MutableList<CustomParameter> = mutableListOf(),
        var customUserParameters: MutableList<CustomParameter> = mutableListOf(),
        var externalUserIds: MutableList<ExternalUserId> = mutableListOf(),
    ) {
        /**
         * Initialize Builder with required parameters
         */
        @JvmOverloads
        public constructor(
            siteId: String,
            location: String? = null,
            contentId: String? = null,
            referrer: String? = null,
            eventId: String? = null,
            accountId: Int? = null,
            pageName: String? = null,
            newUser: Boolean? = null,
            customParameters: MutableList<CustomParameter> = mutableListOf(),
            customUserParameters: MutableList<CustomParameter> = mutableListOf(),
            externalUserIds: MutableList<ExternalUserId> = mutableListOf(),
        ) : this(
            DependenciesProvider.getInstance().userProvider,
            siteId,
            location,
            contentId,
            referrer,
            eventId,
            accountId,
            pageName,
            newUser,
            customParameters,
            customUserParameters,
            externalUserIds
        )

        /**
         * Sets site identifier.
         * @param siteId the Cxense site identifier.
         */
        public fun siteId(siteId: String): Builder = apply { this.siteId = siteId }

        /**
         * Sets location URL
         * @param location Sets the URL of the page. Must be a syntactically valid URL, or else the event will be dropped.
         */
        public fun location(location: String?): Builder = apply { this.location = location }

        /**
         * Sets content id
         * @param contentId Sets content id for URL-less mode. Forces to ignore page location value.
         */
        public fun contentId(contentId: String?): Builder = apply { this.contentId = contentId }

        /**
         * Sets referrer URL
         * @param referrer Sets the URL of the referring page. Must be a syntactically valid URL
         */
        public fun referrer(referrer: String?): Builder = apply { this.referrer = referrer }

        /**
         * Sets custom event id
         * @param eventId custom event id, that used for tracking locally.
         */
        public fun eventId(eventId: String?): Builder = apply { this.eventId = eventId }

        /**
         * Sets account identifier.
         * @param accountId the Cxense account identifier.
         */
        public fun accountId(accountId: Int?): Builder = apply { this.accountId = accountId }

        /**
         * Sets the page name.
         * @param pageName the page name.
         */
        public fun pageName(pageName: String?): Builder = apply { this.pageName = pageName }

        /**
         * Sets new user flag.
         * @param newUser hint to indicate if this looks like a new user.
         */
        public fun newUser(newUser: Boolean?): Builder = apply { this.newUser = newUser }

        /**
         * Add custom parameters.
         * @param customParameters one or many [CustomParameter] objects
         */
        public fun addCustomParameters(vararg customParameters: CustomParameter): Builder =
            apply { this.customParameters.addAll(customParameters) }

        /**
         * Add custom parameters.
         * @param customParameters [Iterable] with [CustomParameter] objects
         */
        public fun addCustomParameters(customParameters: Iterable<CustomParameter>): Builder =
            apply { this.customParameters.addAll(customParameters) }

        /**
         * Add custom user profile parameters.
         * @param customUserParameters one or many [CustomParameter] objects
         */
        public fun addCustomUserParameters(vararg customUserParameters: CustomParameter): Builder =
            apply { this.customUserParameters.addAll(customUserParameters) }

        /**
         * Add custom user profile parameters.
         * @param customUserParameters [Iterable] with [CustomParameter] objects
         */
        public fun addCustomUserParameters(customUserParameters: Iterable<CustomParameter>): Builder =
            apply { this.customUserParameters.addAll(customUserParameters) }

        /**
         * Adds external user ids for this event.
         * You can add a maximum of [MAX_EXTERNAL_USER_IDS] external user ids, if you add more, then last will be used.
         * @param externalUserIds one or many [ExternalUserId] objects
         */
        public fun addExternalUserIds(vararg externalUserIds: ExternalUserId): Builder =
            apply { this.externalUserIds.addAll(externalUserIds) }

        /**
         * Adds external user ids for this event.
         * You can add a maximum of [MAX_EXTERNAL_USER_IDS] external user ids, if you add more, then last will be used.
         * @param externalUserIds [Iterable] with [ExternalUserId] objects
         */
        public fun addExternalUserIds(externalUserIds: Iterable<ExternalUserId>): Builder =
            apply { this.externalUserIds.addAll(externalUserIds) }

        /**
         * Builds page view event
         * @throws [IllegalArgumentException] if constraints failed
         */
        public fun build(): PageViewEvent {
            check(location != null || contentId != null) {
                "You should specify page location or content id"
            }
            check(siteId.isNotEmpty()) {
                "Site id can't be empty"
            }
            contentId?.let {
                check(it.isNotEmpty()) {
                    "Content id can't be empty"
                }
            }
            location?.let {
                check(it.toHttpUrlOrNull() != null) {
                    "You should provide valid url as location"
                }
            }
            referrer?.let {
                check(it.toHttpUrlOrNull() != null) {
                    "You should provide valid url as referrer"
                }
            }
            val time = System.currentTimeMillis()

            return PageViewEvent(
                eventId,
                userProvider.userId,
                siteId,
                location,
                contentId,
                referrer,
                accountId,
                pageName,
                newUser,
                Collections.unmodifiableList(customParameters),
                Collections.unmodifiableList(customUserParameters),
                Collections.unmodifiableList(externalUserIds.takeLast(MAX_EXTERNAL_USER_IDS)),
                time,
                DependenciesProvider.getInstance().cxenseConfiguration.randomIdProvider(time)
            )
        }
    }

    public companion object {
        internal const val EVENT_TYPE = "pgv"
        public const val MAX_EXTERNAL_USER_IDS: Int = 5
    }
}
