package com.gkash.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
                String MerchantId = "M102-U-XXX";
                String SignatureKey = "YourSignatureKey";

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

                //Set to false if staging environment
                gkashPayment.setProductionEnvironment(true);

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
    public void onStatusCallback(PaymentResponse response) {
        if (response != null) {
            String result = "Status: " + response.status + "\n" +
                    "Description: " + response.description + "\n" +
                    "CID: " + response.CID + "\n" +
                    "POID: " + response.POID + "\n" +
                    "cartid: " + response.cartid + "\n" +
                    "amount: " + response.amount + "\n" +
                    "currency: " + response.currency + "\n" +
                    "PaymentType: " + response.PaymentType;

            Toast.makeText(MainActivity.this, result,
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("cartId", response.cartid);
            intent.putExtra("currency", response.currency);
            intent.putExtra("amount", response.amount.toString());
            intent.putExtra("status", response.status);
            intent.putExtra("description", response.description);
            intent.putExtra("paymentType", response.PaymentType);
            startActivity(intent);
        }
    }
}
