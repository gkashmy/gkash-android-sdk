package com.gkash.demo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gkash.gkashandroidsdk.GkashPayment;
import com.gkash.gkashandroidsdk.PaymentRequest;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button submitButton;
    EditText amountInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submitButton = findViewById(R.id.submit);
        amountInput = findViewById(R.id.amount);

        final GkashPayment gkashPayment = GkashPayment.getInstance();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Integration Credentials
                String MerchantId = "M161-U-20001";
                String SignatureKey = "YourSignatureKey";

                // Unique transaction cart id
                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String cartId = dateFormat.format(date);

                BigDecimal amount = new BigDecimal(amountInput.getText().toString());

                PaymentRequest request = new PaymentRequest("1.5.0", MerchantId, SignatureKey, "MYR", amount, cartId);
                request.setCallbackUrl("https://paymentdemo.gkash.my/callback.php");
                request.setEmail("test@example.com");
                request.setMobileNo("0123456789");

                gkashPayment.setProductionEnvironment(false);

                try {
                    gkashPayment.startPayment(MainActivity.this, request);
                }
                catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (data != null) {
                String result = "Status: " + data.getStringExtra("status") + "\n" +
                        "Description: " + data.getStringExtra("description") + "\n" +
                        "CID: " + data.getStringExtra("CID") + "\n" +
                        "POID: " + data.getStringExtra("POID") + "\n" +
                        "cartid: " + data.getStringExtra("cartid") + "\n" +
                        "amount: " + data.getStringExtra("amount") + "\n" +
                        "currency: " + data.getStringExtra("currency") + "\n" +
                        "PaymentType: " + data.getStringExtra("PaymentType");

                Toast.makeText(MainActivity.this, result,
                        Toast.LENGTH_LONG).show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(MainActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
