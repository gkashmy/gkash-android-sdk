package com.gkash.demo;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ResultActivity extends AppCompatActivity {

    TextView cartId;
    TextView currency;
    TextView amount;
    TextView status;
    TextView description;
    TextView paymentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        cartId = findViewById(R.id.cartId_value);
        currency = findViewById(R.id.currency_value);
        amount = findViewById(R.id.amount_value);
        status = findViewById(R.id.status_value);
        description = findViewById(R.id.description_value);
        paymentType = findViewById(R.id.paymentType_value);

        Intent intent = getIntent();
        String action = intent.getAction();

        String cartId_value = intent.getStringExtra("cartId");
        String currency_value = intent.getStringExtra("currency");
        String amount_value = intent.getStringExtra("amount");
        String status_value = intent.getStringExtra("status");
        String description_value = intent.getStringExtra("description");
        String paymentType_value = intent.getStringExtra("paymentType");

        Log.i("ResultIntent", "action: " + action);
        Log.i("ResultIntent", "cartid: " + cartId_value);
        Log.i("ResultIntent", "currency: " + currency_value);
        Log.i("ResultIntent", "amount: " + amount_value);
        Log.i("ResultIntent", "status: " + status_value);
        Log.i("ResultIntent", "description: " + description_value);
        Log.i("ResultIntent", "paymentType: " + paymentType_value);

        cartId.setText(cartId_value);
        currency.setText(currency_value);
        amount.setText(amount_value);
        status.setText(status_value);
        description.setText(description_value);
        paymentType.setText(paymentType_value);

        applyStatusStyle(status_value);

        String epkey_value = intent.getStringExtra("epkey");
        Log.i("ResultIntent", "epkey: " + epkey_value);
        if (epkey_value != null && !epkey_value.isEmpty()) {
            TextView epkey = findViewById(R.id.epkey_value);
            epkey.setText(epkey_value);
            findViewById(R.id.epkey_row).setVisibility(View.VISIBLE);
        }

        // Back arrow in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // Colour the status pill: 88 = success, 11 = pending, anything else = failed
    private void applyStatusStyle(String statusValue) {
        int textColor;
        int bgColor;
        if (statusValue != null && statusValue.startsWith("88")) {
            textColor = R.color.statusSuccess;
            bgColor = R.color.statusSuccessBg;
        } else if (statusValue != null && statusValue.startsWith("11")) {
            textColor = R.color.statusPending;
            bgColor = R.color.statusPendingBg;
        } else {
            textColor = R.color.statusFailed;
            bgColor = R.color.statusFailedBg;
        }

        status.setTextColor(ContextCompat.getColor(this, textColor));
        GradientDrawable background = (GradientDrawable) status.getBackground().mutate();
        background.setColor(ContextCompat.getColor(this, bgColor));
    }
}
