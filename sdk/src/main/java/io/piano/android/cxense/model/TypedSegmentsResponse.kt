package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response for segments from server.
 *
 */
@JsonClass(generateAdapter = true)
public class TypedSegmentsResponse(
    @Json(name = "segments") public val segments: List<Segment> = emptyList(),
)
