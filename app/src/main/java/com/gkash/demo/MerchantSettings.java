package com.gkash.demo;

import android.content.Context;
import android.content.SharedPreferences;

// Merchant credentials and environment, persisted in SharedPreferences
public class MerchantSettings {

    private static final String PREFS_NAME = "merchant_settings";
    private static final String KEY_MERCHANT_ID = "merchant_id";
    private static final String KEY_SIGNATURE_KEY = "signature_key";
    private static final String KEY_PRODUCTION = "production";

    private final SharedPreferences prefs;

    public MerchantSettings(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getMerchantId() {
        return prefs.getString(KEY_MERCHANT_ID, "");
    }

    public String getSignatureKey() {
        return prefs.getString(KEY_SIGNATURE_KEY, "");
    }

    // False until the user saves credentials on the Settings page
    public boolean hasCredentials() {
        return !getMerchantId().isEmpty() && !getSignatureKey().isEmpty();
    }

    public boolean isProduction() {
        return prefs.getBoolean(KEY_PRODUCTION, false);
    }

    public void save(String merchantId, String signatureKey, boolean production) {
        prefs.edit()
                .putString(KEY_MERCHANT_ID, merchantId)
                .putString(KEY_SIGNATURE_KEY, signatureKey)
                .putBoolean(KEY_PRODUCTION, production)
                .apply();
    }
}
