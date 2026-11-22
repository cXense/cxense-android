package io.piano.android.cxense

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesRepairableException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Provides AAID
 */
internal class AdvertisingIdProvider(
    private val context: Context,
    scope: CoroutineScope,
) {
    private var advertisingInfo: AdvertisingIdClient.Info? = null

    private suspend fun initAdvertisingIdTask() {
        runSuspendCatching {
            delay(DELAY)
            AdvertisingIdClient.getAdvertisingIdInfo(context)
        }.onSuccess {
            advertisingInfo = it
        }.onFailure { e ->
            Timber.e(e)
            if (e is IOException || e is GooglePlayServicesRepairableException) {
                initAdvertisingIdTask()
            }
        }
    }

    val defaultUserId: String?
        get() = advertisingInfo?.id

    val limitAdTrackingEnabled: Boolean
        get() = advertisingInfo?.isLimitAdTrackingEnabled ?: false

    init {
        scope.launch(Dispatchers.IO) {
            initAdvertisingIdTask()
        }
    }

    companion object {
        private val DELAY: Duration = 300.milliseconds
    }
}
