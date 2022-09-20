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
    implementation 'io.github.gkashmy:gkash-android-sdk:2.0.0'
}
```


Implement the library as follows. 

```Java
// Create instance of PaymentRequest
PaymentRequest request = new PaymentRequest("1.5.0", MerchantId, SignatureKey, "MYR", amount, cartId, MainActivity.this);
//set your callback url, email ,mobile number and return url
//return url is your app url scheme
request.setCallbackUrl("https://paymentdemo.gkash.my/callback.php");
request.setEmail("test@example.com");
request.setMobileNo("0123456789");
request.setReturnUrl("gkash://returntoapp");

// Get instance of GkashPayment
final GkashPayment gkashPayment = GkashPayment.getInstance();

// Set environment and start payment
gkashPayment.setProductionEnvironment(false);
gkashPayment.startPayment(MainActivity.this, request);
```

## Getting the Payment Result

Upon finishing of the WebView Activity, implement TransStatusCallback to obtain the payment result.


```Java
public class MainActivity extends AppCompatActivity implements TransStatusCallback {
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
            }
        }
}
```

## License
[Apache 2.0](https://choosealicense.com/licenses/apache-2.0/)
