package io.piano.android.cxense

import io.piano.android.cxense.model.ConsentSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Cxense SDK configuration class
 * @property autoMetaInfoTrackingEnabled Should meta information about application be tracked automatically.
 * Note: if 'true', events will be enriched by custom parameters with information about application's name, version and etc.
 * @property dispatchPeriod Current dispatch period.
 * @property minimumNetworkStatus The minimum network status for sending events.
 * @property outdatePeriod Current out-date period.
 * @property credentialsProvider Credential provider, which provide username/api key dynamically.
 * @property consentSettings Current consent settings for user.
 * @property eventsMergePeriod Current event merging period (for deduplication)
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public class CxenseConfiguration {
    private val dispatchPeriodMutableFlow = MutableStateFlow(DEFAULT_DISPATCH_PERIOD)
    internal val dispatchPeriodFlow = dispatchPeriodMutableFlow.asStateFlow()

    public var autoMetaInfoTrackingEnabled: Boolean = true

    /**
     * @throws IllegalArgumentException if period smaller than {#MIN_DISPATCH_PERIOD]
     */
    public var dispatchPeriod: Duration = DEFAULT_DISPATCH_PERIOD
        set(value) {
            require(value >= MIN_DISPATCH_PERIOD) {
                "Period must be greater than $MIN_DISPATCH_PERIOD"
            }
            if (field != value) {
                field = value
                dispatchPeriodMutableFlow.value = value
            }
        }

    public var minimumNetworkStatus: NetworkStatus = NetworkStatus.NONE

    /**
     * @throws IllegalArgumentException if period smaller than {#MIN_OUTDATE_PERIOD]
     */
    public var outdatePeriod: Duration = DEFAULT_OUTDATED_PERIOD
        set(value) {
            require(value >= MIN_OUTDATE_PERIOD) {
                "Period must be greater than $MIN_OUTDATE_PERIOD"
            }
            field = value
        }
    public var credentialsProvider: CredentialsProvider = object : CredentialsProvider {
        override fun getUsername(): String = ""
        override fun getApiKey(): String = ""
        override fun getDmpPushPersistentId(): String = ""
    }

    public var consentSettings: ConsentSettings = ConsentSettings()
    public var eventsMergePeriod: Duration = Duration.ZERO
        set(value) {
            require(value >= Duration.ZERO) {
                "Period must be at least 0"
            }
            field = value
        }
    public var sendEventsAtPush: Boolean = false

    public var randomIdProvider: (Long) -> String = { "$it${(Math.random() * 10E8).toInt()}" }

    /**
     * Network statuses ordered by connection capability.
     */
    public enum class NetworkStatus {
        /**
         * No network.
         */
        NONE,

        /**
         * GPRS connection.
         */
        GPRS,

        /**
         * A mobile connection (3G/4G/LTE).
         */
        MOBILE,

        /**
         * A Wi-Fi connection.
         */
        WIFI,
    }

    public companion object {
        /**
         * Default dispatch period for events
         */
        public val DEFAULT_DISPATCH_PERIOD: Duration = 300.seconds

        /**
         * Minimum dispatch period for events
         */
        public val MIN_DISPATCH_PERIOD: Duration = 10.seconds

        /**
         * Default out-date period for events
         */
        public val DEFAULT_OUTDATED_PERIOD: Duration = 7.days

        /**
         * Minimum out-date period for events
         */
        public val MIN_OUTDATE_PERIOD: Duration = 10.minutes
    }
}
