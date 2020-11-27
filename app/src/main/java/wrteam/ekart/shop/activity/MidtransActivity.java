package wrteam.ekart.shop.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.helper.AppController;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.PaymentModelClass;

public class MidtransActivity extends AppCompatActivity {
    Toolbar toolbar;
    WebView webView;
    String url;
    PaymentModelClass paymentModelClass;
    boolean isTxnInProcess = true;
    String itemNo;
    Map<String, String> sendParams;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pal_web);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Midtrans");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        paymentModelClass = new PaymentModelClass(MidtransActivity.this);
        url = getIntent().getStringExtra("url");
        itemNo = getIntent().getStringExtra(Constant.ORDER_ID);
        sendParams = (Map<String, String>) getIntent().getSerializableExtra(Constant.PARAMS);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(Constant.MAINBASEUrl)) {
                    GetTransactionResponse(url);
                    return true;
                } else
                    isTxnInProcess = true;
                return false;
            }
        });
        webView.loadUrl(url);
    }

    public void GetTransactionResponse(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    isTxnInProcess = false;
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("transaction_status");
                        if (status.equals("capture") || status.equals("challenge") || status.equals("pending")) {
                            paymentModelClass.PlaceOrder(MidtransActivity.this, getString(R.string.midtrans), itemNo, true, sendParams, status);
                        } else if (status.equals("deny") || status.equals("expire") || status.equals("cancel")) {
                            paymentModelClass.PlaceOrder(MidtransActivity.this, getString(R.string.midtrans), itemNo, true, sendParams, status);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                (VolleyError error) -> error.printStackTrace());
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);

    }


    @Override
    public void onBackPressed() {
        if (isTxnInProcess)
            ProcessAlertDialog();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void ProcessAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MidtransActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.txn_cancel_msg));
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();
        alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                paymentModelClass.PlaceOrder(MidtransActivity.this, getString(R.string.paypal), "none", false, sendParams, "canceled");
                alertDialog1.dismiss();
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
