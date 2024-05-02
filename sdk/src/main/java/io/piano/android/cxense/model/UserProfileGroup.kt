package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Group object for item in {@link UserProfile}
 *
 */
@JsonClass(generateAdapter = true)
public class UserProfileGroup(
    @Json(name = "group") public val group: String?,
    @Json(name = "count") public val count: Int,
    @Json(name = "weight") public val weight: Double,
)
