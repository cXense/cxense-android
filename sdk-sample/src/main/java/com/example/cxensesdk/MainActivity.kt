package com.example.cxensesdk

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import io.piano.android.cxense.CredentialsProvider
import io.piano.android.cxense.CxenseSdk
import io.piano.android.cxense.ENDPOINT_USER_SEGMENTS
import io.piano.android.cxense.LoadCallback
import io.piano.android.cxense.MIN_DISPATCH_PERIOD
import io.piano.android.cxense.model.CustomParameter
import io.piano.android.cxense.model.PerformanceEvent
import io.piano.android.cxense.model.User
import io.piano.android.cxense.model.UserIdentity
import io.piano.android.cxense.model.UserSegmentRequest
import io.piano.android.cxense.model.WidgetContext
import io.piano.android.cxense.model.WidgetItem
import com.example.cxensesdk.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import io.piano.android.cxense.model.ExternalTypedItem
import io.piano.android.cxense.model.Segment
import io.piano.android.cxense.model.TypedItem
import io.piano.android.cxense.model.TypedSegmentsResponse
import io.piano.android.cxense.model.UserExternalTypedData
import timber.log.Timber
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding: ActivityMainBinding by viewBinding(R.id.recyclerview)

    private val animals = listOf(
        "alligator", "ant", "bear", "bee", "bird", "camel", "cat",
        "cheetah", "chicken", "chimpanzee", "cow", "crocodile", "deer", "dog", "dolphin", "duck",
        "eagle", "elephant", "fish", "fly", "fox", "frog", "giraffe", "goat", "goldfish", "hamster",
        "hippopotamus", "horse", "kangaroo", "kitten", "lion", "lobster", "monkey", "octopus", "owl",
        "panda", "pig", "puppy", "rabbit", "rat", "scorpion", "seal", "shark", "sheep", "snail",
        "snake", "spider", "squirrel", "tiger", "turtle", "wolf", "zebra"
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
            dispatchPeriod(MIN_DISPATCH_PERIOD, TimeUnit.MILLISECONDS)
            credentialsProvider = object : CredentialsProvider {
                override fun getUsername(): String = BuildConfig.USERNAME // load it from secured store

                override fun getApiKey(): String = BuildConfig.API_KEY // load it from secured store

                override fun getDmpPushPersistentId(): String = BuildConfig.PERSISTED_ID
            }
        }
    }

    override fun onPause() {
        CxenseSdk.getInstance().setDispatchEventsCallback(null)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        CxenseSdk.getInstance().setDispatchEventsCallback { statuses ->
            val grouped = statuses.groupBy { it.isSent }
            runOnUiThread {
                showText(
                    """
                    Sent: '${grouped[true]?.joinToString { it.eventId ?: ""}}'
                    Not sent: '${grouped[false]?.joinToString { it.eventId ?: ""}}'
                    """.trimIndent()
                )
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
        cxenseSdk.loadWidgetRecommendations(
            "w",
            WidgetContext.Builder("").build(),
            callback = object : LoadCallback<List<WidgetItem>> {
                override fun onSuccess(data: List<WidgetItem>) {
                    showText("TEXT")
                }

                override fun onError(throwable: Throwable) {
                    showError(throwable)
                }
            }
        )
        cxenseSdk.executePersistedQuery(
            ENDPOINT_USER_SEGMENTS,
            segmentsPersistentId,
            UserSegmentRequest(
                identities,
                listOf(BuildConfig.SITEGROUP_ID),
                format = UserSegmentRequest.ResponseFormat.CX_TYPED
            ),
            object : LoadCallback<TypedSegmentsResponse> {
                override fun onSuccess(data: TypedSegmentsResponse) {
                    showText(data.segments.joinToString(" ") { "{id=${it.id},type=${it.type}}" })
                }

                override fun onError(throwable: Throwable) {
                    showError(throwable)
                }
            }
        )
        cxenseSdk.getUserSegments(
            identities,
            listOf(BuildConfig.SITEGROUP_ID),
            callback = object : LoadCallback<List<Segment>> {
                override fun onSuccess(data: List<Segment>) {
                    showText(data.joinToString(" ") { "{id=${it.id},type=${it.type}}" })
                }

                override fun onError(throwable: Throwable) {
                    showError(throwable)
                }
            }
        )
        cxenseSdk.getUser(
            identity,
            callback = object : LoadCallback<User> {
                override fun onSuccess(data: User) {
                    showText(String.format(Locale.US, "User id = %s", data.id))
                }

                override fun onError(throwable: Throwable) {
                    showError(throwable)
                }
            }
        )

        // read external data for user
        cxenseSdk.getUserExternalTypedData(
            type,
            id,
            callback = object : LoadCallback<List<UserExternalTypedData>> {
                override fun onSuccess(data: List<UserExternalTypedData>) {
                    showText(String.format(Locale.US, "We have %d items", data.size))
                }

                override fun onError(throwable: Throwable) {
                    showError(throwable)
                }
            }
        )

        // delete external data for user
        cxenseSdk.deleteUserExternalData(
            identity,
            object : LoadCallback<Unit> {
                override fun onSuccess(data: Unit) {
                    showText("Success")
                }

                override fun onError(throwable: Throwable) {
                    showError(throwable)
                }
            }
        )

        // update external data for user
        val userExternalData = UserExternalTypedData.Builder(identity)
            .addExternalItems(
                ExternalTypedItem("gender", TypedItem.String("male")),
                ExternalTypedItem("interests", TypedItem.Number(2)),
                ExternalTypedItem("sports", TypedItem.Time(Date())),
                ExternalTypedItem("sports", TypedItem.Decimal(4.0))
            )
            .build()
        cxenseSdk.setUserExternalTypedData(
            userExternalData,
            object : LoadCallback<Unit> {
                override fun onSuccess(data: Unit) {
                    showText("Success")
                }

                override fun onError(throwable: Throwable) {
                    showError(throwable)
                }
            }
        )

        val builder = PerformanceEvent.Builder(BuildConfig.SITE_ID, "cxd-origin", "tap", mutableListOf(identity))
            .prnd(UUID.randomUUID().toString())
            .addCustomParameters(
                CustomParameter("cxd-interests", "TEST"),
                CustomParameter("cxd-test", "TEST")
            )
        cxenseSdk.pushEvents(builder.build(), builder.build())
    }
}
