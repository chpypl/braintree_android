package com.braintreepayments.api;

import com.visa.checkout.VisaPaymentSummary;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static junit.framework.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.powermock.*", "org.mockito.*", "org.robolectric.*", "android.*", "androidx.*"})
@PrepareForTest({ VisaPaymentSummary.class })
public class VisaCheckoutAccountUnitTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private VisaPaymentSummary visaPaymentSummary;

    @Before
    public void beforeEach() throws Exception {
        visaPaymentSummary = PowerMockito.mock(VisaPaymentSummary.class);
        PowerMockito.whenNew(VisaPaymentSummary.class).withAnyArguments().thenReturn(visaPaymentSummary);
    }

    @Test
    public void build_withNullVisaPaymentSummary_buildsEmptyPaymentMethod() throws JSONException {
        JSONObject expectedBase = new JSONObject("{\"visaCheckoutCard\":{}}");

        VisaCheckoutAccount visaCheckoutAccount = new VisaCheckoutAccount(null);
        JSONObject json = visaCheckoutAccount.buildTokenizationJSON();

        JSONAssert.assertEquals(expectedBase, json, JSONCompareMode.STRICT);
    }

    @Test
    public void build_withVisaPaymentSummary_buildsExpectedPaymentMethod() throws JSONException {
        when(visaPaymentSummary.getCallId()).thenReturn("stubbedCallId");
        when(visaPaymentSummary.getEncKey()).thenReturn("stubbedEncKey");
        when(visaPaymentSummary.getEncPaymentData()).thenReturn("stubbedEncPaymentData");

        VisaCheckoutAccount visaCheckoutAccount = new VisaCheckoutAccount(visaPaymentSummary);
        JSONObject json = visaCheckoutAccount.buildTokenizationJSON();

        JSONObject expectedBase = new JSONObject();
        JSONObject expectedPaymentMethodNonce = new JSONObject();
        expectedPaymentMethodNonce.put("callId", "stubbedCallId");
        expectedPaymentMethodNonce.put("encryptedKey", "stubbedEncKey");
        expectedPaymentMethodNonce.put("encryptedPaymentData", "stubbedEncPaymentData");
        expectedBase.put("visaCheckoutCard", expectedPaymentMethodNonce);

        JSONAssert.assertEquals(expectedBase, json, JSONCompareMode.STRICT);
    }

    @Test
    public void getApiPath_returnsCorrectApiPath() {
        assertEquals("visa_checkout_cards", new VisaCheckoutAccount(null).getApiPath());
    }

    @Test
    public void getResponsePaymentMethodType_returnsCorrectPaymentMethodType() {
        assertEquals(VisaCheckoutNonce.TYPE,
                new VisaCheckoutAccount(null).getResponsePaymentMethodType());
    }
}
