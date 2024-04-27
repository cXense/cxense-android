package io.piano.android.cxense.model

import com.squareup.moshi.JsonClass

/**
 * Response for widget from server.
 */
@JsonClass(generateAdapter = true)
public class WidgetResponse(
    public val items: List<WidgetItem> = emptyList(),
    public val template: String? = null,
    public val style: String? = null,
)
