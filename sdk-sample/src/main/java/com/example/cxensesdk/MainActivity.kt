package com.example.cxensesdk

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.cxensesdk.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import io.piano.android.cxense.CredentialsProvider
import io.piano.android.cxense.CxenseConfiguration
import io.piano.android.cxense.CxenseSdk
import io.piano.android.cxense.ENDPOINT_USER_SEGMENTS
import io.piano.android.cxense.model.CustomParameter
import io.piano.android.cxense.model.ExternalTypedItem
import io.piano.android.cxense.model.PerformanceEvent
import io.piano.android.cxense.model.TypedItem
import io.piano.android.cxense.model.TypedSegmentsResponse
import io.piano.android.cxense.model.UserExternalTypedData
import io.piano.android.cxense.model.UserIdentity
import io.piano.android.cxense.model.UserSegmentRequest
import io.piano.android.cxense.model.WidgetContext
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import java.util.Locale
import java.util.UUID

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding: ActivityMainBinding by viewBinding(R.id.recyclerview)

    private val animals = listOf(
        "alligator", "ant", "bear", "bee", "bird", "camel", "cat",
        "cheetah", "chicken", "chimpanzee", "cow", "crocodile", "deer", "dog", "dolphin", "duck",
        "eagle", "elephant", "fish", "fly", "fox", "frog", "giraffe", "goat", "goldfish", "hamster",
        "hippopotamus", "horse", "kangaroo", "kitten", "lion", "lobster", "monkey", "octopus", "owl",
        "panda", "pig", "puppy", "rabbit", "rat", "scorpion", "seal", "shark", "sheep", "snail",
        "snake", "spider", "squirrel", "tiger", "turtle", "wolf", "zebra",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MainAdapter(animals, this@MainActivity::onItemClick)
        }

        CxenseSdk.getInstance().configuration.apply {
            consentSettings
                .consentRequired(true)
                .pvAllowed(true)
                .deviceAllowed(true)
            consentSettings.version = 2
            dispatchPeriod = CxenseConfiguration.MIN_DISPATCH_PERIOD
            credentialsProvider = object : CredentialsProvider {
                override fun getUsername(): String = BuildConfig.USERNAME // load it from secured store

                override fun getApiKey(): String = BuildConfig.API_KEY // load it from secured store

                override fun getDmpPushPersistentId(): String = BuildConfig.PERSISTED_ID
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                CxenseSdk.getInstance().eventStatuses.collect { statuses ->
                    val grouped = statuses.groupBy { it.isSent }
                    showText(
                        """
                        Sent: '${grouped[true]?.joinToString { it.eventId ?: "" }}'
                        Not sent: '${grouped[false]?.joinToString { it.eventId ?: "" }}'
                        """.trimIndent(),
                    )
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.run -> {
                runMethods()
                true
            }
            R.id.flush -> {
                CxenseSdk.getInstance().flushEventQueue()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onItemClick(item: String) {
        Intent(this, AnimalActivity::class.java)
            .putExtra(AnimalActivity.ITEM_KEY, item)
            .also { startActivity(it) }
    }

    private fun showText(str: String) {
        Snackbar.make(binding.root, str, Snackbar.LENGTH_LONG).show()
    }

    private fun showError(t: Throwable) {
        Timber.e(t)
        showText(t.message ?: "")
    }

    private fun runMethods() {
        val id = "some_user_id"
        val type = "cxd"
        val segmentsPersistentId = "some_persistemt_id"
        val cxenseSdk = CxenseSdk.getInstance()
        val identity = UserIdentity(type, id)
        val identities = listOf(identity)
        lifecycleScope.launch {
            cxenseSdk.loadWidgetRecommendations(
                "w",
                WidgetContext.Builder("").build(),
            ).onSuccess {
                showText("TEXT")
            }.onFailure {
                showError(it)
            }

            cxenseSdk.executePersistedQuery<TypedSegmentsResponse>(
                ENDPOINT_USER_SEGMENTS,
                segmentsPersistentId,
                UserSegmentRequest(
                    identities,
                    listOf(BuildConfig.SITEGROUP_ID),
                    format = UserSegmentRequest.ResponseFormat.CX_TYPED,
                ),
            ).onSuccess { data ->
                showText(data.segments.joinToString(" ") { "{id=${it.id},type=${it.type}}" })
            }.onFailure {
                showError(it)
            }

            cxenseSdk.getUserSegments(
                identities,
                listOf(BuildConfig.SITEGROUP_ID),
            ).onSuccess { data ->
                showText(data.joinToString(" ") { "{id=${it.id},type=${it.type}}" })
            }.onFailure {
                showError(it)
            }

            cxenseSdk.getUser(identity).onSuccess { data ->
                showText(String.format(Locale.US, "User id = %s", data.id))
            }.onFailure {
                showError(it)
            }

            // read external data for user
            cxenseSdk.getUserExternalTypedData(type, id).onSuccess { data ->
                showText(String.format(Locale.US, "We have %d items", data.size))
            }.onFailure {
                showError(it)
            }

            // delete external data for user
            cxenseSdk.deleteUserExternalData(identity).onSuccess {
                showText("Success")
            }.onFailure {
                showError(it)
            }

            // update external data for user
            val userExternalData = UserExternalTypedData.Builder(identity)
                .addExternalItems(
                    ExternalTypedItem("gender", TypedItem.String("male")),
                    ExternalTypedItem("interests", TypedItem.Number(2)),
                    ExternalTypedItem("sports", TypedItem.Time(Date())),
                    ExternalTypedItem("sports", TypedItem.Decimal(4.0)),
                )
                .build()
            cxenseSdk.setUserExternalTypedData(userExternalData).onSuccess {
                showText("Success")
            }.onFailure {
                showError(it)
            }

            val builder = PerformanceEvent.Builder(BuildConfig.SITE_ID, "cxd-origin", "tap", mutableListOf(identity))
                .prnd(UUID.randomUUID().toString())
                .addCustomParameters(
                    CustomParameter("cxd-interests", "TEST"),
                    CustomParameter("cxd-test", "TEST"),
                )
            cxenseSdk.pushEvents(builder.build(), builder.build())
        }
    }
}
