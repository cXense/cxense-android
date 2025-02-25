package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
public enum class SegmentType {
    @Json(name = "traffic")
    TRAFFIC,

    @Json(name = "external")
    EXTERNAL,

    @Json(name = "lookalike")
    LOOKALIKE,

    @Json(name = "contextual")
    CONTEXTUAL,

    @Json(name = "combined")
    COMBINED,

    @Json(name = "unknown")
    UNKNOWN,
}
