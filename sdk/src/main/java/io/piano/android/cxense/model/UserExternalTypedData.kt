package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data associated with the user(s).
 * @property items stored key-values for the user.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JsonClass(generateAdapter = true)
class UserExternalTypedData internal constructor(
    type: String,
    id: String,
    @Json(name = "profile") val items: List<ExternalTypedItem>,
) : UserIdentity(type, id) {

    /**
     * @constructor Initialize Builder with required parameters
     * @property identity user identifier with type and id
     * @property externalItems stored key-values for the user.
     */
    data class Builder @JvmOverloads constructor(
        var identity: UserIdentity,
        var externalItems: MutableList<ExternalTypedItem> = mutableListOf(),
    ) {

        /**
         * Adds known user identities to identify the user.
         * @param items one or multiple [UserIdentity] objects.
         */
        fun addExternalItems(vararg items: ExternalTypedItem) = apply { this.externalItems.addAll(items) }

        /**
         * Adds known user identities to identify the user.
         * @param items [Iterable] with [UserIdentity] objects.
         */
        fun addExternalItems(items: Iterable<ExternalTypedItem>) = apply { this.externalItems.addAll(items) }

        /**
         * Sets user identity
         * @param identity user identity
         */
        fun identity(identity: UserIdentity) = apply { this.identity = identity }

        /**
         * Builds user external data
         * @throws [IllegalArgumentException] if constraints failed
         */
        fun build(): UserExternalTypedData {
            check(externalItems.size <= MAX_PROFILE_ITEMS) {
                "Too many profile items. Current size: ${externalItems.size}, allowed max size: $MAX_PROFILE_ITEMS"
            }
            return UserExternalTypedData(
                identity.type,
                identity.id,
                externalItems.map {
                    ExternalTypedItem("${identity.type}-${it.group}", it.item)
                }
            )
        }
    }

    companion object {
        const val MAX_PROFILE_ITEMS = 40
    }
}
