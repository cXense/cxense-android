package com.example.cxensesdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.cxensesdk.databinding.ActivityAnimalBinding
import com.google.android.material.snackbar.Snackbar
import io.piano.android.cxense.CxenseSdk
import io.piano.android.cxense.model.ConversionEvent
import io.piano.android.cxense.model.CustomParameter
import io.piano.android.cxense.model.Impression
import io.piano.android.cxense.model.PageViewEvent
import io.piano.android.cxense.model.PerformanceEvent
import io.piano.android.cxense.model.UserIdentity
import io.piano.android.cxense.model.WidgetContext
import kotlinx.coroutines.launch
import timber.log.Timber

class AnimalActivity : AppCompatActivity(R.layout.activity_animal) {
    private val binding: ActivityAnimalBinding by viewBinding(R.id.animalText)
    private lateinit var item: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = intent.getStringExtra(ITEM_KEY) ?: ""
        binding.animalText.text = getString(R.string.item_text, item)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                CxenseSdk.getInstance().eventStatuses.collect { statuses ->
                    val grouped = statuses.groupBy { it.isSent }
                    val message =
                        """
                        Sent: '${grouped[true]?.joinToString { it.eventId ?: "" }}'
                        Not sent: '${grouped[false]?.joinToString { it.eventId ?: "" }}'
                        """.trimIndent()
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onPause() {
        CxenseSdk.getInstance().trackActiveTime(item)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        CxenseSdk.getInstance().pushEvents(
            PageViewEvent.Builder(BuildConfig.SITE_ID)
                .contentId(item)
                .eventId(item)
                .addCustomParameters(CustomParameter("cxd-item", item))
                .build(),
            PerformanceEvent.Builder(BuildConfig.SITE_ID, "cxd-app", "view")
                .addIdentities(UserIdentity("cxd", "value"))
                .eventId(item)
                .addCustomParameters(CustomParameter("xyz-item", item))
                .build(),
            ConversionEvent.Builder(
                BuildConfig.SITE_ID,
                "0ab24abee9a85d869b29f46c837144",
                ConversionEvent.FUNNEL_TYPE_CONVERT_PRODUCT,
                mutableListOf(UserIdentity("cxd", "123456")),
            )
                .productPrice(12.25)
                .renewalFrequency("1wC")
                .build(),
        )
        lifecycleScope.launch {
            CxenseSdk.getInstance().loadWidgetRecommendations(
                "ffb1d2523b582f5f649df351d37928d2c108e715",
                WidgetContext.Builder("https://cxense.com").build(),
            ).onSuccess { data ->
                val impressions = data.mapIndexed { index, item ->
                    Impression(item.clickUrl ?: "", index + 1)
                }
                if (impressions.isNotEmpty()) {
                    CxenseSdk.getInstance().reportWidgetVisibilities(*impressions.toTypedArray())
                        .onSuccess { Timber.d("Success") }
                        .onFailure { Timber.e(it) }
                } else {
                    Timber.d("Success")
                }
            }.onFailure { throwable ->
                Timber.e(throwable)
            }
        }
    }

    companion object {
        const val ITEM_KEY = "item"
    }
}
