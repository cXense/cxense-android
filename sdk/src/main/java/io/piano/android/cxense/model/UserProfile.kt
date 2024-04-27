package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * User generated content profile
 *
 */
@JsonClass(generateAdapter = true)
public class UserProfile(
    @Json(name = "item") public val item: String?,
    @Json(name = "groups") public val groups: List<UserProfileGroup>?,
)
