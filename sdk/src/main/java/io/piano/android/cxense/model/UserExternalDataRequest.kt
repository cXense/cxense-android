package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request object for getting user external data from server
 *
 */
@JsonClass(generateAdapter = true)
public class UserExternalDataRequest(
    @Json(name = "type") public val type: String,
    @Json(name = "id") public val id: String?,
    @Json(name = "filter") public val filter: String?,
    @Json(name = "groups") public val groups: List<String>?,
    @Json(name = "format") public val format: ResponseFormat = ResponseFormat.LEGACY,
) {
    @JsonClass(generateAdapter = false)
    public enum class ResponseFormat {
        @Deprecated("Will be replaced with [TYPED] in future")
        @Json(name = "legacy")
        LEGACY,

        @Json(name = "typed")
        TYPED,
    }
}
