package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response for data associated with the user(s) from server.
 *
 */
@JsonClass(generateAdapter = true)
class UserExternalTypedDataResponse(
    @Json(name = "data") val items: List<UserExternalTypedData> = emptyList(),
)
