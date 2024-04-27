package io.piano.android.cxense.model

/**
 * Event status
 * @property eventId custom event id
 * @property isSent is event sent
 * @property exception exception thrown at sending
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public class EventStatus(
    public val eventId: String?,
    public val isSent: Boolean,
    public val exception: Exception? = null,
)
