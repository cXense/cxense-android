package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.piano.android.cxense.DependenciesProvider
import java.util.Collections
import java.util.Objects

/**
 * Conversion event description
 * @property eventType Predefined event type
 * @property siteId The Cxense site identifier to be associated with the events.
 * @property productId Product identifier.
 * @property funnelStep Funnel step.
 * @property identities List of known user identities to identify the user. Note that different users must be fed as different events.
 * @property price A price to override the original value in the conversion product object.
 * @property renewalFrequency A renewal frequency to override the original value in the conversion product object.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JsonClass(generateAdapter = true)
public class ConversionEvent internal constructor(
    @Json(name = "userIds") public val identities: List<UserIdentity>,
    @Json(name = "siteId") public val siteId: String,
    @Json(name = "consent") public val consentOptions: List<String>,
    @Json(name = "productId") public val productId: String,
    @Json(name = "funnelStep") public val funnelStep: String,
    @Json(name = "productPrice") public val price: Double?,
    @Json(name = "productRenewalFrequency") public val renewalFrequency: String?,
    @Json(name = "eventType") public val eventType: String = EVENT_TYPE,
) : Event(null) {
    override val mergeKey = Objects.hash(eventType, siteId, productId)

    /**
     * @constructor Initialize Builder with required parameters
     * @property siteId The Cxense site identifier to be associated with the events.
     * @property productId Product identifier.
     * @property funnelStep Funnel step. Can be one of the pre-defined [FUNNEL_TYPE_CONVERT_PRODUCT], [FUNNEL_TYPE_TERMINATE_PRODUCT] and [FUNNEL_TYPE_RENEW_PRODUCT] or alternatively any string representing the step e.g. 'creditCardDetails'.
     * @property identities List of known user identities to identify the user. Note that different users must be fed as different events.
     * @property productPrice A price to override the original value in the conversion product object.
     * @property renewalFrequency A renewal frequency to override the original value in the conversion product object.
     * The renewal frequency has the format "`<number><units><type>`". If the renewal frequency is set on the product, the system will automatically renew all the conversions to this product every `<number>` of `<units>` until the conversion is explicitly stopped, renewed or started over.
     * The `<number>` is limited to 3 digits. Only 'd' (days), 'w' (weeks), 'M' (months) and 'y' (years) are supported as `<units>`. The `<type>` can be one of 'R' (relative to the time the user has converted) or 'C' (calendar-based: happening at the beginning of the `<unit>`).
     * Examples: "`1yC`", "`28wR`" and so on.
     */
    public data class Builder @JvmOverloads constructor(
        var siteId: String,
        var productId: String,
        var funnelStep: String,
        val identities: MutableList<UserIdentity> = mutableListOf(),
        var productPrice: Double? = null,
        var renewalFrequency: String? = null,
    ) {

        /**
         * Adds known user identities to identify the user.
         * @param identities one or multiple [UserIdentity] objects.
         */
        public fun addIdentities(vararg identities: UserIdentity): Builder = apply {
            this.identities.addAll(identities)
        }

        /**
         * Adds known user identities to identify the user.
         * @param identities [Iterable] with [UserIdentity] objects.
         */
        public fun addIdentities(identities: Iterable<UserIdentity>): Builder = apply {
            this.identities.addAll(identities)
        }

        /**
         * Sets site identifier
         * @param siteId The Cxense site identifier to be associated with the events.
         */
        public fun siteId(siteId: String): Builder = apply { this.siteId = siteId }

        /**
         * Sets product identifier
         * @param productId Product identifier.
         */
        public fun productId(productId: String): Builder = apply { this.productId = productId }

        /**
         * Sets funnel step
         * @param funnelStep Funnel step. Can be one of the pre-defined [FUNNEL_TYPE_CONVERT_PRODUCT], [FUNNEL_TYPE_TERMINATE_PRODUCT] and [FUNNEL_TYPE_RENEW_PRODUCT] or alternatively any string representing the step e.g. 'creditCardDetails'.
         */
        public fun funnelStep(funnelStep: String): Builder = apply { this.funnelStep = funnelStep }

        /**
         * Sets product price
         * @param productPrice A price to override the original value in the conversion product object.
         */
        public fun productPrice(productPrice: Double?): Builder = apply { this.productPrice = productPrice }

        /**
         * Sets renewal frequency
         * @param renewalFrequency A renewal frequency to override the original value in the conversion product object.
         */
        public fun renewalFrequency(renewalFrequency: String?): Builder = apply {
            this.renewalFrequency = renewalFrequency
        }

        /**
         * Builds conversion event
         * @throws [IllegalArgumentException] if constraints failed
         */
        public fun build(): ConversionEvent {
            check(siteId.isNotEmpty()) {
                "Site id can't be empty"
            }
            check(productId.isNotEmpty()) {
                "Product id can't be empty"
            }
            check(productId.length <= MAX_LENGTH) {
                "Product id can't be longer than $MAX_LENGTH symbols"
            }
            check(funnelStep.isNotEmpty()) {
                "Funnel step can't be empty"
            }
            check(funnelStep.length <= MAX_LENGTH) {
                "Funnel step can't be longer than $MAX_LENGTH symbols"
            }
            renewalFrequency?.let {
                check(it.matches("^\\d{1,3}[dwMy][CR]$".toRegex())) {
                    """
                        The renewal frequency has the format "<number><units><type>". The <number> is limited to 3 digits. 
                        Only 'd' (days), 'w' (weeks), 'M' (months) and 'y' (years) are supported as <units>. 
                        The <type> can be one of 'R' (relative to the time the user has converted) or 'C' (calendar-based:
                         happening at the beginning of the <unit>).
                    """.trimIndent()
                }
            }

            return ConversionEvent(
                Collections.unmodifiableList(identities),
                siteId,
                DependenciesProvider.getInstance().cxenseConfiguration.consentSettings.consents,
                productId,
                funnelStep,
                productPrice,
                renewalFrequency
            )
        }
    }

    public companion object {
        public const val FUNNEL_TYPE_CONVERT_PRODUCT: String = "convertProduct"
        public const val FUNNEL_TYPE_TERMINATE_PRODUCT: String = "terminateProduct"
        public const val FUNNEL_TYPE_RENEW_PRODUCT: String = "renewProduct"
        internal const val EVENT_TYPE = "conversion"
        internal const val MAX_LENGTH = 30
    }
}
