package com.gkash.gkashandroidsdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GkashPayment {
    private static volatile GkashPayment INSTANCE;
    private String hostUrl = "https://api-staging.pay.asia/";
    private TransStatusCallback transStatusCallback;

    private List<String> _walletScheme = Arrays.asList("apaylater://app.apaylater.com", "stage-onlinepayment.boostorium.com", "uat.shopee.com.my/universal-link/");
    private final List<String> _prodWalletScheme = Arrays.asList("apaylater://app.apaylater.com", "boost-my.com", "shopeemy://");

    public static GkashPayment getInstance() {
        if (INSTANCE == null) {
            synchronized (GkashPayment.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GkashPayment();
                }
            }
        }
        return INSTANCE;
    }

    public List<String> get_walletScheme() {
        return _walletScheme;
    }

    public void setProductionEnvironment(boolean isProduction) {
        if (isProduction) {
            hostUrl = "https://api.gkash.my/";
            _walletScheme = _prodWalletScheme;
        }
    }

    public TransStatusCallback getTransStatusCallback() {
        return transStatusCallback;
    }

    public void startPayment(Activity activity, PaymentRequest request) throws IllegalArgumentException {
        if (request == null) throw new IllegalArgumentException("PaymentRequest object is required.");

        // Validate required parameters
        if (request.getVersion() == null || request.getVersion().isEmpty()) throw new IllegalArgumentException("Parameter version is required.");
        if (request.getCid() == null || request.getCid().isEmpty()) throw new IllegalArgumentException("Parameter cid is required.");
        if (request.getCurrency() == null || request.getCurrency().isEmpty()) throw new IllegalArgumentException("Parameter currency is required.");
        if (request.getAmount() == null) throw new IllegalArgumentException("Parameter amount is required.");
        if (request.getCartId() == null || request.getCartId().isEmpty()) throw new IllegalArgumentException("Parameter cartId is required.");
        if (request.getSignatureKey() == null || request.getSignatureKey().isEmpty()) throw new IllegalArgumentException("Parameter signatureKey is required.");
        if (request.getTransStatusCallback() == null) throw new IllegalArgumentException("Parameter transStatusCallback is required.");

        this.transStatusCallback = request.getTransStatusCallback();

        String defaultReturnUrl;
        if (request.getReturnUrl() == null || request.getReturnUrl().equals("")) {
            defaultReturnUrl = "gkash://returntoapp";
        } else {
            defaultReturnUrl = request.getReturnUrl();
        }

        // Intent to Payment WebView
        Intent intent = new Intent(activity, GkashPaymentActivity.class);
        intent.setAction("Payment");
        intent.putExtra("hostUrl", hostUrl);
        intent.putExtra("version", request.getVersion() == null ? "" : request.getVersion());
        intent.putExtra("cid", request.getCid() == null ? "" : request.getCid());
        intent.putExtra("currency", request.getCurrency() == null ? "" : request.getCurrency());
        intent.putExtra("amount", request.getStrAmount() == null ? "" : request.getStrAmount());
        intent.putExtra("cartId", request.getCartId() == null ? "" : request.getCartId());
        intent.putExtra("callbackUrl", request.getCallbackUrl() == null ? "" : request.getCallbackUrl());
        intent.putExtra("returnUrl", defaultReturnUrl);
        intent.putExtra("email", request.getEmail() == null ? "" : request.getEmail());
        intent.putExtra("mobileNo", request.getMobileNo() == null ? "" : request.getMobileNo());
        intent.putExtra("firstName", request.getFirstName() == null ? "" : request.getFirstName());
        intent.putExtra("lastName", request.getLastName() == null ? "" : request.getLastName());
        intent.putExtra("productDescription", request.getProductDescription() == null ? "" : request.getProductDescription());
        intent.putExtra("billingStreet", request.getBillingStreet() == null ? "" : request.getBillingStreet());
        intent.putExtra("billingPostCode", request.getBillingPostCode() == null ? "" : request.getBillingPostCode());
        intent.putExtra("billingCity", request.getBillingCity() == null ? "" : request.getBillingCity());
        intent.putExtra("billingState", request.getBillingState() == null ? "" : request.getBillingState());
        intent.putExtra("billingCountry", request.getBillingCountry() == null ? "" : request.getBillingCountry());
        intent.putExtra("signature", request.generateSignature() == null ? "" : request.generateSignature());
        intent.putExtra("packageName", activity.getPackageName() == null ? "" : activity.getPackageName());
        intent.putExtra("signatureKey", request.getSignatureKey() == null ? "" : request.getSignatureKey());

        activity.startActivity(intent);
    }

}
