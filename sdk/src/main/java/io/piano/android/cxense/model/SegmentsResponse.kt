package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response for segments from server.
 *
 */
@Deprecated("See TypedSegmentsResponse")
@JsonClass(generateAdapter = true)
public class SegmentsResponse(
    @Json(name = "segments") public val ids: List<String> = emptyList(),
)
