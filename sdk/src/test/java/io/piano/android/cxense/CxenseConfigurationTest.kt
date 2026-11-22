package io.piano.android.cxense

import io.piano.android.cxense.CxenseConfiguration.Companion.MIN_DISPATCH_PERIOD
import io.piano.android.cxense.CxenseConfiguration.Companion.MIN_OUTDATE_PERIOD
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class CxenseConfigurationTest {
    private lateinit var configuration: CxenseConfiguration

    @BeforeTest
    fun setUp() {
        configuration = CxenseConfiguration()
    }

    @Test
    fun setDifferentDispatchPeriod() = runTest {
        val values = mutableListOf<Duration>()
        val job = launch {
            configuration.dispatchPeriodFlow.collect {
                values.add(it)
            }
        }
        kotlinx.coroutines.yield()

        configuration.dispatchPeriod = configuration.dispatchPeriod * 2
        kotlinx.coroutines.yield()

        assertEquals(2, values.size)
        assertEquals(CxenseConfiguration.DEFAULT_DISPATCH_PERIOD, values[0])
        assertEquals(CxenseConfiguration.DEFAULT_DISPATCH_PERIOD * 2, values[1])

        job.cancel()
    }

    @Test
    fun setSameDispatchPeriod() = runTest {
        val values = mutableListOf<Duration>()
        val job = launch {
            configuration.dispatchPeriodFlow.collect {
                values.add(it)
            }
        }
        kotlinx.coroutines.yield()

        val period = configuration.dispatchPeriod
        configuration.dispatchPeriod = period
        kotlinx.coroutines.yield()

        assertEquals(1, values.size)
        assertEquals(period, values[0])

        job.cancel()
    }

    @Test
    fun setInvalidDispatchPeriod() {
        assertFailsWithMessage<IllegalArgumentException>(
            "greater than $MIN_DISPATCH_PERIOD",
            "Expected fail for dispatch period",
        ) {
            configuration.dispatchPeriod = MIN_DISPATCH_PERIOD - 1.milliseconds
        }
        assertEquals(MIN_DISPATCH_PERIOD, CxenseConfiguration.MIN_DISPATCH_PERIOD)
    }

    @Test
    fun setInvalidOutdatePeriod() {
        assertFailsWithMessage<IllegalArgumentException>(
            "greater than $MIN_OUTDATE_PERIOD",
            "Expected fail for outdate period",
        ) {
            configuration.outdatePeriod = MIN_OUTDATE_PERIOD - 1.milliseconds
        }
    }
}
