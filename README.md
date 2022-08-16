# Gkash Payment SDK for Android

This library allows you to integrate Gkash Payment Gateway into your Android App.


## Usage

Ensure jcenter is in the root level build.gradle file of an Android Project.

```Gradle
repositories {
    mavenCentral()
}
```


Include the library reference in the build.gradle file.

```Gradle
dependencies {
    implementation 'io.github.gkashmy:gkash-android-sdk:1.0.3'
}
```


Implement the library as follows. 

```Java
// Create instance of PaymentRequest
PaymentRequest request = new PaymentRequest("1.5.0", merchantId, signatureKey, "MYR", amount, cartId);
request.setCallbackUrl("https://paymentdemo.gkash.my/callback.php");

// Get instance of GkashPayment
final GkashPayment gkashPayment = GkashPayment.getInstance();

// Set environment and start payment
gkashPayment.setProductionEnvironment(false);
gkashPayment.startPayment(MyActivity.this, request);
```

## Getting the Payment Result

Upon finishing of the WebView Activity, implement [onActivityResult](https://developer.android.com/training/basics/intents/result) to obtain the payment result.


```Java
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    try {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Toast.makeText(MyActivity.this, "Transaction Status: " + data.getStringExtra("status"),
                    Toast.LENGTH_SHORT).show();
        }

    } catch (Exception e) {
        e.printStackTrace();
        Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
    }
}
```

## License
[Apache 2.0](https://choosealicense.com/licenses/apache-2.0/)
