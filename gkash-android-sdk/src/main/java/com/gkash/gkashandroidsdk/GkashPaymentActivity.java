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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GkashPaymentActivity extends AppCompatActivity {
    WebView webView;
    ProgressBar progressBar;
    private String _defaultBrowser;
    final private GkashPayment _gkashPayment = GkashPayment.getInstance();
    private static final String WECHAT_CHECK_MWEB_PREFIX = "https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb";

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

                if(url.startsWith(WECHAT_CHECK_MWEB_PREFIX)){
                    Log.d("AppInfo", "add header to wechat");
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Referer", _gkashPayment.getHostUrl());
                    webView.loadUrl(url, headers);
                    return true;
                }

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

        boolean hostToHost = getIntent().getBooleanExtra("hostToHost", false);
        String HostURL = getIntent().getStringExtra("hostUrl") + (hostToHost ? "api/payment/submit" : "api/paymentform.aspx");
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
                    "&recurringtype=" + URLEncoder.encode(getIntent().getStringExtra("recurringType"), "UTF-8") +
                    "&epkey=" + URLEncoder.encode(getIntent().getStringExtra("epKey"), "UTF-8") +
                    "&signature=" + URLEncoder.encode(getIntent().getStringExtra("signature"), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (hostToHost) {
            // Host to host returns JSON instead of a payment page, so post it directly
            submitHostToHost(HostURL, postData);
        } else {
            postData(HostURL, postData);
        }
    }

    private void submitHostToHost(final String url, final String postData) {
        progressBar.setIndeterminate(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                String body;
                try {
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setConnectTimeout(30000);
                    connection.setReadTimeout(60000);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Accept", "application/json");

                    OutputStream os = connection.getOutputStream();
                    os.write(postData.getBytes(StandardCharsets.UTF_8));
                    os.close();

                    int code = connection.getResponseCode();
                    InputStream is = code >= 400 ? connection.getErrorStream() : connection.getInputStream();
                    body = readStream(is);
                    Log.d("GkashPayment", "Host to host response (" + code + "): " + body);
                } catch (Exception e) {
                    e.printStackTrace();
                    body = null;
                } finally {
                    if (connection != null) connection.disconnect();
                }

                final String responseBody = body;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HostToHostStatusCallback(responseBody);
                        finish();
                    }
                });
            }
        }).start();
    }

    private String readStream(InputStream is) throws IOException {
        if (is == null) return "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    private void HostToHostStatusCallback(String body) {
        try {
            JSONObject json = new JSONObject(body);
            DeliverStatus(
                    json.optString("description", null),
                    json.optString("CID", null),
                    json.optString("POID", null),
                    json.optString("status", null),
                    json.optString("currency", null),
                    json.optString("amount", null),
                    json.optString("cartid", null),
                    json.optString("PaymentType", null),
                    json.optString("epkey", null),
                    json.optString("signature", null));
        } catch (Exception e) {
            Log.d("GkashPayment", "Host to host response is not valid JSON: " + body);
            if (_gkashPayment.getTransStatusCallback() == null) return;

            PaymentResponse response = new PaymentResponse(
                    body == null ? "Unable to reach payment server" : "Invalid response from payment server",
                    getIntent().getStringExtra("cid"), null, "Request failed",
                    getIntent().getStringExtra("currency"), new BigDecimal(getIntent().getStringExtra("amount")),
                    getIntent().getStringExtra("cartId"), null);
            response.epkey = getIntent().getStringExtra("epKey");
            _gkashPayment.getTransStatusCallback().onStatusCallback(response);
        }
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
        DeliverStatus(
                uri.getQueryParameter("description"),
                uri.getQueryParameter("CID"),
                uri.getQueryParameter("POID"),
                uri.getQueryParameter("status"),
                uri.getQueryParameter("currency"),
                uri.getQueryParameter("amount"),
                uri.getQueryParameter("cartid"),
                uri.getQueryParameter("PaymentType"),
                uri.getQueryParameter("epkey"),
                uri.getQueryParameter("signature"));
    }

    private void DeliverStatus(String description, String cid, String POID, String status, String currency,
                               String amount, String cartid, String PaymentType, String epkey, String Signature){
        String key = getIntent().getStringExtra("signatureKey");
        if(_gkashPayment.getTransStatusCallback() == null || key == null){
            return;
        }

        BigDecimal decAmt = new BigDecimal(amount);
        PaymentResponse response = new PaymentResponse(description, cid, POID, status, currency, decAmt, cartid
                ,PaymentType);
        response.epkey = epkey;
        if (Signature == null || !Signature.equals(response.generateSignature(key))) {
            Log.d("GkashPayment", "Signature not match");
            response.status = "11 - Pending";
            response.description = "Signature not match";
        }
        _gkashPayment.getTransStatusCallback().onStatusCallback(response);
    }
}
