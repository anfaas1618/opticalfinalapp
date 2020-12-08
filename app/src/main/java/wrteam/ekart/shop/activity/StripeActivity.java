package wrteam.ekart.shop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import wrteam.ekart.shop.R;
import wrteam.ekart.shop.fragment.AddressListFragment;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.VolleyCallback;

public class StripeActivity extends AppCompatActivity {

    private String paymentIntentClientSecret;
    private Stripe stripe;
    String stripePublishableKey;
    Button payButton;
    Map<String, String> sendparams;
    Session session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);
        payButton = findViewById(R.id.payButton);
        session = new Session(StripeActivity.this);
        sendparams = (Map<String, String>) getIntent().getSerializableExtra(Constant.PARAMS);
        startCheckout();
    }

    private void startCheckout() {
        String[] address = AddressListFragment.selectedAddress.split(", ");
        int pincode = Integer.parseInt(address[6].replace(getString(R.string.pincode_), ""));
        pincode = (int) pincode / 10;

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.NAME, session.getData(Constant.NAME));
        params.put(Constant.ADDRESS_LINE1, AddressListFragment.selectedAddress);
        params.put(Constant.POSTAL_CODE, "" + pincode);
        params.put(Constant.CITY, address[2]);
        params.put(Constant.AMOUNT, "" + (int) Math.round(Double.parseDouble(sendparams.get(Constant.FINAL_TOTAL))));
        params.put(Constant.ORDER_ID, getIntent().getStringExtra(Constant.ORDER_ID));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        stripePublishableKey = jsonObject.getString(Constant.publishableKey);
                        paymentIntentClientSecret = jsonObject.getString(Constant.clientSecret);

                        stripe = new Stripe(
                                getApplicationContext(),
                                Objects.requireNonNull(stripePublishableKey)
                        );

                        payButton.setOnClickListener((View view) -> {
                            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
                            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
                            if (params != null) {
                                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                                stripe.confirmPayment(StripeActivity.this, confirmParams);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, StripeActivity.this, Constant.STRIPE_BASE_URL, params, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }
    private static final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<StripeActivity> activityRef;

        PayCallback(@NonNull StripeActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final StripeActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final StripeActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            Log.d("Stripe", "Response : " + response.toString());
            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }


    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );
        Log.d("Stripe", "response.body() : " + responseMap.toString());
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull private final WeakReference<StripeActivity> activityRef;

        PaymentResultCallback(@NonNull StripeActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final StripeActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();

            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method

            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final StripeActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

        }
    }
}
