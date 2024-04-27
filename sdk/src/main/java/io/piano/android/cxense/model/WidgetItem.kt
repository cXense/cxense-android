package io.piano.android.cxense.model

/**
 * Widget item
 * @property title Item title
 * @property url Item url
 * @property clickUrl Click url for item
 * @property properties Item custom properties
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public class WidgetItem(
    public val title: String?,
    public val url: String?,
    public val clickUrl: String?,
    public val properties: Map<String, Any>,
)
