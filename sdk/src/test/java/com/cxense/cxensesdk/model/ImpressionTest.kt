package com.cxense.cxensesdk.model

import com.cxense.cxensesdk.assertFailsWithMessage
import kotlin.test.Test

class ImpressionTest {
    @Test
    fun createValid() {
        Impression("url", 5)
    }

    @Test
    fun createWithEmptyUrl() {
        assertFailsWithMessage<IllegalArgumentException>("clickUrl", "Expected fail for clickUrl") {
            Impression("", 5)
        }
    }

    @Test
    fun createWithNegativeSeconds() {
        assertFailsWithMessage<IllegalArgumentException>("Seconds", "Expected fail for seconds") {
            Impression("url", -5)
        }
    }
}
