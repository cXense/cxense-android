package com.cxense.cxensesdk

import androidx.annotation.RestrictTo
import com.cxense.cxensesdk.model.ContentUser
import java.util.UUID

@RestrictTo(RestrictTo.Scope.LIBRARY)
class UserProvider(
    private val advertisingIdProvider: AdvertisingIdProvider
) {
    private var currentUserId: String? = null

    val defaultUser: ContentUser by lazy { ContentUser(userId) }
    var userId: String
        get() = currentUserId ?: advertisingIdProvider.defaultUserId?.also { currentUserId = it }
        ?: UUID.randomUUID().toString()
        set(value) {
            currentUserId = value.also {
                require(it.matches(ID_REGEX.toRegex())) {
                    "The user id must be at least 16 characters long. Allowed characters are: A-Z, a-z, 0-9, \"_\", \"-\", \"+\" and \".\"."
                }
            }
        }

    companion object {
        const val ID_REGEX = "^[\\w-+.]{16,}$"
    }
}