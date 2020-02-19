package com.cxense.cxensesdk

import com.cxense.cxensesdk.model.ConsentOption
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * Cxense SDK configuration class
 * @property autoMetaInfoTrackingEnabled Should meta information about application be tracked automatically.
 * Note: if 'true', events will be enriched by custom parameters with information about application's name, version and etc.
 * @property dispatchPeriod Current dispatch period in milliseconds.
 * @property minimumNetworkStatus The minimum network status for sending events.
 * @property outdatePeriod Current out-date period in milliseconds.
 * @property credentialsProvider Credential provider, which provide username/api key dynamically.
 * @property consentOptions Current consent options for user.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
class CxenseConfiguration {
    var autoMetaInfoTrackingEnabled: Boolean = true
    var dispatchPeriod: Long by Delegates.observable(DEFAULT_DISPATCH_PERIOD) { _, oldValue, newValue ->
        if (oldValue != newValue)
            dispatchPeriodListener?.invoke(newValue)
    }
        private set

    var minimumNetworkStatus: NetworkStatus = NetworkStatus.NONE
    var outdatePeriod: Long = DEFAULT_OUTDATED_PERIOD
        private set
    var credentialsProvider: CredentialsProvider = object : CredentialsProvider {
        override fun getUsername(): String = ""
        override fun getApiKey(): String = ""
        override fun getDmpPushPersistentId(): String = ""
    }

    val consentOptions: MutableSet<ConsentOption> = mutableSetOf()

    internal val consentOptionsValues
        get() = consentOptions.map { it.value }

    internal var dispatchPeriodListener: ((Long) -> Unit)? = null

    /**
     * Sets dispatch period for the dispatcher. The dispatcher will check for events to dispatch
     * every {@code period}.
     *
     * @param period the dispatch period
     * @param unit the time unit of the period parameter
     * @throws IllegalArgumentException if period smaller then CxenseConstants.MIN_DISPATCH_PERIOD
     */
    fun dispatchPeriod(period: Long, unit: TimeUnit) {
        val millis = unit.toMillis(period)
        require(millis >= MIN_DISPATCH_PERIOD) {
            "Period must be greater than $MIN_DISPATCH_PERIOD_SECONDS seconds"
        }
        dispatchPeriod = millis
    }

    /**
     * Sets out-date period for events. The dispatcher will delete all events, that tracked more than {@code period}.
     *
     * @param period the dispatch period
     * @param unit the time unit of the period parameter
     * @throws IllegalArgumentException if period smaller than CxenseConstants.MIN_DISPATCH_PERIOD
     */
    fun outdatePeriod(period: Long, unit: TimeUnit) {
        val millis = unit.toMillis(period)
        require(millis >= MIN_OUTDATE_PERIOD) {
            "Period must be greater than $MIN_OUTDATE_PERIOD_SECONDS seconds"
        }
        outdatePeriod = millis
    }

    /**
     * Network statuses ordered by connection capability.
     */
    enum class NetworkStatus {
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
        WIFI
    }
}
