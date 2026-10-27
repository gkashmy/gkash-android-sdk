package com.gkash.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gkash.gkashandroidsdk.GkashPayment;
import com.gkash.gkashandroidsdk.PaymentRequest;
import com.gkash.gkashandroidsdk.PaymentResponse;
import com.gkash.gkashandroidsdk.TransStatusCallback;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements TransStatusCallback {

    Button submitButton;
    Button bindButton;
    Button recurringButton;
    EditText amountInput;
    EditText epKeyInput;
    TextView environmentLabel;

    MerchantSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submitButton = findViewById(R.id.submit);
        bindButton = findViewById(R.id.bind);
        recurringButton = findViewById(R.id.recurring);
        amountInput = findViewById(R.id.amount);
        epKeyInput = findViewById(R.id.epkey);
        environmentLabel = findViewById(R.id.environment_label);

        settings = new MerchantSettings(this);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkCredentials()) return;
                startPayment(buildPaymentRequest());
            }
        });

        bindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkCredentials()) return;
                PaymentRequest request = buildPaymentRequest();
                request.setRecurringType("MONTHLY");
                startPayment(request);
            }
        });

        recurringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkCredentials()) return;
                PaymentRequest request = buildPaymentRequest();
                request.setRecurringType("MONTHLY");
                request.setEpKey(epKeyInput.getText().toString().trim());
                request.setHostToHost(true);
                startPayment(request);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh after returning from Settings
        String merchantId = settings.getMerchantId();
        environmentLabel.setText((merchantId.isEmpty() ? "Merchant ID not set" : merchantId) + " · "
                + (settings.isProduction() ? "Production" : "Staging"));
    }

    // Send the user to Settings when no credentials are saved yet
    private boolean checkCredentials() {
        if (settings.hasCredentials()) {
            return true;
        }
        Toast.makeText(this, "Enter your Merchant ID and Signature Key first", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, SettingsActivity.class));
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private PaymentRequest buildPaymentRequest() {
        // Integration Credentials, edited on the Settings page
        String MerchantId = settings.getMerchantId();
        String SignatureKey = settings.getSignatureKey();

        // Unique transaction cart id
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String cartId = dateFormat.format(date);

        BigDecimal amount = new BigDecimal(amountInput.getText().toString());

        PaymentRequest request = new PaymentRequest("1.5.0", MerchantId, SignatureKey, "MYR", amount, cartId, MainActivity.this);
        //set your callback url, email ,mobile number and return url
        //return url is your app url scheme
        request.setCallbackUrl("https://paymentdemo.gkash.my/callback.php");
        request.setEmail("test@example.com");
        request.setMobileNo("0123456789");
        request.setReturnUrl("gkash://returntoapp");
        return request;
    }

    private void startPayment(PaymentRequest request) {
        GkashPayment gkashPayment = GkashPayment.getInstance();

        //Production toggle on the Settings page, false = staging environment
        gkashPayment.setProductionEnvironment(settings.isProduction());

        try {
            gkashPayment.startPayment(MainActivity.this, request);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusCallback(PaymentResponse response) {
        if (response != null) {
            String result = "Status: " + response.status + "\n" +
                    "Description: " + response.description + "\n" +
                    "CID: " + response.CID + "\n" +
                    "POID: " + response.POID + "\n" +
                    "cartid: " + response.cartid + "\n" +
                    "amount: " + response.amount + "\n" +
                    "currency: " + response.currency + "\n" +
                    "PaymentType: " + response.PaymentType + "\n" +
                    "epkey: " + response.epkey;

            Toast.makeText(MainActivity.this, result,
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("cartId", response.cartid);
            intent.putExtra("currency", response.currency);
            intent.putExtra("amount", response.amount.toString());
            intent.putExtra("status", response.status);
            intent.putExtra("description", response.description);
            intent.putExtra("paymentType", response.PaymentType);
            intent.putExtra("epkey", response.epkey);
            startActivity(intent);
        }
    }
}
