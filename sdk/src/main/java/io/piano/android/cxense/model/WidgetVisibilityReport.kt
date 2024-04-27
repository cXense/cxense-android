package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request object for reporting widgets visibilities to server.
 *
 */
@JsonClass(generateAdapter = true)
public class WidgetVisibilityReport(
    @Json(name = "impressions") public val impressions: List<Impression>,
)
