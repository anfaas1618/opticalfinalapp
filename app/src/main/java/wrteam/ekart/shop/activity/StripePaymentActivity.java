package wrteam.ekart.shop.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import wrteam.ekart.shop.R;
import wrteam.ekart.shop.fragment.WalletTransactionFragment;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.PaymentModelClass;
import wrteam.ekart.shop.helper.Session;

public class StripePaymentActivity extends AppCompatActivity {
    /**
     * This example collects card payments, implementing the guide here: https://stripe.com/docs/payments/accept-a-payment#android
     * <p>
     * To run this app, follow the steps here: https://github.com/stripe-samples/accept-a-card-payment#how-to-run-locally
     */
    // 10.0.2.2 is the Android emulator's alias to localhost
    private static final String BACKEND_URL = Constant.STRIPE_BASE_URL;

    private final OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;
    private ProgressDialog progressDialog;
    Map<String, String> sendParams;
    PaymentModelClass paymentModelClass;
    double payableAmount = 0;
    String from;
    String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);
        paymentModelClass = new PaymentModelClass(this);
        sendParams = (Map<String, String>) getIntent().getSerializableExtra(Constant.PARAMS);
        payableAmount = Double.parseDouble(sendParams.get(Constant.FINAL_TOTAL));
        from = sendParams.get(Constant.FROM);
        startCheckout();
    }

    private void startCheckout() {
        // Create a PaymentIntent by calling the sample server's /create-payment-intent endpoint.
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        String json = "{"
                + "\"currency\":\"" + Constant.STRIPE_CURRENCY + "\","
                + "\"items\":["
                + "{\"id\":\"photo_subscription\"}"
                + "]"
                + "}";
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "create-payment-intent.php")
                .post(body)
                .build();
        httpClient.newCall(request).enqueue(new PayCallback(this));

        // Hook up the pay button to the card widget and stripe instance
        Button payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener((View view) -> {
            showProgressDialog();
            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if (params != null) {
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(this, confirmParams);
            }
        });
    }

    private void displayAlert(@NonNull String title, @Nullable String message, boolean retry) {
        dismissProgressDialog();
        try {
            JSONObject jsonObject = new JSONObject(message);
            if (from.equals("wallet")) {
                onBackPressed();
                new WalletTransactionFragment().AddWalletBalance(StripePaymentActivity.this, new Session(StripePaymentActivity.this), WalletTransactionFragment.amount, WalletTransactionFragment.msg, jsonObject.getString(Constant.ID));
            } else if (from.equals("payment")) {
                paymentModelClass.PlaceOrder(StripePaymentActivity.this, getString(R.string.paystack), jsonObject.getString(Constant.ID), true, sendParams, Constant.SUCCESS);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        try {
            dismissProgressDialog();
            JSONObject jsonObject = new JSONObject(response.toString());
            if (from.equals("wallet")) {
                onBackPressed();
                new WalletTransactionFragment().AddWalletBalance(StripePaymentActivity.this, new Session(StripePaymentActivity.this), WalletTransactionFragment.amount, WalletTransactionFragment.msg, jsonObject.getString(Constant.ID));
            } else if (from.equals("payment")) {
                paymentModelClass.PlaceOrder(StripePaymentActivity.this, getString(R.string.paystack), jsonObject.getString(Constant.ID), true, sendParams, "success");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<StripePaymentActivity> activityRef;

        PayCallback(@NonNull StripePaymentActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final StripePaymentActivity activity = activityRef.get();
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
            final StripePaymentActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            Log.d("Stripe", "Response : " + response.toString());
            if (response.isSuccessful()) {
                activity.onPaymentSuccess(response);
            }
        }
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<StripePaymentActivity> activityRef;

        PaymentResultCallback(@NonNull StripePaymentActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final StripePaymentActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                activity.displayAlert(
                        "Payment completed",
                        gson.toJson(paymentIntent),
                        true
                );
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage(),
                        false
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final StripePaymentActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString(), false);
        }
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(StripePaymentActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
