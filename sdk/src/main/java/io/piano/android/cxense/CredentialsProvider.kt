package io.piano.android.cxense

/**
 * Interface for providing API credentials.
 * Note that SDK doesn't cache results from these methods, which allows you to dynamically change credentials without replacing provider
 */
public interface CredentialsProvider {
    /**
     * Returns username
     *
     * @return username
     */
    public fun getUsername(): String

    /**
     * Returns Api key
     *
     * @return api key
     */
    public fun getApiKey(): String

    /**
     * Returns persistent query id for pushing DMP Performance events without authorization
     *
     * @return persistent query id
     */
    public fun getDmpPushPersistentId(): String
}
