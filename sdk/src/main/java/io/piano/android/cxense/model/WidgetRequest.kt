package io.piano.android.cxense.model

import com.squareup.moshi.JsonClass

/**
 * Request widget data object for server
 */
@JsonClass(generateAdapter = true)
public class WidgetRequest(
    public val widgetId: String,
    public val consent: List<String>,
    public val context: WidgetContext? = null,
    public val user: ContentUser? = null,
    public val tag: String? = null,
    public val prnd: String? = null,
    public val experienceId: String? = null,
)
