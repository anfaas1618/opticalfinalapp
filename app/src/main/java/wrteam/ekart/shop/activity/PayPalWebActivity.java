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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.helper.AppController;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.PaymentModelClass;

public class PayPalWebActivity extends AppCompatActivity {


    Toolbar toolbar;
    WebView webView;
    String url;
    PaymentModelClass paymentModelClass;
    boolean isTxnInProcess = true;
    String itemNo;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pal_web);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("PayPal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        paymentModelClass = new PaymentModelClass(PayPalWebActivity.this);
        url = getIntent().getStringExtra("url");
        itemNo = getIntent().getStringExtra("item_no");
//        System.out.println("=====url paypal " + url);

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
                        String status = jsonObject.getString("status");
                        paymentModelClass.PlaceOrder(PayPalWebActivity.this, getString(R.string.paypal), itemNo, status.equals("Completed"), (Map<String, String>) getIntent().getSerializableExtra("params"), status);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
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
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PayPalWebActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.txn_cancel_msg));

        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();
        alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                paymentModelClass.PlaceOrder(PayPalWebActivity.this,
                        getString(R.string.paypal),
                        "none",
                        false,
                        (Map<String, String>) getIntent().getSerializableExtra("params"),
                        "canceled");

                alertDialog1.dismiss();
                finish();

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
