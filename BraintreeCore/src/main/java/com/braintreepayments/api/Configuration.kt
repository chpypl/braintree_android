package com.braintreepayments.api

import androidx.annotation.RestrictTo
import org.json.JSONException
import org.json.JSONObject

// NEXT MAJOR VERSION: remove 'open' modifiers, Java classes/methods are open by default
// Ref: https://kotlin-quick-reference.com/102c-R-open-final-classes.html

/**
 * Contains the remote configuration for the Braintree Android SDK.
 *
 * @property assetsUrl The assets URL of the current environment.
 * @property clientApiUrl The url of the Braintree client API for the current environment.
 * @property environment The current environment.
 * @property merchantId the current Braintree merchant id.
 * @property isPayPalEnabled `true` if PayPal is enabled and supported in the current environment, `false` otherwise.
 * @property isThreeDSecureEnabled `true` if 3D Secure is enabled and supported for the current merchant account, * `false` otherwise.
 * @property merchantAccountId the current Braintree merchant account id.
 * @property cardinalAuthenticationJwt the JWT for Cardinal
 * @property isCvvChallengePresent `true` if cvv is required for card transactions, `false` otherwise.
 * @property isPostalCodeChallengePresent `true` if postal code is required for card transactions, `false` otherwise.
 * @property isFraudDataCollectionEnabled `true` if fraud device data collection should occur; `false` otherwise.
 * @property isVenmoEnabled `true` if Venmo is enabled for the merchant account; `false` otherwise.
 * @property venmoAccessToken the Access Token used by the Venmo app to tokenize on behalf of the merchant.
 * @property venmoMerchantId the Venmo merchant id used by the Venmo app to authorize payment.
 * @property venmoEnvironment the Venmo environment used to handle this payment.
 * @property isGraphQLEnabled  `true` if GraphQL is enabled for the merchant account; `false` otherwise.
 * @property isLocalPaymentEnabled `true` if Local Payment is enabled for the merchant account; `false` otherwise.
 * @property isKountEnabled `true` if Kount is enabled for the merchant account; `false` otherwise.
 * @property kountMerchantId the Kount merchant id set in the Gateway.
 * @property isUnionPayEnabled `true` if UnionPay is enabled for the merchant account; `false` otherwise.
 * @property payPalDisplayName the PayPal app display name.
 * @property payPalClientId the PayPal app client id.
 * @property payPalPrivacyUrl the PayPal app privacy url.
 * @property payPalUserAgreementUrl the PayPal app user agreement url.
 * @property payPalDirectBaseUrl the url for custom PayPal environments.
 * @property payPalEnvironment the current environment for PayPal.
 * @property isPayPalTouchDisabled `true` if PayPal touch is currently disabled, `false` otherwise.
 * @property payPalCurrencyIsoCode the PayPal currency code.
 * @property isGooglePayEnabled `true` if Google Payment is enabled and supported in the current environment; `false` otherwise.
 * @property googlePayAuthorizationFingerprint the authorization fingerprint to use for Google Payment, only allows tokenizing Google Payment cards.
 * @property googlePayEnvironment the current Google Pay environment.
 * @property googlePayDisplayName the Google Pay display name to show to the user.
 * @property googlePaySupportedNetworks a list of supported card networks for Google Pay.
 * @property googlePayPayPalClientId the PayPal Client ID used by Google Pay.
 * @property analyticsUrl [String] url of the Braintree analytics service.
 * @property isAnalyticsEnabled `true` if analytics are enabled, `false` otherwise.
 * @property isVisaCheckoutEnabled `true` if Visa Checkout is enabled for the merchant account; `false` otherwise.
 * @property visaCheckoutSupportedNetworks the Visa Checkout supported networks enabled for the merchant account.
 * @property visaCheckoutApiKey the Visa Checkout API key configured in the Braintree Control Panel.
 * @property visaCheckoutExternalClientId the Visa Checkout External Client ID configured in the Braintree Control Panel.
 * @property graphQLUrl the GraphQL url.
 * @property isSamsungPayEnabled `true` if Samsung Pay is enabled; `false` otherwise.
 * @property samsungPayMerchantDisplayName the merchant display name for Samsung Pay.
 * @property samsungPayServiceId the Samsung Pay service id associated with the merchant.
 * @property samsungPaySupportedCardBrands a list of card brands supported by Samsung Pay.
 * @property samsungPayAuthorization the authorization to use with Samsung Pay.
 * @property samsungPayEnvironment the Braintree environment Samsung Pay should interact with.
 * @property braintreeApiAccessToken The Access Token for Braintree API.
 * @property braintreeApiUrl the base url for accessing Braintree API.
 * @property isBraintreeApiEnabled a boolean indicating whether Braintree API is enabled for this merchant.
 * @property supportedCardTypes a list of card types supported by the merchant.
 */
open class Configuration internal constructor(configurationString: String?) {

    companion object {
        private const val ASSETS_URL_KEY = "assetsUrl"
        private const val CLIENT_API_URL_KEY = "clientApiUrl"
        private const val CHALLENGES_KEY = "challenges"
        private const val ENVIRONMENT_KEY = "environment"
        private const val MERCHANT_ID_KEY = "merchantId"
        private const val MERCHANT_ACCOUNT_ID_KEY = "merchantAccountId"
        private const val ANALYTICS_KEY = "analytics"
        private const val BRAINTREE_API_KEY = "braintreeApi"
        private const val PAYPAL_ENABLED_KEY = "paypalEnabled"
        private const val PAYPAL_KEY = "paypal"
        private const val KOUNT_KEY = "kount"
        private const val GOOGLE_PAY_KEY = "androidPay"
        private const val THREE_D_SECURE_ENABLED_KEY = "threeDSecureEnabled"
        private const val PAY_WITH_VENMO_KEY = "payWithVenmo"
        private const val UNIONPAY_KEY = "unionPay"
        private const val CARD_KEY = "creditCards"
        private const val VISA_CHECKOUT_KEY = "visaCheckout"
        private const val GRAPHQL_KEY = "graphQL"
        private const val SAMSUNG_PAY_KEY = "samsungPay"
        private const val CARDINAL_AUTHENTICATION_JWT = "cardinalAuthenticationJWT"

        @JvmStatic
        @Throws(JSONException::class)
        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun fromJson(configurationString: String?): Configuration {
            return Configuration(configurationString)
        }
    }

    open val assetsUrl: String
    open val cardinalAuthenticationJwt: String?
    open val clientApiUrl: String
    open val environment: String
    open val isPayPalEnabled: Boolean
    open val isThreeDSecureEnabled: Boolean
    open val merchantAccountId: String?
    open val merchantId: String

    private val analyticsConfiguration: AnalyticsConfiguration
    private val braintreeApiConfiguration: BraintreeApiConfiguration
    private val cardConfiguration: CardConfiguration
    private val challenges: MutableSet<String> = HashSet()
    private val configurationString: String
    private val googlePayConfiguration: GooglePayConfiguration
    private val graphQLConfiguration: GraphQLConfiguration
    private val kountConfiguration: KountConfiguration
    private val payPalConfiguration: PayPalConfiguration
    private val samsungPayConfiguration: SamsungPayConfiguration
    private val unionPayConfiguration: UnionPayConfiguration
    private val venmoConfiguration: VenmoConfiguration
    private val visaCheckoutConfiguration: VisaCheckoutConfiguration

    init {
        if (configurationString == null) {
            throw JSONException("Configuration cannot be null")
        }

        this.configurationString = configurationString
        val json = JSONObject(configurationString)
        assetsUrl = Json.optString(json, ASSETS_URL_KEY, "")
        clientApiUrl = json.getString(CLIENT_API_URL_KEY)

        // parse json challenges
        json.optJSONArray(CHALLENGES_KEY)?.let { challengesArray ->
            for (i in 0 until challengesArray.length()) {
                challenges.add(challengesArray.optString(i, ""))
            }
        }

        environment = json.getString(ENVIRONMENT_KEY)
        merchantId = json.getString(MERCHANT_ID_KEY)
        merchantAccountId = Json.optString(json, MERCHANT_ACCOUNT_ID_KEY, null)
        analyticsConfiguration = AnalyticsConfiguration.fromJson(json.optJSONObject(ANALYTICS_KEY))
        braintreeApiConfiguration =
            BraintreeApiConfiguration.fromJson(json.optJSONObject(BRAINTREE_API_KEY))
        cardConfiguration = CardConfiguration.fromJson(json.optJSONObject(CARD_KEY))
        isPayPalEnabled = json.optBoolean(PAYPAL_ENABLED_KEY, false)
        payPalConfiguration = PayPalConfiguration.fromJson(json.optJSONObject(PAYPAL_KEY))
        googlePayConfiguration = GooglePayConfiguration.fromJson(json.optJSONObject(GOOGLE_PAY_KEY))
        isThreeDSecureEnabled = json.optBoolean(THREE_D_SECURE_ENABLED_KEY, false)
        venmoConfiguration = VenmoConfiguration.fromJson(json.optJSONObject(PAY_WITH_VENMO_KEY))
        kountConfiguration = KountConfiguration.fromJson(json.optJSONObject(KOUNT_KEY))
        unionPayConfiguration = UnionPayConfiguration.fromJson(json.optJSONObject(UNIONPAY_KEY))
        visaCheckoutConfiguration =
            VisaCheckoutConfiguration.fromJson(json.optJSONObject(VISA_CHECKOUT_KEY))
        graphQLConfiguration = GraphQLConfiguration.fromJson(json.optJSONObject(GRAPHQL_KEY))
        samsungPayConfiguration =
            SamsungPayConfiguration.fromJson(json.optJSONObject(SAMSUNG_PAY_KEY))
        cardinalAuthenticationJwt = Json.optString(json, CARDINAL_AUTHENTICATION_JWT, null)
    }

    open val isCvvChallengePresent = challenges.contains("cvv")
    open val isPostalCodeChallengePresent = challenges.contains("postal_code")
    open val isVenmoEnabled = venmoConfiguration.isAccessTokenValid
    open val isLocalPaymentEnabled = isPayPalEnabled // Local Payments are enabled when PayPal is enabled
    open val isUnionPayEnabled = unionPayConfiguration.isEnabled
    open val payPalPrivacyUrl: String? = payPalConfiguration.privacyUrl
    open val payPalUserAgreementUrl: String? = payPalConfiguration.userAgreementUrl
    open val payPalDirectBaseUrl: String? = payPalConfiguration.directBaseUrl
    open val isGooglePayEnabled = googlePayConfiguration.isEnabled
    open val isVisaCheckoutEnabled = visaCheckoutConfiguration.isEnabled
    open val isSamsungPayEnabled = samsungPayConfiguration.isEnabled

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val isFraudDataCollectionEnabled = cardConfiguration.isFraudDataCollectionEnabled

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val venmoAccessToken = venmoConfiguration.accessToken

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val venmoMerchantId = venmoConfiguration.merchantId

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val venmoEnvironment = venmoConfiguration.environment

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val isGraphQLEnabled = graphQLConfiguration.isEnabled

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val isKountEnabled = kountConfiguration.isEnabled

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val kountMerchantId = kountConfiguration.kountMerchantId

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val payPalDisplayName = payPalConfiguration.displayName

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val payPalClientId = payPalConfiguration.clientId

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val payPalEnvironment = payPalConfiguration.environment

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val isPayPalTouchDisabled = payPalConfiguration.isTouchDisabled

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val payPalCurrencyIsoCode = payPalConfiguration.currencyIsoCode

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val googlePayAuthorizationFingerprint = googlePayConfiguration.googleAuthorizationFingerprint

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val googlePayEnvironment = googlePayConfiguration.environment

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val googlePayDisplayName = googlePayConfiguration.displayName

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val googlePaySupportedNetworks = googlePayConfiguration.supportedNetworks

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val googlePayPayPalClientId = googlePayConfiguration.paypalClientId

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val analyticsUrl = analyticsConfiguration.url

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val isAnalyticsEnabled = analyticsConfiguration.isEnabled

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val visaCheckoutSupportedNetworks = visaCheckoutConfiguration.acceptedCardBrands

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val visaCheckoutApiKey = visaCheckoutConfiguration.apiKey

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val visaCheckoutExternalClientId = visaCheckoutConfiguration.externalClientId

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val graphQLUrl = graphQLConfiguration.url

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val samsungPayMerchantDisplayName = samsungPayConfiguration.merchantDisplayName

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val samsungPayServiceId = samsungPayConfiguration.serviceId

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val samsungPaySupportedCardBrands = samsungPayConfiguration.supportedCardBrands.toList()

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val samsungPayAuthorization = samsungPayConfiguration.samsungAuthorization

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val samsungPayEnvironment = samsungPayConfiguration.environment

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val braintreeApiAccessToken = braintreeApiConfiguration.accessToken

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val braintreeApiUrl = braintreeApiConfiguration.url

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val isBraintreeApiEnabled = braintreeApiConfiguration.isEnabled

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val supportedCardTypes = cardConfiguration.supportedCardTypes

    /**
     * Check if a specific feature is enabled in the GraphQL API.
     *
     * @param feature The feature to check.
     * @return `true` if GraphQL is enabled and the feature is enabled, `false` otherwise.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun isGraphQLFeatureEnabled(feature: String) = graphQLConfiguration.isFeatureEnabled(feature)

    /**
     * @return Configuration as a json [String].
     */
    open fun toJson(): String {
        return configurationString
    }
}