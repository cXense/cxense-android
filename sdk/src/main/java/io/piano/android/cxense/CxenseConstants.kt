@file:JvmName("CxenseConstants")

package io.piano.android.cxense

import java.util.concurrent.TimeUnit

/**
 * Default dispatch period for events in milliseconds
 */
@get:JvmName("getDefaultDispatchPeriod")
public val DEFAULT_DISPATCH_PERIOD: Long = TimeUnit.SECONDS.toMillis(300)

/**
 * Minimum dispatch period for events in seconds
 */
internal const val MIN_DISPATCH_PERIOD_SECONDS = 10L

/**
 * Minimum dispatch period for events in milliseconds
 */
@get:JvmName("getMinDispatchPeriod")
public val MIN_DISPATCH_PERIOD: Long = TimeUnit.SECONDS.toMillis(MIN_DISPATCH_PERIOD_SECONDS)

/**
 * Default out-date period for events in milliseconds
 */
@get:JvmName("getDefaultOutdatedPeriod")
public val DEFAULT_OUTDATED_PERIOD: Long = TimeUnit.DAYS.toMillis(7)

/**
 * Minimum outdate period for events in milliseconds
 */
@get:JvmName("getMinOutdatePeriodSeconds")
internal val MIN_OUTDATE_PERIOD_SECONDS = TimeUnit.MINUTES.toSeconds(10)

/**
 * Minimum outdate period for events in milliseconds
 */
@get:JvmName("getMinOutdatePeriod")
public val MIN_OUTDATE_PERIOD: Long = TimeUnit.MINUTES.toMillis(10)

/**
 * Endpoint for getting user segments
 */
public const val ENDPOINT_USER_SEGMENTS: String = "profile/user/segment"

/**
 * Endpoint for getting user profile
 */
public const val ENDPOINT_USER_PROFILE: String = "profile/user"

/**
 * Endpoint for getting user external data
 */
public const val ENDPOINT_READ_USER_EXTERNAL_DATA: String = "profile/user/external/read"

/**
 * Endpoint for updating user external data
 */
public const val ENDPOINT_UPDATE_USER_EXTERNAL_DATA: String = "profile/user/external/update"

/**
 * Endpoint for deleting user external data
 */
public const val ENDPOINT_DELETE_USER_EXTERNAL_DATA: String = "profile/user/external/delete"

/**
 * Endpoint for getting user's external identity mapping
 */
public const val ENDPOINT_READ_USER_EXTERNAL_LINK: String = "profile/user/external/link"

/**
 * Endpoint for creating new user's external identity mapping
 */
public const val ENDPOINT_UPDATE_USER_EXTERNAL_LINK: String = "profile/user/external/link/update"

/**
 * Endpoint for pushing DMP events
 */
public const val ENDPOINT_PUSH_DMP_EVENTS: String = "dmp/push"
