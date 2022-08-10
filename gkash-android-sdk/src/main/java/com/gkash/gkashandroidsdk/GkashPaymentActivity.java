package com.gkash.gkashandroidsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
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

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GkashPaymentActivity extends AppCompatActivity {

    public static String ACTION_RETURN = "android.intent.action.ACTION_RETURN";

    public static int RESULT_OK = 1;

    WebView webView;
    ProgressBar progressBar;
    final private GkashPayment _gkashPayment = GkashPayment.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle intent (NEW_TASK) from intent://gkash.my/return#Intent;scheme=https;package=com.caller.packagename;action=android.intent.action.ACTION_RETURN;end;
        // Only applicable if caller task/activity is destroyed and new task/activity is created
        if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equals(ACTION_RETURN)) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(getIntent().getPackage());
            startActivity(intent);
            finish();
            return;
        }

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

                if (containScheme(url)) {
                    Log.d("webview", "launching app: " + url);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                    return true;
                }
                if (url.startsWith("intent://gkash.my/return")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        setResult(RESULT_OK, intent);
                        finish();
                        return true;
                    }
                    catch (URISyntaxException e) {
                        // Not an intent uri
                        e.printStackTrace();
                    }
                }

                if (url.startsWith("intent://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    catch (URISyntaxException e) {
                        // Not an intent uri
                        e.printStackTrace();
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setResult(RESULT_OK, intent);
        finish();
    }

    private void postData(String url, String postData) {
        try {
            webView.postUrl(url, postData.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean containScheme(String inputString) {

        List<String> walletScheme = _gkashPayment.get_walletScheme();

        for (String scheme : walletScheme) {
            if (inputString.contains(scheme)) {
                return true;
            }
        }
        return false;
    }
}
