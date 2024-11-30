package io.piano.android.cxense.model

/**
 * User consent settings
 * @since 2.1.0
 */
public class ConsentSettings {
    private val map: MutableMap<String, Boolean> = mapping.mapValuesTo(mutableMapOf()) { false }
    internal val consents: List<String>
        get() = map.filterValues { it }.keys.mapNotNull(mapping::get)

    /**
     * Consent version, that will be used
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public var version: Int = 1
        set(value) = require(value in 1..2) {
            "Consent version $value is not supported. Allowed values range: [1..2]"
        }.also {
            field = value
        }

    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public var consentRequired: Boolean by map
        private set

    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public var pvAllowed: Boolean by map
        private set

    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public var recsAllowed: Boolean by map
        private set

    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public var segmentAllowed: Boolean by map
        private set

    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public var deviceAllowed: Boolean by map
        private set

    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public var geoAllowed: Boolean by map
        private set

    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public var adAllowed: Boolean by map
        private set

    /**
     * Explicit consent from user is required before processing data
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public fun consentRequired(value: Boolean): ConsentSettings = apply { consentRequired = value }

    /**
     * User allowed Page view tracking, DMP event tracking and browsing habit collection to understand a user’s interests and profile.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public fun pvAllowed(value: Boolean): ConsentSettings = apply { pvAllowed = value }

    /**
     * User allowed personalisation of content recommendations and suggested content based on user interests and browsing habits.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public fun recsAllowed(value: Boolean): ConsentSettings = apply { recsAllowed = value }

    /**
     * User allowed audience segmentation - processing of browsing habits and first party data to include users in specific audience segments.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public fun segmentAllowed(value: Boolean): ConsentSettings = apply { segmentAllowed = value }

    /**
     * User allowed usage of user-agent and other device-specific data. Ignored, if version set to 1.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public fun deviceAllowed(value: Boolean): ConsentSettings = apply { deviceAllowed = value }

    /**
     * User allowed usage of geolocation with page view events. Ignored, if version set to 1.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public fun geoAllowed(value: Boolean): ConsentSettings = apply { geoAllowed = value }

    /**
     * User allowed targeting advertising based on browsing habits and audience segmentation.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    public fun adAllowed(value: Boolean): ConsentSettings = apply { adAllowed = value }

    private companion object {
        @JvmStatic
        private val mapping = mapOf(
            "consentRequired" to "y",
            "pvAllowed" to "pv",
            "recsAllowed" to "recs",
            "segmentAllowed" to "segment",
            "deviceAllowed" to "device",
            "geoAllowed" to "geo",
            "adAllowed" to "ad",
        )
    }
}
