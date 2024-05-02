package io.piano.android.cxense.model

import com.squareup.moshi.JsonClass
import io.piano.android.cxense.DoubleString
import io.piano.android.cxense.IntString
import java.util.Date

public sealed class TypedItem {
    @JsonClass(generateAdapter = true)
    public class String(public val value: kotlin.String) : TypedItem()

    @JsonClass(generateAdapter = true)
    public class Number(@IntString public val value: Int) : TypedItem() {
        init {
            require(value >= 0)
        }
    }

    @JsonClass(generateAdapter = true)
    public class Time(public val value: Date) : TypedItem()

    @JsonClass(generateAdapter = true)
    public class Decimal(@DoubleString public val value: Double) : TypedItem() {
        init {
            require(value >= 0 && value * 100 <= Int.MAX_VALUE)
        }
    }

    public object Unknown : TypedItem()
}
