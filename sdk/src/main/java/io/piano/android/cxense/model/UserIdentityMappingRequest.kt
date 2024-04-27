package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request object for getting/adding user identity mapping from server
 *
 */
@JsonClass(generateAdapter = true)
public class UserIdentityMappingRequest(
    @Json(name = "cxid") public val cxenseId: String,
    @Json(name = "type") public val type: String,
    @Json(name = "id") public val id: String? = null,
)
