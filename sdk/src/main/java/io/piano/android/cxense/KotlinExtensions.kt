@file:JvmName("KotlinExtensions")

package io.piano.android.cxense

import io.piano.android.cxense.model.CandidateSegment
import io.piano.android.cxense.model.ContentUser
import io.piano.android.cxense.model.Impression
import io.piano.android.cxense.model.Segment
import io.piano.android.cxense.model.SegmentLookupRequest.SegmentContext
import io.piano.android.cxense.model.SegmentLookupResponse
import io.piano.android.cxense.model.User
import io.piano.android.cxense.model.UserExternalTypedData
import io.piano.android.cxense.model.UserIdentity
import io.piano.android.cxense.model.UserSegmentRequest
import io.piano.android.cxense.model.WidgetContext
import io.piano.android.cxense.model.WidgetItem
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public suspend fun CxenseSdk.trackClick(item: WidgetItem): Unit =
    suspendCancellableCoroutine { continuation ->
        trackClick(
            item,
            continuation.toLoadCallback(),
        )
    }

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public suspend fun CxenseSdk.trackClick(url: String): Unit =
    suspendCancellableCoroutine { continuation ->
        trackClick(
            url,
            continuation.toLoadCallback(),
        )
    }

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JvmOverloads
public suspend fun CxenseSdk.loadWidgetRecommendations(
    widgetId: String,
    widgetContext: WidgetContext? = null,
    user: ContentUser? = null,
    tag: String? = null,
    prnd: String? = null,
    experienceId: String? = null,
): List<WidgetItem> = suspendCancellableCoroutine { continuation ->
    loadWidgetRecommendations(
        widgetId,
        widgetContext,
        user,
        tag,
        prnd,
        experienceId,
        continuation.toLoadCallback(),
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public suspend fun CxenseSdk.reportWidgetVisibilities(
    vararg impressions: Impression,
): Unit = suspendCancellableCoroutine { continuation ->
    reportWidgetVisibilities(
        continuation.toLoadCallback(),
        *impressions,
    )
}

public suspend fun CxenseSdk.lookupSegments(
    siteGroupIds: List<String>,
    identities: List<UserIdentity>? = null,
    candidateSegmentIds: List<String>? = null,
    shortIds: Boolean = false,
    segmentContext: SegmentContext? = null,
): SegmentLookupResponse = suspendCancellableCoroutine { continuation ->
    lookupSegments(
        siteGroupIds,
        identities,
        candidateSegmentIds,
        shortIds,
        segmentContext,
        continuation.toLoadCallback(),
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public suspend fun CxenseSdk.getUserSegments(
    identities: List<UserIdentity>,
    siteGroupIds: List<String>,
    candidateSegments: List<CandidateSegment>? = null,
    segmentFormat: UserSegmentRequest.SegmentFormat = UserSegmentRequest.SegmentFormat.STANDARD,
): List<Segment> = suspendCancellableCoroutine { continuation ->
    getUserSegments(
        identities,
        siteGroupIds,
        candidateSegments,
        segmentFormat,
        continuation.toLoadCallback(),
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JvmOverloads
public suspend fun CxenseSdk.getUser(
    identity: UserIdentity,
    groups: List<String>? = null,
    recent: Boolean? = null,
    identityTypes: List<String>? = null,
): User = suspendCancellableCoroutine { continuation ->
    getUser(
        identity,
        groups,
        recent,
        identityTypes,
        continuation.toLoadCallback(),
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JvmOverloads
public suspend fun CxenseSdk.getUserExternalTypedData(
    type: String,
    id: String? = null,
    filter: String? = null,
    groups: List<String>? = null,
): List<UserExternalTypedData> = suspendCancellableCoroutine { continuation ->
    getUserExternalTypedData(
        type,
        id,
        filter,
        groups,
        continuation.toLoadCallback(),
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public suspend fun CxenseSdk.setUserExternalTypedData(
    userExternalData: UserExternalTypedData,
): Unit = suspendCancellableCoroutine { continuation ->
    setUserExternalTypedData(
        userExternalData,
        continuation.toLoadCallback(),
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public suspend fun CxenseSdk.deleteUserExternalData(
    identity: UserIdentity,
): Unit = suspendCancellableCoroutine { continuation ->
    deleteUserExternalData(
        identity,
        continuation.toLoadCallback(),
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public suspend fun CxenseSdk.getUserExternalLink(
    cxenseId: String,
    type: String,
): UserIdentity = suspendCancellableCoroutine { continuation ->
    getUserExternalLink(
        cxenseId,
        type,
        continuation.toLoadCallback(),
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public suspend fun CxenseSdk.addUserExternalLink(
    cxenseId: String,
    identity: UserIdentity,
): UserIdentity = suspendCancellableCoroutine { continuation ->
    addUserExternalLink(
        cxenseId,
        identity,
        continuation.toLoadCallback(),
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JvmOverloads
public suspend inline fun <reified T : Any> CxenseSdk.executePersistedQuery(
    url: String,
    persistentQueryId: String,
    data: Any? = null,
): T = suspendCancellableCoroutine { continuation ->
    executePersistedQuery(
        url,
        persistentQueryId,
        data,
        continuation.toLoadCallback(),
    )
}

@PublishedApi
internal inline fun <reified T : Any> CancellableContinuation<T>.toLoadCallback(): LoadCallback<T> =
    object : LoadCallback<T> {
        override fun onSuccess(data: T) {
            resume(data)
        }

        override fun onError(throwable: Throwable) {
            resumeWithException(throwable)
        }
    }
