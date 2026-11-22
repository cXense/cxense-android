package io.piano.android.cxense

import android.content.Context
import androidx.annotation.RestrictTo
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.EnumJsonAdapter
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import io.piano.android.cxense.db.DatabaseHelper
import io.piano.android.cxense.model.ApiError
import io.piano.android.cxense.model.ConversionEvent
import io.piano.android.cxense.model.EventDataRequest
import io.piano.android.cxense.model.PerformanceEvent
import io.piano.android.cxense.model.SegmentType
import io.piano.android.cxense.model.TypedItem
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import timber.log.Timber
import java.util.Date
import kotlin.time.Duration.Companion.seconds

/**
 * Builds and provides all other classes
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class DependenciesProvider private constructor(
    context: Context,
) {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable, "Unhandled coroutine exception")
    }
    private val sdkCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default + coroutineExceptionHandler)
    private val userAgentProvider: UserAgentProvider by lazyUnsafe {
        UserAgentProvider(BuildConfig.SDK_VERSION, context)
    }
    private val deviceInfoProvider: DeviceInfoProvider by lazyUnsafe { DeviceInfoProvider(context) }
    private val advertisingIdProvider: AdvertisingIdProvider = AdvertisingIdProvider(context, sdkCoroutineScope)
    private val prefsStorage: PrefsStorage by lazyUnsafe { PrefsStorage(context) }
    internal val userProvider: UserProvider by lazyUnsafe { UserProvider(advertisingIdProvider, prefsStorage) }
    internal val cxenseConfiguration: CxenseConfiguration by lazyUnsafe { CxenseConfiguration() }

    private val okHttpClient: OkHttpClient by lazyUnsafe {
        OkHttpClient.Builder()
            .connectTimeout(10.seconds)
            .readTimeout(10.seconds)
            .addInterceptor(AuthInterceptor(cxenseConfiguration))
            .addInterceptor(SdkInterceptor(BuildConfig.SDK_NAME, BuildConfig.SDK_VERSION))
            .addInterceptor(UserAgentInterceptor(userAgentProvider))
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.HEADERS
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                },
            )
            .addNetworkInterceptor(RedirectLimiterInterceptor("/public/widget/click/"))
            .build()
    }

    private val moshi by lazyUnsafe {
        Moshi.Builder()
            .add(EventDataRequest::class.java, EventsRequestAdapter())
            .add(WidgetItemAdapter)
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(IntStringAdapter)
            .add(DoubleStringAdapter)
            .add(
                SegmentType::class.java,
                EnumJsonAdapter.create(SegmentType::class.java).withUnknownFallback(SegmentType.UNKNOWN),
            )
            .add(
                PolymorphicJsonAdapterFactory.of(TypedItem::class.java, "type")
                    .withSubtype(TypedItem.String::class.java, "string")
                    .withSubtype(TypedItem.Number::class.java, "number")
                    .withSubtype(TypedItem.Time::class.java, "time")
                    .withSubtype(TypedItem.Decimal::class.java, "decimal")
                    .withDefaultValue(TypedItem.Unknown),
            )
            .build()
    }

    private val retrofit: Retrofit by lazyUnsafe {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SDK_ENDPOINT)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    private val cxApi: CxApi by lazyUnsafe {
        retrofit.create()
    }

    private val pageViewEventConverter: PageViewEventConverter by lazyUnsafe {
        PageViewEventConverter(
            moshi.adapter(
                Types.newParameterizedType(
                    Map::class.java,
                    String::class.java,
                    String::class.java,
                ),
            ),
            cxenseConfiguration,
            deviceInfoProvider,
        )
    }
    private val performanceEventConverter: PerformanceEventConverter by lazyUnsafe {
        PerformanceEventConverter(
            moshi.adapter(PerformanceEvent::class.java),
        )
    }

    private val errorParser: ApiErrorParser by lazyUnsafe {
        ApiErrorParser(
            retrofit.responseBodyConverter(
                ApiError::class.java,
                emptyArray(),
            ),
        )
    }

    private val eventRepository: EventRepository by lazyUnsafe {
        EventRepository(
            cxenseConfiguration,
            DatabaseHelper(context),
            listOf(
                pageViewEventConverter,
                performanceEventConverter,
                ConversionEventConverter(moshi.adapter(ConversionEvent::class.java)),
            ),
        )
    }

    private val eventSender: EventSender by lazyUnsafe {
        EventSender(
            cxApi,
            eventRepository,
            cxenseConfiguration,
            deviceInfoProvider,
            userProvider,
            pageViewEventConverter,
            performanceEventConverter,
            errorParser,
        )
    }

    internal val cxenseSdk: CxenseSdk by lazyUnsafe {
        CxenseSdk(
            sdkCoroutineScope,
            cxenseConfiguration,
            advertisingIdProvider,
            userProvider,
            cxApi,
            errorParser,
            moshi,
            eventRepository,
            eventSender,
        )
    }

    companion object {
        @Volatile
        private var instance: DependenciesProvider? = null

        internal fun init(context: Context): DependenciesProvider {
            if (instance == null) {
                instance = DependenciesProvider(context)
            }
            return getInstance()
        }

        fun getInstance(): DependenciesProvider {
            checkNotNull(instance) {
                "The Cxense SDK is not initialized! Make sure to call `AppInitializer.getInstance(context)" +
                    ".initializeComponent(CxSdkInitializer::class.java)` before calling other methods."
            }
            return instance as DependenciesProvider
        }
    }
}
