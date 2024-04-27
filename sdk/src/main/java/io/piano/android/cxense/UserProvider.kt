package io.piano.android.cxense

import androidx.annotation.RestrictTo
import io.piano.android.cxense.model.ContentUser
import java.util.UUID

/**
 * Provides user id and content user
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class UserProvider(
    private val advertisingIdProvider: AdvertisingIdProvider,
    private val prefsStorage: PrefsStorage,
) {
    private var currentUserId: String? = null
    private val fallbackUserId: String = prefsStorage.defaultUserId ?: UUID.randomUUID().toString().also {
        prefsStorage.defaultUserId = it
    }

    val defaultUser: ContentUser by lazy { ContentUser(userId) }
    var userId: String
        get() = currentUserId ?: advertisingIdProvider.defaultUserId.takeUnless { it == BAD_AAID }?.also {
            currentUserId = it
        } ?: fallbackUserId
        set(value) {
            currentUserId = value.also {
                require(it.matches(ID_REGEX.toRegex())) {
                    "The user id must be at least 16 characters long. Allowed characters are:" +
                        " A-Z, a-z, 0-9, \"_\", \"-\", \"+\" and \".\"."
                }
            }
        }

    companion object {
        const val ID_REGEX = "^[\\w-+.]{16,}$"
        internal const val BAD_AAID = "00000000-0000-0000-0000-000000000000"
    }
}
