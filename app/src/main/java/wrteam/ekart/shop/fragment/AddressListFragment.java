package wrteam.ekart.shop.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.MainActivity;
import wrteam.ekart.shop.adapter.AddressAdapter;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.AppController;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.VolleyCallback;
import wrteam.ekart.shop.model.Address;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class AddressListFragment extends Fragment {
    public static RecyclerView recyclerView;
    public static ArrayList<Address> addresses;
    public static AddressAdapter addressAdapter;
    public static TextView tvAlert;
    public static String selectedAddress = "";
    public static Activity activity;
    public NestedScrollView nestedScrollView;
    public int total = 0;
    FloatingActionButton fabAddAddress;
    View root;
    SwipeRefreshLayout swipeLayout;
    LinearLayoutManager linearLayoutManager;
    TextView txttotalitems, tvSubTotal, tvConfirmOrder, tvUpdate, tvCurrent;
    LinearLayout lytCLocation, processLyt;
    RelativeLayout confirmLyt;
    int offset = 0;
    private Session session;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_address_list, container, false);
        activity = getActivity();
        session = new Session(activity);

        recyclerView = root.findViewById(R.id.recyclerView);
        swipeLayout = root.findViewById(R.id.swipeLayout);
        nestedScrollView = root.findViewById(R.id.nestedScrollView);
        tvConfirmOrder = root.findViewById(R.id.tvConfirmOrder);
        tvAlert = root.findViewById(R.id.tvAlert);
        lytCLocation = root.findViewById(R.id.lytCLocation);
        fabAddAddress = root.findViewById(R.id.fabAddAddress);
        processLyt = root.findViewById(R.id.processLyt);
        tvUpdate = root.findViewById(R.id.tvUpdate);
        tvCurrent = root.findViewById(R.id.tvCurrent);
        tvSubTotal = root.findViewById(R.id.tvSubTotal);
        txttotalitems = root.findViewById(R.id.txttotalitems);
        confirmLyt = root.findViewById(R.id.confirmLyt);
        linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.getItemAnimator().setChangeDuration(0);

        if (AppController.isConnected(activity)) {
            offset = 0;
            getAddresses();
        }

        if (getArguments().getString(Constant.FROM).equalsIgnoreCase("process")) {
            tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + Constant.formater.format(getArguments().getDouble("total")));
            txttotalitems.setText(Constant.TOTAL_CART_ITEM + " Items");
            tvConfirmOrder.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
                    if (!selectedAddress.isEmpty()) {
                        Fragment fragment = new CheckoutFragment();
                        Bundle bundle = new Bundle();
                        bundle.putDouble("dCharge", Constant.SETTING_DELIVERY_CHARGE);
                        bundle.putString("address", selectedAddress);
                        fragment.setArguments(bundle);
                        MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    } else {
                        Toast.makeText(activity, R.string.select_delivery_address, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            processLyt.setVisibility(View.GONE);
            confirmLyt.setVisibility(View.GONE);
        }

        setHasOptionsMenu(true);

        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                addresses.clear();
                addressAdapter = null;
                offset = 0;
                getAddresses();
                swipeLayout.setRefreshing(false);
            }
        });

        fabAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewAddress();
            }
        });

        return root;
    }

    public void addNewAddress() {
        Fragment fragment = new AddressAddUpdateFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", "");
        bundle.putString("for", "add");
        bundle.putInt("position", 0);

        fragment.setArguments(bundle);
        MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
    }

    public void getAddresses() {
        addresses = new ArrayList<>();
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_ADDRESSES, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        Constant.selectedAddressId = "";
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            total = Integer.parseInt(jsonObject.getString(Constant.TOTAL));
                            session.setData(Constant.TOTAL, String.valueOf(total));
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            //System.out.println("=====res addresss   " + response);
                            Gson g = new Gson();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                if (jsonObject1 != null) {
                                    Address address = g.fromJson(jsonObject1.toString(), Address.class);
                                    if (address.getIs_default().equals("1")) {
                                        session.setData(Constant.LONGITUDE, address.getLongitude());
                                        session.setData(Constant.LATITUDE, address.getLatitude());
                                        Constant.selectedAddressId = address.getId();
                                        if (Constant.SETTING_AREA_WISE_DELIVERY_CHARGE == 1) {
                                            Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY = Double.parseDouble(address.getMinimum_free_delivery_order_amount());
                                            Constant.SETTING_DELIVERY_CHARGE = Double.parseDouble(address.getDelivery_charges());
                                        }
                                    }
                                    addresses.add(address);
                                } else {
                                    break;
                                }

                            }
                            addressAdapter = new AddressAdapter(activity, addresses);
                            recyclerView.setAdapter(addressAdapter);
                            confirmLyt.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            tvAlert.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.GET_ADDRESS_URL, params, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.addresses);
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
    }
}