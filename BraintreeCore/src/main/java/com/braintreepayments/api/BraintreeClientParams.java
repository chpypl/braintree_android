package com.braintreepayments.api;

import android.content.Context;

class BraintreeClientParams {

    private Authorization authorization;
    private AnalyticsClient analyticsClient;
    private BraintreeHTTPClient httpClient;
    private Context context;

    private String sessionId;
    private String integrationType;
    private BraintreeGraphQLClient graphQLClient;

    private ConfigurationLoader configurationLoader;
    private BrowserSwitchClient browserSwitchClient;
    private ManifestValidator manifestValidator;

    Authorization getAuthorization() {
        return authorization;
    }

    BraintreeClientParams authorization(Authorization authorization) {
        this.authorization = authorization;
        return this;
    }

    AnalyticsClient getAnalyticsClient() {
        return analyticsClient;
    }

    BraintreeClientParams analyticsClient(AnalyticsClient analyticsClient) {
        this.analyticsClient = analyticsClient;
        return this;
    }

    BraintreeHTTPClient getHTTPClient() {
        return httpClient;
    }

    BraintreeClientParams httpClient(BraintreeHTTPClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    Context getContext() {
        return context;
    }

    BraintreeClientParams context(Context context) {
        this.context = context;
        return this;
    }

    String getSessionId() {
        return sessionId;
    }

    BraintreeClientParams sessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    BraintreeGraphQLClient getGraphQLClient() {
        return graphQLClient;
    }

    BraintreeClientParams graphQLClient(BraintreeGraphQLClient graphQLClient) {
        this.graphQLClient = graphQLClient;
        return this;
    }

    ConfigurationLoader getConfigurationLoader() {
        return configurationLoader;
    }

    BraintreeClientParams configurationLoader(ConfigurationLoader configurationLoader) {
        this.configurationLoader = configurationLoader;
        return this;
    }

    BrowserSwitchClient getBrowserSwitchClient() {
        return browserSwitchClient;
    }

    BraintreeClientParams browserSwitchClient(BrowserSwitchClient browserSwitchClient) {
        this.browserSwitchClient = browserSwitchClient;
        return this;
    }

    ManifestValidator getManifestValidator() {
        return manifestValidator;
    }

    BraintreeClientParams manifestValidator(ManifestValidator manifestValidator) {
        this.manifestValidator = manifestValidator;
        return this;
    }

    String getIntegrationType() {
        return integrationType;
    }

    BraintreeClientParams setIntegrationType(String integrationType) {
        this.integrationType = integrationType;
        return this;
    }
}
