package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Segment information
 */
@JsonClass(generateAdapter = true)
public class Segment(
    @Json(name = "id") public val id: String,
    @Json(name = "shortId") public val shortId: String,
    @Json(name = "type") public val type: SegmentType,
)
