package wrteam.ekart.shop.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.MainActivity;
import wrteam.ekart.shop.adapter.CheckoutItemListAdapter;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.AppController;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.VolleyCallback;
import wrteam.ekart.shop.model.Cart;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class CheckoutFragment extends Fragment {
    public static String pCode = "", appliedCode = "", deliveryCharge = "0";
    public double pCodeDiscount = 0.0, subtotal = 0.0, dCharge = 0.0, taxAmt = 0.0, total = 0.0;
    public TextView tvConfirmOrder, tvPayment, tvDelivery;
    public ArrayList<String> variantIdList, qtyList;
    public TextView tvTaxPercent, tvTaxAmt, tvAlert, tvTotalBeforeTax, tvDeliveryCharge, tvSubTotal, tvPromoCode, tvPCAmount, txttotalitems;
    public LinearLayout lytTax, processLyt, lytPromo;
    RecyclerView recyclerView;
    View root;
    RelativeLayout confirmLyt;
    boolean isApplied;
    ImageView imgRefresh;
    Button btnApply;
    ProgressBar progressBar;
    EditText edtPromoCode;
    Session session;
    Activity activity;
    CheckoutItemListAdapter checkoutItemListAdapter;
    ArrayList<Cart> carts;
    double totalPercentage = 0.0;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_checkout, container, false);

        activity = getActivity();
        session = new Session(activity);
        progressBar = root.findViewById(R.id.progressBar);
        lytTax = root.findViewById(R.id.lytTax);
        tvTaxAmt = root.findViewById(R.id.tvTaxAmt);
        tvTaxPercent = root.findViewById(R.id.tvTaxPercent);
        tvDelivery = root.findViewById(R.id.tvSummary);
        tvPayment = root.findViewById(R.id.tvPayment);
        tvPCAmount = root.findViewById(R.id.tvPCAmount);
        tvPromoCode = root.findViewById(R.id.tvPromoCode);
        tvAlert = root.findViewById(R.id.tvAlert);
        edtPromoCode = root.findViewById(R.id.edtPromoCode);
        tvSubTotal = root.findViewById(R.id.tvSubTotal);
        txttotalitems = root.findViewById(R.id.txttotalitems);
        tvDeliveryCharge = root.findViewById(R.id.tvDeliveryCharge);
        confirmLyt = root.findViewById(R.id.confirmLyt);
        tvConfirmOrder = root.findViewById(R.id.tvConfirmOrder);
        processLyt = root.findViewById(R.id.processLyt);
        lytPromo = root.findViewById(R.id.lytPromo);
        imgRefresh = root.findViewById(R.id.imgRefresh);
        tvTotalBeforeTax = root.findViewById(R.id.tvTotalBeforeTax);
        btnApply = root.findViewById(R.id.btnApply);
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        carts = new ArrayList<>();

        setHasOptionsMenu(true);
        txttotalitems.setText(Constant.TOTAL_CART_ITEM + " Items");

        tvConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PaymentFragment();
                Bundle bundle = new Bundle();
                bundle.putDouble("subtotal", Double.parseDouble(Constant.formater.format(subtotal)));
                bundle.putDouble("total", Double.parseDouble(Constant.formater.format(total)));
                bundle.putDouble("taxAmt", Double.parseDouble(Constant.formater.format(taxAmt)));
                bundle.putDouble("tax", Double.parseDouble(Constant.formater.format(((taxAmt * 100) / total))));
                bundle.putDouble("pCodeDiscount", Double.parseDouble(Constant.formater.format(pCodeDiscount)));
                bundle.putString("pCode", pCode);
                bundle.putDouble("dCharge", Constant.SETTING_DELIVERY_CHARGE);
                bundle.putStringArrayList("variantIdList", variantIdList);
                bundle.putStringArrayList("qtyList", qtyList);
                bundle.putString(Constant.FROM, "process");
                bundle.putString("address", getArguments().getString("address"));
                PaymentFragment.paymentMethod = "";
                PaymentFragment.deliveryTime = "";
                PaymentFragment.deliveryDay = "";
                fragment.setArguments(bundle);
                MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }
        });

        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isApplied) {
                    btnApply.setEnabled(true);
                    btnApply.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    btnApply.setText("Apply");
                    edtPromoCode.setText("");
                    lytPromo.setVisibility(View.GONE);
                    isApplied = false;
                    appliedCode = "";
                    pCode = "";
                    SetDataTotal();
                } else {
                    lytPromo.setVisibility(View.VISIBLE);
                }
            }
        });


        if (AppController.isConnected(activity)) {
            ApiConfig.getWalletBalance(activity, session);
            getCartData();
            PromoCodeCheck();
        }

        return root;
    }


    void getCartData() {

        ApiConfig.getCartItemCount(activity, session);

        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_USER_CART, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.LIMIT, "" + Constant.TOTAL_CART_ITEM);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        Gson gson = new Gson();
                        variantIdList = new ArrayList<>();
                        qtyList = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            Cart cart = gson.fromJson(String.valueOf(jsonArray.getJSONObject(i)), Cart.class);
                            variantIdList.add(cart.getProduct_variant_id());
                            qtyList.add(cart.getQty());

                            totalPercentage += (Double.parseDouble(cart.getItems().get(0).getTax_percentage()));

                            float price;
                            if (cart.getItems().get(0).getDiscounted_price().equals("0")) {
                                price = Float.parseFloat(cart.getItems().get(0).getPrice());
                            } else {
                                price = Float.parseFloat(cart.getItems().get(0).getDiscounted_price());
                            }

                            double itemTotal = price * (Integer.parseInt(cart.getQty()));
                            double itemTaxAmt = (itemTotal * (Double.parseDouble(cart.getItems().get(0).getTax_percentage()) / 100));

                            taxAmt += itemTaxAmt;
                            total += itemTotal;

                            carts.add(cart);
                        }
                        checkoutItemListAdapter = new CheckoutItemListAdapter(getActivity(), carts);
                        recyclerView.setAdapter(checkoutItemListAdapter);
                        SetDataTotal();

                        confirmLyt.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.CART_URL, params, false);
    }

    @SuppressLint("SetTextI18n")
    public void SetDataTotal() {
        tvTotalBeforeTax.setText(Constant.SETTING_CURRENCY_SYMBOL + Constant.formater.format(Double.parseDouble("" + total)));
        subtotal = total;
        if (total <= Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY) {
            tvDeliveryCharge.setText(Constant.SETTING_CURRENCY_SYMBOL + Constant.formater.format(Constant.SETTING_DELIVERY_CHARGE));
            subtotal = Double.parseDouble(Constant.formater.format(subtotal + Constant.SETTING_DELIVERY_CHARGE));
            deliveryCharge = Constant.formater.format(Constant.SETTING_DELIVERY_CHARGE);
        } else {
            tvDeliveryCharge.setText(getResources().getString(R.string.free));
            deliveryCharge = "0";
        }
        dCharge = tvDeliveryCharge.getText().toString().equals(getString(R.string.free)) ? 0.0 : Constant.SETTING_DELIVERY_CHARGE;
        if (pCode.isEmpty()) {
            subtotal = (subtotal + taxAmt);
        } else {
            subtotal = (subtotal + taxAmt - pCodeDiscount);
        }
        tvTaxPercent.setText("Tax (" + Constant.formater.format((taxAmt * 100) / total) + "%)");
        tvTaxAmt.setText("+ " + Constant.SETTING_CURRENCY_SYMBOL + "" + Constant.formater.format(Double.parseDouble("" + taxAmt)));
        tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + "" + Constant.formater.format(Double.parseDouble("" + subtotal)));
    }

    public void PromoCodeCheck() {
        btnApply.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                final String promoCode = edtPromoCode.getText().toString().trim();
                if (promoCode.isEmpty()) {
                    tvAlert.setVisibility(View.VISIBLE);
                    tvAlert.setText("Enter Promo Code");
                } else if (isApplied && promoCode.equals(appliedCode)) {
                    Toast.makeText(getContext(), "promo code already applied", Toast.LENGTH_SHORT).show();
                } else {
                    if (isApplied) {
                        SetDataTotal();
                    }
                    tvAlert.setVisibility(View.GONE);
                    btnApply.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    Map<String, String> params = new HashMap<>();
                    params.put(Constant.VALIDATE_PROMO_CODE, Constant.GetVal);
                    params.put(Constant.USER_ID, session.getData(Constant.ID));
                    params.put(Constant.PROMO_CODE, promoCode);
                    params.put(Constant.TOTAL, String.valueOf((total + taxAmt + dCharge)));

                    ApiConfig.RequestToVolley(new VolleyCallback() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(boolean result, String response) {
                            if (result) {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    //   System.out.println("===res " + response);
                                    if (!object.getBoolean(Constant.ERROR)) {
                                        pCode = object.getString(Constant.PROMO_CODE);
                                        tvPromoCode.setText(getString(R.string.promo_code) + "(" + pCode + ")");
                                        btnApply.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_green));
                                        btnApply.setText("Applied");
                                        btnApply.setEnabled(false);
                                        isApplied = true;
                                        lytPromo.setVisibility(View.VISIBLE);
                                        appliedCode = edtPromoCode.getText().toString();
                                        dCharge = tvDeliveryCharge.getText().toString().equals(getString(R.string.free)) ? 0.0 : Constant.SETTING_DELIVERY_CHARGE;
                                        subtotal = (object.getDouble(Constant.DISCOUNTED_AMOUNT));
                                        pCodeDiscount = Double.parseDouble(object.getString(Constant.DISCOUNT));
                                        tvPCAmount.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + pCodeDiscount);
                                        tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + Constant.formater.format(Double.parseDouble("" + subtotal)));
                                    } else {
                                        btnApply.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                                        btnApply.setText("Apply");
                                        btnApply.setEnabled(true);
                                        tvAlert.setVisibility(View.VISIBLE);
                                        tvAlert.setText(object.getString("message"));
                                        SetDataTotal();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    btnApply.setVisibility(View.VISIBLE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, activity, Constant.PROMO_CODE_CHECK_URL, params, false);

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.checkout);
        activity.invalidateOptionsMenu();
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        activity.invalidateOptionsMenu();
    }

}