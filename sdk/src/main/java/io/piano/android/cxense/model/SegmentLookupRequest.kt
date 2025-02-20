package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request object for getting segments from server.
 *
 */
@JsonClass(generateAdapter = true)
public class SegmentLookupRequest(
    @Json(name = "siteGroupIds") public val siteGroups: List<String>,
    @Json(name = "identities") public val identities: List<UserIdentity>? = null,
    @Json(name = "candidateSegmentIds") public val candidateSegmentIds: List<String>? = null,
    @Json(name = "shortIds") public val shortIds: Boolean = false,
    @Json(name = "context") public val segmentContext: SegmentContext? = null,
) {
    @JsonClass(generateAdapter = true)
    public class SegmentContext(
        @Json(name = "siteId") public val siteId: String,
        @Json(name = "url") public val url: String,
    )
}
