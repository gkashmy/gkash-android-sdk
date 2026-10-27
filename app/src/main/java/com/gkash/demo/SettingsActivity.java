package com.gkash.demo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    EditText merchantIdInput;
    EditText signatureKeyInput;
    TextView toggleKeyButton;
    SwitchCompat productionSwitch;
    TextView environmentHint;
    Button saveButton;

    MerchantSettings settings;
    boolean keyVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        merchantIdInput = findViewById(R.id.merchant_id);
        signatureKeyInput = findViewById(R.id.signature_key);
        toggleKeyButton = findViewById(R.id.toggle_key);
        productionSwitch = findViewById(R.id.production);
        environmentHint = findViewById(R.id.environment_hint);
        saveButton = findViewById(R.id.save);

        settings = new MerchantSettings(this);

        // Restore saved values only on first creation, so edits survive rotation
        if (savedInstanceState == null) {
            merchantIdInput.setText(settings.getMerchantId());
            signatureKeyInput.setText(settings.getSignatureKey());
            productionSwitch.setChecked(settings.isProduction());
        }
        updateEnvironmentHint(productionSwitch.isChecked());

        productionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateEnvironmentHint(isChecked);
            }
        });

        toggleKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyVisible = !keyVisible;
                int variation = keyVisible
                        ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        : InputType.TYPE_TEXT_VARIATION_PASSWORD;
                // setInputType swaps password fields to monospace, so keep the current typeface
                Typeface typeface = signatureKeyInput.getTypeface();
                signatureKeyInput.setInputType(InputType.TYPE_CLASS_TEXT | variation);
                signatureKeyInput.setTypeface(typeface);
                signatureKeyInput.setSelection(signatureKeyInput.getText().length());
                toggleKeyButton.setText(keyVisible ? "Hide" : "Show");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        // Back arrow in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void updateEnvironmentHint(boolean production) {
        environmentHint.setText(production
                ? "On: payments go to production (api.gkash.my)."
                : "Off: payments go to staging (api-staging.pay.asia).");
    }

    private void save() {
        String merchantId = merchantIdInput.getText().toString().trim();
        String signatureKey = signatureKeyInput.getText().toString().trim();

        if (merchantId.isEmpty()) {
            merchantIdInput.setError("Merchant ID is required");
            merchantIdInput.requestFocus();
            return;
        }
        if (signatureKey.isEmpty()) {
            signatureKeyInput.setError("Signature key is required");
            signatureKeyInput.requestFocus();
            return;
        }

        settings.save(merchantId, signatureKey, productionSwitch.isChecked());
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
