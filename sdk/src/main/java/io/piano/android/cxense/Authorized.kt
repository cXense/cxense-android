package io.piano.android.cxense

/**
 * This marker annotation indicates, that Retrofit API method should be with auth header
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class Authorized
