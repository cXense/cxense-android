package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request object for getting user from server
 *
 */
@JsonClass(generateAdapter = true)
public class UserDataRequest(
    type: String,
    id: String,
    @Json(name = "groups") public val groups: List<String>? = null,
    @Json(name = "recent") public val recent: Boolean? = null,
    @Json(name = "identityTypes") public val identityTypes: List<String>? = null,
) : UserIdentity(type, id) {
    public constructor(
        userIdentity: UserIdentity,
        @Json(name = "groups") groups: List<String>? = null,
        @Json(name = "recent") recent: Boolean? = null,
        @Json(name = "identityTypes") identityTypes: List<String>? = null,
    ) : this(userIdentity.type, userIdentity.id, groups, recent, identityTypes)
}
