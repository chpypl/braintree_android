package com.braintreepayments.api;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import static com.braintreepayments.api.Assertions.assertIsANonce;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ThreeDSecureClientUnitTest {

    private FragmentActivity activity;
    private ThreeDSecureV1BrowserSwitchHelper browserSwitchHelper;

    private ThreeDSecureResultCallback threeDSecureResultCallback;

    private Configuration threeDSecureEnabledConfig;

    ThreeDSecureRequest basicRequest;

    @Before
    public void beforeEach() {
        activity = mock(FragmentActivity.class);
        threeDSecureResultCallback = mock(ThreeDSecureResultCallback.class);
        browserSwitchHelper = mock(ThreeDSecureV1BrowserSwitchHelper.class);

        threeDSecureEnabledConfig = new TestConfigurationBuilder()
                .threeDSecureEnabled(true)
                .cardinalAuthenticationJWT("cardinal-jwt")
                .buildConfiguration();

        basicRequest = new ThreeDSecureRequest()
                .nonce("a-nonce")
                .amount("amount")
                .billingAddress(new ThreeDSecurePostalAddress()
                        .givenName("billing-given-name"));
    }

    @Test
    public void performVerification_sendsAnalyticEvent() {
        CardinalClient cardinalClient = new MockCardinalClientBuilder()
                .successReferenceId("sample-session-id")
                .build();

        BraintreeClient braintreeClient = new MockBraintreeClientBuilder()
                .configuration(threeDSecureEnabledConfig)
                .build();
        when(braintreeClient.canPerformBrowserSwitch(activity, BraintreeRequestCodes.THREE_D_SECURE)).thenReturn(true);

        ThreeDSecureClient sut = new ThreeDSecureClient(braintreeClient, "sample-scheme", cardinalClient, browserSwitchHelper);
        sut.performVerification(activity, basicRequest, threeDSecureResultCallback);

        verify(braintreeClient).sendAnalyticsEvent("three-d-secure.initialized");
    }

    @Test
    public void performVerification_sendsParamsInLookupRequest() throws JSONException {
        CardinalClient cardinalClient = new MockCardinalClientBuilder()
                .successReferenceId("df-reference-id")
                .build();

        BraintreeClient braintreeClient = new MockBraintreeClientBuilder()
                .configuration(threeDSecureEnabledConfig)
                .build();
        when(braintreeClient.canPerformBrowserSwitch(activity, BraintreeRequestCodes.THREE_D_SECURE)).thenReturn(true);

        ThreeDSecureRequest request = new ThreeDSecureRequest()
                .nonce("a-nonce")
                .versionRequested(ThreeDSecureRequest.VERSION_2)
                .amount("amount")
                .billingAddress(new ThreeDSecurePostalAddress()
                        .givenName("billing-given-name"));

        ThreeDSecureClient sut = new ThreeDSecureClient(braintreeClient, "sample-scheme", cardinalClient, browserSwitchHelper);
        sut.performVerification(activity, request, threeDSecureResultCallback);

        String expectedUrl = "/v1/payment_methods/a-nonce/three_d_secure/lookup";
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(braintreeClient).sendPOST(eq(expectedUrl), bodyCaptor.capture(), any(HttpResponseCallback.class));

        JSONObject body = new JSONObject(bodyCaptor.getValue());
        assertEquals("amount", body.getString("amount"));
        assertEquals("df-reference-id", body.getString("df_reference_id"));
        assertEquals("billing-given-name", body.getJSONObject("additional_info").getString("billing_given_name"));
    }

    @Test
    public void performVerification_performsLookup_WhenCardinalSDKInitFails() {
        CardinalClient cardinalClient = new MockCardinalClientBuilder()
                .error(new Exception("error"))
                .build();

        BraintreeClient braintreeClient = new MockBraintreeClientBuilder()
                .configuration(threeDSecureEnabledConfig)
                .build();
        when(braintreeClient.canPerformBrowserSwitch(activity, BraintreeRequestCodes.THREE_D_SECURE)).thenReturn(true);

        ThreeDSecureRequest request = new ThreeDSecureRequest()
                .nonce("a-nonce")
                .versionRequested(ThreeDSecureRequest.VERSION_2)
                .amount("amount")
                .billingAddress(new ThreeDSecurePostalAddress()
                        .givenName("billing-given-name"));

        ThreeDSecureClient sut = new ThreeDSecureClient(braintreeClient, "sample-scheme", cardinalClient, browserSwitchHelper);
        sut.performVerification(activity, request, threeDSecureResultCallback);

        // TODO: consider refining this assertion to be more precise than the original
        verify(braintreeClient).sendPOST(anyString(), anyString(), any(HttpResponseCallback.class));
    }
    @Test
    public void performVerification_callsLookupListener() {
        CardinalClient cardinalClient = new MockCardinalClientBuilder()
                .successReferenceId("sample-session-id")
                .build();

        BraintreeClient braintreeClient = new MockBraintreeClientBuilder()
                .configuration(threeDSecureEnabledConfig)
                .sendPOSTSuccessfulResponse(Fixtures.THREE_D_SECURE_LOOKUP_RESPONSE)
                .build();
        when(braintreeClient.canPerformBrowserSwitch(activity, BraintreeRequestCodes.THREE_D_SECURE)).thenReturn(true);

        when(browserSwitchHelper.getUrl(anyString(), anyString(), any(ThreeDSecureRequest.class), any(ThreeDSecureLookup.class))).thenReturn("https://example.com");

        ThreeDSecureRequest request = new ThreeDSecureRequest()
                .nonce("a-nonce")
                .versionRequested(ThreeDSecureRequest.VERSION_2)
                .amount("amount")
                .billingAddress(new ThreeDSecurePostalAddress()
                        .givenName("billing-given-name"));

        ThreeDSecureClient sut = new ThreeDSecureClient(braintreeClient, "sample-scheme", cardinalClient, browserSwitchHelper);

        ThreeDSecureLookupCallback lookupListener = mock(ThreeDSecureLookupCallback.class);
        sut.performVerification(activity, request, lookupListener);

        verify(lookupListener).onResult(same(request), any(ThreeDSecureLookup.class), any(Exception.class));
    }

    @Test
    public void performVerification_withInvalidRequest_postsException() {
        CardinalClient cardinalClient = new MockCardinalClientBuilder().build();

        BraintreeClient braintreeClient = new MockBraintreeClientBuilder().build();
        when(braintreeClient.canPerformBrowserSwitch(activity, BraintreeRequestCodes.THREE_D_SECURE)).thenReturn(true);

        ThreeDSecureClient sut = new ThreeDSecureClient(braintreeClient, "sample-scheme", cardinalClient, browserSwitchHelper);

        ThreeDSecureRequest request = new ThreeDSecureRequest().amount("5");
        sut.performVerification(activity, request, threeDSecureResultCallback);

        ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
        verify(threeDSecureResultCallback).onResult((CardNonce) isNull(), captor.capture());
        assertEquals("The ThreeDSecureRequest nonce and amount cannot be null",
                captor.getValue().getMessage());
    }

    @Test
    public void performVerification_whenBrowserSwitchNotSetup_postsException() {
        CardinalClient cardinalClient = new MockCardinalClientBuilder().build();

        BraintreeClient braintreeClient = new MockBraintreeClientBuilder()
                .configuration(threeDSecureEnabledConfig)
                .build();
        when(braintreeClient.canPerformBrowserSwitch(activity, BraintreeRequestCodes.THREE_D_SECURE)).thenReturn(false);

        ThreeDSecureClient sut = new ThreeDSecureClient(braintreeClient, "sample-scheme", cardinalClient, browserSwitchHelper);
        sut.performVerification(activity, basicRequest, threeDSecureResultCallback);

        ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
        verify(threeDSecureResultCallback).onResult((CardNonce) isNull(), captor.capture());

        assertEquals("BraintreeBrowserSwitchActivity missing, " +
                "incorrectly configured in AndroidManifest.xml or another app defines the same browser " +
                "switch url as this app. See " +
                "https://developers.braintreepayments.com/guides/client-sdk/android/v2#browser-switch " +
                "for the correct configuration", captor.getValue().getMessage());
    }

    @Test
    public void performVerification_whenBrowserSwitchNotSetup_sendsAnalyticEvent() {
        CardinalClient cardinalClient = new MockCardinalClientBuilder().build();

        BraintreeClient braintreeClient = new MockBraintreeClientBuilder()
                .configuration(threeDSecureEnabledConfig)
                .build();
        when(braintreeClient.canPerformBrowserSwitch(activity, BraintreeRequestCodes.THREE_D_SECURE)).thenReturn(false);

        ThreeDSecureClient sut = new ThreeDSecureClient(braintreeClient, "sample-scheme", cardinalClient, browserSwitchHelper);
        sut.performVerification(activity, basicRequest, threeDSecureResultCallback);

        verify(braintreeClient).sendAnalyticsEvent("three-d-secure.invalid-manifest");
    }

    @Test
    public void onActivityResult_whenResultNotOk_doesNothing() {
        CardinalClient cardinalClient = new MockCardinalClientBuilder().build();
        BraintreeClient braintreeClient = new MockBraintreeClientBuilder().build();

        ThreeDSecureClient sut = new ThreeDSecureClient(braintreeClient, "sample-scheme", cardinalClient, browserSwitchHelper);

        verifyNoMoreInteractions(braintreeClient);
        sut.onActivityResult(AppCompatActivity.RESULT_CANCELED, new Intent(), threeDSecureResultCallback);
        verifyNoMoreInteractions(braintreeClient);
    }

    @Test
    public void onBrowserSwitchResult_whenSuccessful_postsPayment() {
        CardinalClient cardinalClient = new MockCardinalClientBuilder().build();
        BraintreeClient braintreeClient = new MockBraintreeClientBuilder().build();

        Uri uri = Uri.parse("http://demo-app.com")
                .buildUpon()
                .appendQueryParameter("auth_response", Fixtures.THREE_D_SECURE_AUTHENTICATION_RESPONSE)
                .build();

        BrowserSwitchResult browserSwitchResult =
            new BrowserSwitchResult(BrowserSwitchResult.STATUS_OK, null);

        ThreeDSecureClient sut = new ThreeDSecureClient(braintreeClient, "sample-scheme", cardinalClient, browserSwitchHelper);
        sut.onBrowserSwitchResult(browserSwitchResult, uri, threeDSecureResultCallback);

        ArgumentCaptor<CardNonce> captor = ArgumentCaptor.forClass(CardNonce.class);
        verify(threeDSecureResultCallback).onResult(captor.capture(), (Exception) isNull());

        CardNonce cardNonce = captor.getValue();
        assertIsANonce(cardNonce.getNonce());
        assertEquals("11", cardNonce.getLastTwo());
        assertTrue(cardNonce.getThreeDSecureInfo().wasVerified());
    }

    @Test
    public void onBrowserSwitchResult_whenSuccessful_sendAnalyticsEvents() {
        CardinalClient cardinalClient = new MockCardinalClientBuilder().build();
        BraintreeClient braintreeClient = new MockBraintreeClientBuilder().build();

        Uri uri = Uri.parse("http://demo-app.com")
                .buildUpon()
                .appendQueryParameter("auth_response", Fixtures.THREE_D_SECURE_AUTHENTICATION_RESPONSE)
                .build();

        BrowserSwitchResult browserSwitchResult =
            new BrowserSwitchResult(BrowserSwitchResult.STATUS_OK, null);

        ThreeDSecureClient sut = new ThreeDSecureClient(braintreeClient, "sample-scheme", cardinalClient, browserSwitchHelper);
        sut.onBrowserSwitchResult(browserSwitchResult, uri, threeDSecureResultCallback);

        verify(braintreeClient).sendAnalyticsEvent("three-d-secure.verification-flow.liability-shifted.true");
        verify(braintreeClient).sendAnalyticsEvent("three-d-secure.verification-flow.liability-shift-possible.true");
    }

    @Test
    public void onBrowserSwitchResult_whenFailure_postsErrorWithResponse() throws Exception {
        CardinalClient cardinalClient = new MockCardinalClientBuilder().build();
        BraintreeClient braintreeClient = new MockBraintreeClientBuilder().build();

        JSONObject json = new JSONObject();
        json.put("success", false);

        JSONObject errorJson = new JSONObject();
        errorJson.put("message", "Failed to authenticate, please try a different form of payment.");
        json.put("error", errorJson);

        Uri uri = Uri.parse("https://.com?auth_response=" + json.toString());

        BrowserSwitchResult browserSwitchResult =
            new BrowserSwitchResult(BrowserSwitchResult.STATUS_OK, null);

        ThreeDSecureClient sut = new ThreeDSecureClient(braintreeClient, "sample-scheme", cardinalClient, browserSwitchHelper);
        sut.onBrowserSwitchResult(browserSwitchResult, uri, threeDSecureResultCallback);

        ArgumentCaptor<ErrorWithResponse> captor = ArgumentCaptor.forClass(ErrorWithResponse.class);
        verify(threeDSecureResultCallback).onResult((CardNonce) isNull(), captor.capture());

        ErrorWithResponse error = captor.getValue();
        assertEquals(422, error.getStatusCode());
        assertEquals("Failed to authenticate, please try a different form of payment.", error.getMessage());
    }
}