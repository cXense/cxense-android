@file:JvmName("KotlinExtensions")

package io.piano.android.cxense

import io.piano.android.cxense.model.CandidateSegment
import io.piano.android.cxense.model.ContentUser
import io.piano.android.cxense.model.Impression
import io.piano.android.cxense.model.Segment
import io.piano.android.cxense.model.User
import io.piano.android.cxense.model.UserExternalTypedData
import io.piano.android.cxense.model.UserIdentity
import io.piano.android.cxense.model.UserSegmentRequest
import io.piano.android.cxense.model.WidgetContext
import io.piano.android.cxense.model.WidgetItem
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public suspend fun CxenseSdk.trackClick(item: WidgetItem): Unit =
    suspendCancellableCoroutine { continuation ->
        trackClick(
            item,
            object : LoadCallback<Unit> {
                override fun onSuccess(data: Unit) {
                    continuation.resume(Unit)
                }

                override fun onError(throwable: Throwable) {
                    continuation.resumeWithException(throwable)
                }
            }
        )
    }

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public suspend fun CxenseSdk.trackClick(url: String): Unit =
    suspendCancellableCoroutine { continuation ->
        trackClick(
            url,
            object : LoadCallback<Unit> {
                override fun onSuccess(data: Unit) {
                    continuation.resume(Unit)
                }

                override fun onError(throwable: Throwable) {
                    continuation.resumeWithException(throwable)
                }
            }
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
        object : LoadCallback<List<WidgetItem>> {
            override fun onSuccess(data: List<WidgetItem>) {
                continuation.resume(data)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public suspend fun CxenseSdk.reportWidgetVisibilities(
    vararg impressions: Impression,
): Unit = suspendCancellableCoroutine { continuation ->
    reportWidgetVisibilities(
        object : LoadCallback<Unit> {
            override fun onSuccess(data: Unit) {
                continuation.resume(Unit)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        },
        *impressions
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
        object : LoadCallback<List<Segment>> {
            override fun onSuccess(data: List<Segment>) {
                continuation.resume(data)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
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
        object : LoadCallback<User> {
            override fun onSuccess(data: User) {
                continuation.resume(data)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
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
        object : LoadCallback<List<UserExternalTypedData>> {
            override fun onSuccess(data: List<UserExternalTypedData>) {
                continuation.resume(data)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public suspend fun CxenseSdk.setUserExternalTypedData(
    userExternalData: UserExternalTypedData,
): Unit = suspendCancellableCoroutine { continuation ->
    setUserExternalTypedData(
        userExternalData,
        object : LoadCallback<Unit> {
            override fun onSuccess(data: Unit) {
                continuation.resume(Unit)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public suspend fun CxenseSdk.deleteUserExternalData(
    identity: UserIdentity,
): Unit = suspendCancellableCoroutine { continuation ->
    deleteUserExternalData(
        identity,
        object : LoadCallback<Unit> {
            override fun onSuccess(data: Unit) {
                continuation.resume(Unit)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
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
        object : LoadCallback<UserIdentity> {
            override fun onSuccess(data: UserIdentity) {
                continuation.resume(data)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
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
        object : LoadCallback<UserIdentity> {
            override fun onSuccess(data: UserIdentity) {
                continuation.resume(data)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
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
        object : LoadCallback<T> {
            override fun onSuccess(data: T) {
                continuation.resume(data)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
    )
}
