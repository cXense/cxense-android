package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response for segments lookup from server.
 *
 */
@JsonClass(generateAdapter = true)
public class SegmentLookupResponse(
    @Json(name = "segments") public val segments: List<Segment> = emptyList(),
    @Json(name = "segmentCompositionTrees") public val segmentCompositionTrees: List<String> = emptyList(),
)
