package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request object for getting user segments from server.
 *
 */
@JsonClass(generateAdapter = true)
public class UserSegmentRequest(
    @Json(name = "identities") public val identities: List<UserIdentity>,
    @Json(name = "siteGroupIds") public val siteGroups: List<String>?,
    @Json(name = "candidateSegments") public val candidateSegments: List<CandidateSegment>? = null,
    @Json(name = "format") public val format: ResponseFormat = ResponseFormat.CX,
    @Json(name = "segmentFormat") public val segmentFormat: SegmentFormat = SegmentFormat.STANDARD,
) {
    @JsonClass(generateAdapter = false)
    public enum class ResponseFormat {
        @Deprecated("Will be replaced with [CX_TYPED] in future")
        @Json(name = "cx")
        CX,

        @Json(name = "cx_typed")
        CX_TYPED,
    }

    @JsonClass(generateAdapter = false)
    public enum class SegmentFormat {
        @Json(name = "standard")
        STANDARD,

        @Json(name = "short_ids")
        SHORT_IDS,
    }
}
