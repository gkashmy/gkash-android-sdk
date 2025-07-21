package com.gkash.gkashandroidsdk;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GkashPaymentActivity extends AppCompatActivity {
    WebView webView;
    ProgressBar progressBar;
    private String _defaultBrowser;
    final private GkashPayment _gkashPayment = GkashPayment.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String action = getIntent().getAction();
        if (getIntent() != null && !"Payment".equals(action)) {
            Log.d("GkashPaymentActivity", "onCreate PaymentStatusCallback");
            Intent intent = getIntent();
            Uri uri = intent.getData();
            PaymentStatusCallback(uri);
            finish();
            return;
        }
        _defaultBrowser = getDefaultBrowserPackageName(this);
        Log.d("webview", "_defaultBrowser: " + _defaultBrowser);
        setContentView(R.layout.activity_payment);

        webView = findViewById(R.id.gkashWebView);
        progressBar = findViewById(R.id.gkashWebViewProgressBar);

        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url)
            {
                return shouldOverrideUrlLoading(url);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request)
            {
                Uri uri = request.getUrl();
                return shouldOverrideUrlLoading(uri.toString());
            }

            private boolean shouldOverrideUrlLoading(final String url)
            {
                Log.d("webview", "shouldOverrideUrlLoading: " + url);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                PackageManager pm = getPackageManager();
                ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

                if (resolveInfo != null) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    String appName = resolveInfo.loadLabel(pm).toString();

                    Log.d("AppInfo", "Handled by: " + appName + " (" + packageName + ")");
                    if(!Objects.equals(packageName, _defaultBrowser)){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        Log.d("webview", "launching app: " + url);
                        startActivity(browserIntent);
                        return true;
                    }
                }
                else {
                    if (url.startsWith(getIntent().getStringExtra("returnUrl"))) {
                        Uri uri = Uri.parse(url);
                        Log.d("webview", "shouldOverrideUrlLoading PaymentStatusCallback");
                        PaymentStatusCallback(uri);
                        finish();
                        return true;
                    }
                }
                // Returning true means that application wants to leave the current WebView and handle the url itself, otherwise return false.
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setProgress(0);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setProgress(0);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        String HostURL = getIntent().getStringExtra("hostUrl") + "api/paymentform.aspx";
        String postData = "";

        try {
            postData = "version=" + URLEncoder.encode(getIntent().getStringExtra("version"), "UTF-8") +
                    "&CID=" + URLEncoder.encode(getIntent().getStringExtra("cid"), "UTF-8") +
                    "&v_currency=" + URLEncoder.encode(getIntent().getStringExtra("currency"), "UTF-8") +
                    "&v_amount=" + URLEncoder.encode(getIntent().getStringExtra("amount"), "UTF-8") +
                    "&v_cartid=" + URLEncoder.encode(getIntent().getStringExtra("cartId"), "UTF-8") +
                    "&v_firstname=" + URLEncoder.encode(getIntent().getStringExtra("firstName"), "UTF-8") +
                    "&v_lastname=" + URLEncoder.encode(getIntent().getStringExtra("lastName"), "UTF-8") +
                    "&v_billemail=" + URLEncoder.encode(getIntent().getStringExtra("email"), "UTF-8") +
                    "&v_billstreet=" + URLEncoder.encode(getIntent().getStringExtra("billingStreet"), "UTF-8") +
                    "&v_billpost=" + URLEncoder.encode(getIntent().getStringExtra("billingPostCode"), "UTF-8") +
                    "&v_billcity=" + URLEncoder.encode(getIntent().getStringExtra("billingCity"), "UTF-8") +
                    "&v_billstate=" + URLEncoder.encode(getIntent().getStringExtra("billingState"), "UTF-8") +
                    "&v_billcountry=" + URLEncoder.encode(getIntent().getStringExtra("billingCountry"), "UTF-8") +
                    "&v_billphone=" + URLEncoder.encode(getIntent().getStringExtra("mobileNo"), "UTF-8") +
                    "&returnurl=" + URLEncoder.encode(getIntent().getStringExtra("returnUrl"), "UTF-8") +
                    "&callbackurl=" + URLEncoder.encode(getIntent().getStringExtra("callbackUrl"), "UTF-8") +
                    "&v_productdesc=" + URLEncoder.encode(getIntent().getStringExtra("productDescription"), "UTF-8") +
                    "&signature=" + URLEncoder.encode(getIntent().getStringExtra("signature"), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        postData(HostURL, postData);
    }

    private void postData(String url, String postData) {
        try {
            webView.postUrl(url, postData.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDefaultBrowserPackageName(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"));
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);

        for (ResolveInfo info : resolveInfos) {
            String pkg = info.activityInfo.packageName;
            // Exclude system chooser ("android")
            if (!"android".equals(pkg)) {
                return pkg;
            }
        }
        return null; // No browser found
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        PaymentStatusCallback(uri);
        finish();
    }

    private void PaymentStatusCallback(Uri uri){
        String description = uri.getQueryParameter("description");
        String cid = uri.getQueryParameter("CID");
        String POID = uri.getQueryParameter("POID");
        String status = uri.getQueryParameter("status");
        String currency = uri.getQueryParameter("currency");
        String amount = uri.getQueryParameter("amount");
        String cartid = uri.getQueryParameter("cartid");
        String PaymentType = uri.getQueryParameter("PaymentType");
        String Signature = uri.getQueryParameter("signature");
        String key = getIntent().getStringExtra("signatureKey");
        if(_gkashPayment.getTransStatusCallback() == null || key == null){
            return;
        }

        BigDecimal decAmt = new BigDecimal(amount);
        PaymentResponse response = new PaymentResponse(description, cid, POID, status, currency, decAmt, cartid
                ,PaymentType);
        if (!Signature.equals(response.generateSignature(key))) {
            Log.d("GkashPayment", "Signature not match");
            response.status = "11 - Pending";
            response.description = "Signature not match";
        }
        _gkashPayment.getTransStatusCallback().onStatusCallback(response);
    }
}
