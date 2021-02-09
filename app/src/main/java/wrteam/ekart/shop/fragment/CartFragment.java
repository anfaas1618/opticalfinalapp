package wrteam.ekart.shop.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
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
import wrteam.ekart.shop.activity.LoginActivity;
import wrteam.ekart.shop.activity.MainActivity;
import wrteam.ekart.shop.adapter.CartAdapter;
import wrteam.ekart.shop.adapter.OfflineCartAdapter;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.DatabaseHelper;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.VolleyCallback;
import wrteam.ekart.shop.model.Cart;
import wrteam.ekart.shop.model.OfflineCart;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static wrteam.ekart.shop.helper.ApiConfig.GetSettings;

public class CartFragment extends Fragment {
    public static LinearLayout lytempty;
    public static RelativeLayout lytTotal;
    public static ArrayList<Cart> carts;
    public static ArrayList<OfflineCart> offlineCarts;
    public static HashMap<String, String> values;
    public static boolean isSoldOut = false;
    static TextView txttotalamount, txttotalitems, tvConfirmOrder;
    static CartAdapter cartAdapter;
    static OfflineCartAdapter offlineCartAdapter;
    static Activity activity;
    static Session session;
    static JSONObject objectbject;
    View root;
    RecyclerView cartrecycleview;
    NestedScrollView scrollView;
    double total;
    ProgressBar progressBar;
    Button btnShowNow;
    private DatabaseHelper databaseHelper;

    @SuppressLint("SetTextI18n")
    public static void SetData() {
        txttotalamount.setText(session.getData(Constant.currency) + Constant.formater.format(Constant.FLOAT_TOTAL_AMOUNT));
        txttotalitems.setText(Constant.TOTAL_CART_ITEM + " Items");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_cart, container, false);

        values = new HashMap<>();
        activity = getActivity();
        session = new Session(getActivity());
        progressBar = root.findViewById(R.id.progressBar);
        lytTotal = root.findViewById(R.id.lytTotal);
        lytempty = root.findViewById(R.id.lytempty);
        btnShowNow = root.findViewById(R.id.btnShowNow);
        txttotalamount = root.findViewById(R.id.txttotalamount);
        txttotalitems = root.findViewById(R.id.txttotalitems);
        scrollView = root.findViewById(R.id.scrollView);
        cartrecycleview = root.findViewById(R.id.cartrecycleview);
        tvConfirmOrder = root.findViewById(R.id.tvConfirmOrder);
        databaseHelper = new DatabaseHelper(activity);

        ApiConfig.GetSettings(activity);

        setHasOptionsMenu(true);

        Constant.FLOAT_TOTAL_AMOUNT = 0.00;

        carts = new ArrayList<>();
        cartrecycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (ApiConfig.isConnected(getActivity())) {
            if (session.isUserLoggedIn()) {
                getCartData();
                GetSettings(getActivity());
            } else {
                GetOfflineCart();
            }
        }

        tvConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApiConfig.isConnected(requireActivity())) {
                    if (!isSoldOut) {
                        if (Constant.SETTING_MINIMUM_ORDER_AMOUNT <= Constant.FLOAT_TOTAL_AMOUNT) {
                            if (session.isUserLoggedIn()) {
                                if (values.size() > 0) {
                                    ApiConfig.AddMultipleProductInCart(session, getActivity(), values);
                                }

                                AddressListFragment.selectedAddress = "";
                                Fragment fragment = new AddressListFragment();
                                final Bundle bundle = new Bundle();
                                bundle.putString(Constant.FROM, "process");
                                bundle.putDouble("total", Constant.FLOAT_TOTAL_AMOUNT);
                                fragment.setArguments(bundle);
                                MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                            } else {
                                startActivity(new Intent(getActivity(), LoginActivity.class).putExtra("fromto", "checkout").putExtra("total", Constant.FLOAT_TOTAL_AMOUNT).putExtra(Constant.FROM, "checkout"));
                            }
                        } else {
                            Toast.makeText(activity, getString(R.string.msg_minimum_order_amount) + session.getData(Constant.currency) + Constant.formater.format(Constant.SETTING_MINIMUM_ORDER_AMOUNT), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, getString(R.string.msg_sold_out), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnShowNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return root;
    }

    private void GetOfflineCart() {
        progressBar.setVisibility(View.VISIBLE);
        if (databaseHelper.getTotalItemOfCart(activity) >= 1) {
            offlineCarts = new ArrayList<OfflineCart>();
            offlineCartAdapter = null;
            Map<String, String> params = new HashMap<>();
            params.put(Constant.GET_CART_OFFLINE, Constant.GetVal);
            params.put(Constant.VARIANT_IDs, databaseHelper.getCartList().toString().replace("[", "").replace("]", "").replace("\"", ""));

            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {

                    if (result) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean(Constant.ERROR)) {
                                session.setData(Constant.TOTAL, jsonObject.getString(Constant.TOTAL));

                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                                Gson g = new Gson();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                    if (jsonObject1 != null) {
                                        OfflineCart cart = g.fromJson(jsonObject1.toString(), OfflineCart.class);
                                        offlineCarts.add(cart);
                                    } else {
                                        break;
                                    }
                                }
                                offlineCartAdapter = new OfflineCartAdapter(getContext(), getActivity(), offlineCarts);
                                offlineCartAdapter.setHasStableIds(true);
                                cartrecycleview.setAdapter(offlineCartAdapter);
                                lytTotal.setVisibility(View.VISIBLE);

                                progressBar.setVisibility(View.GONE);
                            } else {
                                cartrecycleview.setVisibility(View.GONE);
                                lytempty.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                }
            }, getActivity(), Constant.GET_OFFLINE_CART_URL, params, false);
        } else {
            progressBar.setVisibility(View.GONE);
            cartrecycleview.setVisibility(View.GONE);
            lytempty.setVisibility(View.VISIBLE);
        }
    }

    private void getCartData() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_USER_CART, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            Gson g = new Gson();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                if (jsonObject1 != null) {
                                    try {
                                        Cart cart = g.fromJson(jsonObject1.toString(), Cart.class);

                                        float price;
                                        int qty = Integer.parseInt(cart.getQty());
                                        String taxPercentage = cart.getItems().get(0).getTax_percentage();

                                        if (cart.getItems().get(0).getDiscounted_price().equals("0") || cart.getItems().get(0).getDiscounted_price().equals("")) {
                                            price = ((Float.parseFloat(cart.getItems().get(0).getPrice()) + ((Float.parseFloat(cart.getItems().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
                                        } else {
                                            price = ((Float.parseFloat(cart.getItems().get(0).getDiscounted_price()) + ((Float.parseFloat(cart.getItems().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
                                        }
                                        Constant.FLOAT_TOTAL_AMOUNT += (price * qty);
                                        carts.add(cart);
                                    } catch (Exception e) {

                                    }
                                } else {
                                    break;
                                }
                            }
                            cartAdapter = new CartAdapter(getContext(), getActivity(), carts);
                            cartAdapter.setHasStableIds(true);
                            cartrecycleview.setAdapter(cartAdapter);
                            lytTotal.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            total = Double.parseDouble(objectbject.getString(Constant.TOTAL));
                            session.setData(Constant.TOTAL, String.valueOf(total));
                            Constant.TOTAL_CART_ITEM = Integer.parseInt(objectbject.getString(Constant.TOTAL));
                            SetData();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            lytempty.setVisibility(View.VISIBLE);
                            lytTotal.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);

                    }
                }
            }
        }, getActivity(), Constant.CART_URL, params, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (values.size() > 0) {
            ApiConfig.AddMultipleProductInCart(session, getActivity(), values);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.cart);
        activity.invalidateOptionsMenu();
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.toolbar_cart).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

}