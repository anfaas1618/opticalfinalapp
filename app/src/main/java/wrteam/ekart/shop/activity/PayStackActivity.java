package wrteam.ekart.shop.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import wrteam.ekart.shop.R;
import wrteam.ekart.shop.fragment.WalletTransactionFragment;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.PaymentModelClass;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.VolleyCallback;
import wrteam.ekart.shop.ui.CreditCardEditText;

public class PayStackActivity extends AppCompatActivity {
    public String email, cardNumber, cvv;
    public int expiryMonth, expiryYear;
    Toolbar toolbar;
    Session session;
    Activity activity;
    TextView tvPayable;
    Map<String, String> sendParams;
    PaymentModelClass paymentModelClass;
    double payableAmount = 0;
    String from;
    //variables
    Card card;
    Charge charge;
    EditText emailField;
    CreditCardEditText cardNumberField;
    EditText expiryMonthField;
    EditText expiryYearField;
    EditText cvvField;

    public static void setPaystackKey(String publicKey) {
        PaystackSdk.setPublicKey(publicKey);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init paystack sdk
        PaystackSdk.initialize(getApplicationContext());
        setContentView(R.layout.activity_pay_stack);
        getAllWidgets();
        setPaystackKey(Constant.PAYSTACK_KEY);
        activity = PayStackActivity.this;
        session = new Session(activity);
        paymentModelClass = new PaymentModelClass(activity);
        sendParams = (Map<String, String>) getIntent().getSerializableExtra(Constant.PARAMS);
        payableAmount = Double.parseDouble(sendParams.get(Constant.FINAL_TOTAL));
        from = sendParams.get(Constant.FROM);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.paystack));
        emailField.setText(session.getData(Session.KEY_EMAIL));
        tvPayable.setText(Constant.SETTING_CURRENCY_SYMBOL + payableAmount);
    }

    public void getAllWidgets() {
        toolbar = findViewById(R.id.toolbar);
        tvPayable = findViewById(R.id.tvPayable);
        emailField = findViewById(R.id.edit_email_address);
        cardNumberField = findViewById(R.id.edit_card_number);
        expiryMonthField = findViewById(R.id.edit_expiry_month);
        expiryYearField = findViewById(R.id.edit_expiry_year);
        cvvField = findViewById(R.id.edit_cvv);
    }

    /**
     * Method to perform the charging of the card
     */
    void performCharge() {
        //create a Charge object
        String[] amount = String.valueOf(payableAmount * 100).split("\\.");
        charge = new Charge();
        charge.setCard(card); //set the card to charge
        charge.setEmail(email); //dummy email address
        charge.setAmount(Integer.parseInt(amount[0])); //test amount
        PaystackSdk.chargeCard(PayStackActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.
                String paymentReference = transaction.getReference();
                if (from.equals(Constant.WALLET)) {
                    onBackPressed();
                    new WalletTransactionFragment().AddWalletBalance(activity, new Session(activity), WalletTransactionFragment.amount, WalletTransactionFragment.msg, paymentReference);
                } else if (from.equals(Constant.PAYMENT)) {
                    verifyReference(String.valueOf(charge.getAmount()), paymentReference, charge.getEmail());
                }

            }

            @Override
            public void beforeValidate(Transaction transaction) {
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                //handle error here
            }
        });
    }

    boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.requestFocus();
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.requestFocus();
            emailField.setError(null);
        }

        String cardNumber = cardNumberField.getText().toString();
        if (TextUtils.isEmpty(cardNumber)) {
            cardNumberField.requestFocus();
            cardNumberField.setError("Required.");
            valid = false;
        } else {
            cardNumberField.setError(null);
        }


        String expiryMonth = expiryMonthField.getText().toString();
        if (TextUtils.isEmpty(expiryMonth)) {
            expiryMonthField.requestFocus();
            expiryMonthField.setError("Required.");
            valid = false;
        } else {
            expiryMonthField.setError(null);
        }

        String expiryYear = expiryYearField.getText().toString();
        if (TextUtils.isEmpty(expiryYear)) {
            expiryYearField.requestFocus();
            expiryYearField.setError("Required.");
            valid = false;
        } else {
            expiryYearField.setError(null);
        }

        String cvv = cvvField.getText().toString();
        if (TextUtils.isEmpty(cvv)) {
            cvvField.requestFocus();
            cvvField.setError("Required.");
            valid = false;
        } else {
            cvvField.setError(null);
        }

        return valid;
    }

    public void PayButton(View view) {
        if (!validateForm()) {
            return;
        }
        try {
            email = emailField.getText().toString().trim();
            cardNumber = cardNumberField.getText().toString().trim();
            expiryMonth = Integer.parseInt(expiryMonthField.getText().toString().trim());
            expiryYear = Integer.parseInt(expiryYearField.getText().toString().trim());
            cvv = cvvField.getText().toString().trim();

            //String cardNumber = "4084 0840 8408 4081";
            //int expiryMonth = 11; //any month in the future
            //int expiryYear = 18; // any year in the future
            //String cvv = "408";
            card = new Card(cardNumber, expiryMonth, expiryYear, cvv);

            paymentModelClass.showProgressDialog();
            if (card.isValid()) {
                performCharge();
            } else {
                paymentModelClass.hideProgressDialog();
                Toast.makeText(PayStackActivity.this, "Card is not Valid", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verifyReference(String amount, String reference, String email) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.VERIFY_PAYSTACK, Constant.GetVal);
        params.put(Constant.AMOUNT, amount);
        params.put(Constant.REFERENCE, reference);
        params.put(Constant.EMAIL, email);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString(Constant.STATUS);
                        paymentModelClass.PlaceOrder(activity, getString(R.string.paystack), reference, status.equalsIgnoreCase(Constant.SUCCESS), (Map<String, String>) getIntent().getSerializableExtra(Constant.PARAMS), status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.VERIFY_PAYMENT_REQUEST, params, false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}