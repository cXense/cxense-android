package io.piano.android.cxense.model

/**
 * External User Id description.
 * @param userType external user type
 * @property userType external user type
 *
 * @param userId external user id
 * @property userId external user id
 *
 * @throws IllegalArgumentException if [userType] is long or [userId] doesn't meet a criteria.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
public class ExternalUserId(
    userType: String,
    userId: String,
) {
    public val userType: String = userType.also {
        require(it.length <= USER_TYPE_MAX_LENGTH) {
            "User type can't be longer than $USER_TYPE_MAX_LENGTH symbols"
        }
    }

    public val userId: String = userId.also {
        if (userType == CX_USER_TYPE) {
            require(it.matches(CX_USER_ID_REGEX.toRegex())) {
                "The valid characters for internal ids are digits, letters, space and the special characters" +
                    " \"=@+-_.\". Max length is 64 symbols"
            }
        } else {
            require(it.matches(CUSTOMER_USER_ID_REGEX.toRegex())) {
                "The valid characters for external ids are digits, letters, space and the special characters" +
                    " !#$%%&()*+,-./;<=>?@[]^_{}~|. Max length is 100 symbols"
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExternalUserId

        if (userType != other.userType) return false

        return true
    }

    override fun hashCode(): Int = userType.hashCode()

    private companion object {
        private const val CX_USER_TYPE = "cx"
        private const val USER_TYPE_MAX_LENGTH = 10
        private const val CX_USER_ID_REGEX = "^[\\w =@+\\-\\.]{1,64}$"
        private const val CUSTOMER_USER_ID_REGEX = "^[\\w !#$%&()*+,\\-./;<=>?@\\[\\]^{}~|]{1,100}$"
    }
}
