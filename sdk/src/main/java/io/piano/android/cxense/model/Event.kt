package io.piano.android.cxense.model

/**
 * Base class for all events
 * @param eventId custom event id, that used for tracking locally.
 */
public abstract class Event protected constructor(
    @Transient public val eventId: String?,
) {
    internal abstract val mergeKey: Int
}
