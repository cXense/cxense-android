package io.piano.android.cxense

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class UserProviderTest {
    private val advertisingIdProvider: AdvertisingIdProvider = mock()
    private val prefsStorage: PrefsStorage = mock {
        on { defaultUserId } doReturn FALLBACK_USER_ID
    }
    private val provider = UserProvider(advertisingIdProvider, prefsStorage)

    @Test
    fun getUserId() {
        whenever(advertisingIdProvider.defaultUserId).thenReturn(USER_ID)
        assertEquals(USER_ID, provider.userId)
        assertEquals(USER_ID, provider.userId)
        verify(advertisingIdProvider, times(1)).defaultUserId
    }

    @Test
    fun getUserIdBadAaid() {
        whenever(advertisingIdProvider.defaultUserId).thenReturn(UserProvider.BAD_AAID)
        assertEquals(FALLBACK_USER_ID, provider.userId)
    }

    @Test
    fun getUserIdNotAaid() {
        whenever(advertisingIdProvider.defaultUserId).thenReturn(null)
        assertEquals(FALLBACK_USER_ID, provider.userId)
    }

    @Test
    fun setInvalidUserId() {
        assertFailsWithMessage<IllegalArgumentException>("user id must be", "Expected fail for user id") {
            provider.userId = "1"
        }
    }

    companion object {
        private const val USER_ID = "test user id"
        private const val FALLBACK_USER_ID = "test fallback user id"
    }
}
