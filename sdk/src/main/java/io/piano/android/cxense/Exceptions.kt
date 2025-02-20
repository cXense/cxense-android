package io.piano.android.cxense

/**
 * Base class for SDK exceptions
 *
 */
public open class BaseException : RuntimeException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * Exception that is thrown when `consent required` flag was set, but user doesn't provide consent
 */
public class ConsentRequiredException : BaseException {
    public constructor() : super("Required user consent wasn't provided.")
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * Exception that is thrown for HTTP 400 Bad Request responses
 *
 */
public class BadRequestException : BaseException {
    public constructor() : super("Request failed! Please make sure that all the request parameters are valid.")
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * Exception that is thrown for HTTP 403 Forbidden responses
 *
 */
public class ForbiddenException : BaseException {
    public constructor() : super(
        "Request failed! Please make sure that all the request parameters are valid and uses authorized values.",
    )

    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * Exception that is thrown for HTTP 401 Not Authorized responses
 *
 */
public class NotAuthorizedException : BaseException {
    public constructor() : super(
        "Request failed! Please make sure that all the request parameters are valid and uses authorized values.",
    )

    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}
