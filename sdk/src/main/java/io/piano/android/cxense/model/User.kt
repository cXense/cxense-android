package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * User object.
 *
 */
@JsonClass(generateAdapter = true)
public class User(
    type: String,
    id: String,
    @Json(name = "profile") public val profiles: List<UserProfile>,
    @Json(name = "identities") public val identities: List<UserIdentity>,
) : UserIdentity(type, id)
