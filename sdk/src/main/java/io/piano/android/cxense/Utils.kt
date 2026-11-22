package io.piano.android.cxense

import retrofit2.Response
import kotlin.coroutines.cancellation.CancellationException

internal fun <T> lazyUnsafe(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)

internal suspend inline fun <R> runSuspendCatching(crossinline block: suspend () -> R): Result<R> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    Result.failure(e)
}

internal suspend inline fun <T : Any> safeApiCall(
    errorParser: ApiErrorParser,
    crossinline block: suspend () -> Response<T>,
): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    Result.failure(e)
}.mapCatching { response ->
    if (response.isSuccessful) {
        response.body() ?: throw BaseException("Response body is null")
    } else {
        throw errorParser.parseError(response) ?: BaseException("Response body is null")
    }
}
