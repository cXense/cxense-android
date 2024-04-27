package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Api Error answer
 *
 */
@JsonClass(generateAdapter = true)
internal class ApiError(
    @Json(name = "error") val error: String? = null,
)
