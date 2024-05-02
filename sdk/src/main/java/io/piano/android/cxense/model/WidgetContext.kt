package io.piano.android.cxense.model

import com.squareup.moshi.JsonClass
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.util.Collections

/**
 * Provides information about the current context / page for Widget
 * @property url url for widget, required.
 * @property referrer referrer url.
 * @property pageclass the pageclass of the current page.
 * @property sentiment the sentiment of the current page.
 * @property recommending recs-recommending setting of the current page.
 * @property categories map for categories of the current page.
 * @property keywords list of keywords describing the context.
 * @property neighbors list of linked articles IDs.
 * @property parameters list of [ContextParameter] objects.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JsonClass(generateAdapter = true)
public class WidgetContext internal constructor(
    public val url: String,
    public val referrer: String?,
    public val pageclass: String?,
    public val sentiment: String?,
    public val recommending: Boolean?,
    public val categories: MutableMap<String, String>,
    public val keywords: MutableList<String>,
    public val neighbors: MutableList<String>,
    public val parameters: MutableList<ContextParameter>,
) {
    /**
     * @constructor Initialize builder for [WidgetContext]
     * @property url defines url for widget, required.
     * @property referrer defines referrer url.
     * @property pageclass the pageclass of the current page.
     * @property sentiment the sentiment of the current page.
     * @property recommending recs-recommending setting of the current page.
     * @property categories map for categories of the current page.
     * @property keywords list of keywords describing the context.
     * @property neighbors list of linked articles IDs.
     * @property parameters list of [ContextParameter] objects.
     */
    public data class Builder @JvmOverloads constructor(
        var url: String,
        var referrer: String? = null,
        var pageclass: String? = null,
        var sentiment: String? = null,
        var recommending: Boolean? = null,
        var categories: MutableMap<String, String> = mutableMapOf(),
        var keywords: MutableList<String> = mutableListOf(),
        var neighbors: MutableList<String> = mutableListOf(),
        var parameters: MutableList<ContextParameter> = mutableListOf(),
    ) {
        /**
         * Sets url
         * @param url url for widget.
         */
        public fun url(url: String): Builder = apply { this.url = url }

        /**
         * Sets referrer
         * @param referrer referrer url.
         */
        public fun referrer(referrer: String?): Builder = apply { this.referrer = referrer }

        /**
         * Sets pageclass
         * @param pageclass the pageclass of the current page.
         */
        public fun pageclass(pageclass: String?): Builder = apply { this.pageclass = pageclass }

        /**
         * Sets sentiment
         * @param sentiment the sentiment of the current page.
         */
        public fun sentiment(sentiment: String?): Builder = apply { this.sentiment = sentiment }

        /**
         * Sets recommending flag
         * @param recommending recs-recommending setting of the current page.
         */
        public fun recommending(recommending: Boolean?): Builder = apply { this.recommending = recommending }

        /**
         * Sets categories
         * @param categories map for categories of the current page.
         */
        public fun categories(categories: MutableMap<String, String>): Builder = apply { this.categories = categories }

        /**
         * Sets keywords
         * @param keywords list of keywords describing the context.
         */
        public fun keywords(keywords: MutableList<String>): Builder = apply { this.keywords = keywords }

        /**
         * Sets neighbors
         * @param neighbors list of linked articles IDs.
         */
        public fun neighbors(neighbors: MutableList<String>): Builder = apply { this.neighbors = neighbors }

        /**
         * Sets parameters
         * @param parameters list of [ContextParameter] objects.
         */
        public fun parameters(parameters: MutableList<ContextParameter>): Builder = apply {
            this.parameters = parameters
        }

        /**
         * Builds widget context
         * @throws [IllegalArgumentException] if constraints failed
         */
        public fun build(): WidgetContext {
            check(url.toHttpUrlOrNull() != null) {
                "You should provide valid url as source"
            }
            referrer?.let {
                check(it.toHttpUrlOrNull() != null) {
                    "You should provide valid url as referrer"
                }
            }
            return WidgetContext(
                url,
                referrer,
                pageclass,
                sentiment,
                recommending,
                Collections.unmodifiableMap(categories),
                Collections.unmodifiableList(keywords),
                Collections.unmodifiableList(neighbors),
                Collections.unmodifiableList(parameters)
            )
        }
    }
}
