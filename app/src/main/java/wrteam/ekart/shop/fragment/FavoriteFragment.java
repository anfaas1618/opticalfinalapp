package wrteam.ekart.shop.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.adapter.FavoriteLoadMoreAdapter;
import wrteam.ekart.shop.adapter.OfflineFavoriteAdapter;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.DatabaseHelper;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.VolleyCallback;
import wrteam.ekart.shop.model.Favorite;
import wrteam.ekart.shop.model.PriceVariation;
import wrteam.ekart.shop.model.Product;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static wrteam.ekart.shop.helper.ApiConfig.GetSettings;


public class FavoriteFragment extends Fragment {
    public static ArrayList<Favorite> favoriteArrayList;
    public static ArrayList<Product> productArrayList;
    public static FavoriteLoadMoreAdapter favoriteLoadMoreAdapter;
    public static OfflineFavoriteAdapter offlineFavoriteAdapter;
    public static RecyclerView recyclerView;
    public static RelativeLayout tvAlert;
    View root;
    Session session;
    int total;
    NestedScrollView nestedScrollView;
    Activity activity;
    boolean isLogin;
    DatabaseHelper databaseHelper;
    int offset = 0;
    SwipeRefreshLayout swipeLayout;
    boolean isLoadMore = false;
    boolean isGrid = false;
    int resource;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_favorite, container, false);
        setHasOptionsMenu(true);

        activity = getActivity();
        session = new Session(activity);

        resource = R.layout.lyt_item_list;

        isLogin = session.isUserLoggedIn();
        databaseHelper = new DatabaseHelper(activity);

        swipeLayout = root.findViewById(R.id.swipeLayout);

        tvAlert = root.findViewById(R.id.tvAlert);
        nestedScrollView = root.findViewById(R.id.nestedScrollView);

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        GetSettings(activity);

        if (ApiConfig.isConnected(activity)) {
            if (isLogin) {
                ApiConfig.getWalletBalance(activity, new Session(activity));
                GetData();
            } else {
                GetOfflineData();
            }
        }

        swipeLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ApiConfig.isConnected(activity)) {

                    if (new Session(activity).isUserLoggedIn()) {
                        ApiConfig.getWalletBalance(activity, new Session(activity));
                    }
                    if (isLogin) {
                        if (Constant.CartValues.size() > 0) {
                            ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                        }
                        offset = 0;
                        GetData();
                    } else {
                        GetOfflineData();
                    }
                }
                swipeLayout.setRefreshing(false);
            }
        });

        return root;
    }

    void GetData() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_FAVORITES, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
        params.put(Constant.OFFSET, offset + "");

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            total = Integer.parseInt(objectbject.getString(Constant.TOTAL));
                            if (offset == 0) {
                                favoriteArrayList = new ArrayList<>();
                                recyclerView.setVisibility(View.VISIBLE);
                                tvAlert.setVisibility(View.GONE);
                            }
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        ArrayList<PriceVariation> priceVariations = new ArrayList<>();
                                        JSONArray pricearray = jsonObject.getJSONArray(Constant.VARIANT);

                                        for (int j = 0; j < pricearray.length(); j++) {
                                            JSONObject obj = pricearray.getJSONObject(j);
                                            String discountpercent = "0", productPrice = " ";
                                            if (obj.getString(Constant.DISCOUNTED_PRICE).equals("0"))
                                                productPrice = obj.getString(Constant.PRICE);
                                            else {
                                                discountpercent = ApiConfig.GetDiscount(obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE));
                                                productPrice = obj.getString(Constant.DISCOUNTED_PRICE);
                                            }
                                            priceVariations.add(new PriceVariation(obj.getString(Constant.CART_ITEM_COUNT), obj.getString(Constant.ID), obj.getString(Constant.PRODUCT_ID), obj.getString(Constant.TYPE), obj.getString(Constant.MEASUREMENT), obj.getString(Constant.MEASUREMENT_UNIT_ID), productPrice, obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE), obj.getString(Constant.SERVE_FOR), obj.getString(Constant.STOCK), obj.getString(Constant.STOCK_UNIT_ID), obj.getString(Constant.MEASUREMENT_UNIT_NAME), obj.getString(Constant.STOCK_UNIT_NAME), discountpercent));
                                        }
                                        productArrayList.add(new Favorite(jsonObject.getString(Constant.PRODUCT_ID), jsonObject.getString(Constant.CANCELLABLE_STATUS), jsonObject.getString(Constant.TILL_STATUS), jsonObject.getString(Constant.MANUFACTURER), jsonObject.getString(Constant.MADE_IN), jsonObject.getString(Constant.RETURN_STATUS), jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.SLUG), jsonObject.getString(Constant.SUC_CATE_ID), jsonObject.getString(Constant.IMAGE), jsonObject.getJSONArray(Constant.OTHER_IMAGES), jsonObject.getString(Constant.DESCRIPTION), jsonObject.getString(Constant.STATUS), jsonObject.getString(Constant.DATE_ADDED), jsonObject.getBoolean(Constant.IS_FAVORITE), jsonObject.getString(Constant.CATEGORY_ID), priceVariations, jsonObject.getString(Constant.INDICATOR)));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (offset == 0) {
                                favoriteLoadMoreAdapter = new FavoriteLoadMoreAdapter(getContext(), favoriteArrayList, resource);
                                favoriteLoadMoreAdapter.setHasStableIds(true);
                                recyclerView.setAdapter(favoriteLoadMoreAdapter);
                                nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                        // if (diff == 0) {
                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                            if (favoriteArrayList.size() < total) {
                                                if (!isLoadMore) {
                                                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == favoriteArrayList.size() - 1) {
                                                        //bottom of list!
                                                        favoriteArrayList.add(null);
                                                        favoriteLoadMoreAdapter.notifyItemInserted(favoriteArrayList.size() - 1);

                                                        offset = offset + Constant.LOAD_ITEM_LIMIT;
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put(Constant.GET_FAVORITES, Constant.GetVal);
                                                        params.put(Constant.USER_ID, session.getData(Constant.ID));
                                                        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
                                                        params.put(Constant.OFFSET, offset + "");

                                                        ApiConfig.RequestToVolley(new VolleyCallback() {
                                                            @Override
                                                            public void onSuccess(boolean result, String response) {

                                                                if (result) {
                                                                    try {
                                                                        JSONObject objectbject = new JSONObject(response);
                                                                        if (!objectbject.getBoolean(Constant.ERROR)) {

                                                                            JSONObject object = new JSONObject(response);
                                                                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                                                                            favoriteArrayList.remove(favoriteArrayList.size() - 1);
                                                                            favoriteLoadMoreAdapter.notifyItemRemoved(favoriteArrayList.size());

                                                                            try {
                                                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                                                    try {
                                                                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                                                        ArrayList<PriceVariation> priceVariations = new ArrayList<>();
                                                                                        JSONArray pricearray = jsonObject.getJSONArray(Constant.VARIANT);

                                                                                        for (int j = 0; j < pricearray.length(); j++) {
                                                                                            JSONObject obj = pricearray.getJSONObject(j);
                                                                                            String discountpercent = "0", productPrice = " ";
                                                                                            if (obj.getString(Constant.DISCOUNTED_PRICE).equals("0"))
                                                                                                productPrice = obj.getString(Constant.PRICE);
                                                                                            else {
                                                                                                discountpercent = ApiConfig.GetDiscount(obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE));
                                                                                                productPrice = obj.getString(Constant.DISCOUNTED_PRICE);
                                                                                            }
                                                                                            priceVariations.add(new PriceVariation(obj.getString(Constant.CART_ITEM_COUNT), obj.getString(Constant.ID), obj.getString(Constant.PRODUCT_ID), obj.getString(Constant.TYPE), obj.getString(Constant.MEASUREMENT), obj.getString(Constant.MEASUREMENT_UNIT_ID), productPrice, obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE), obj.getString(Constant.SERVE_FOR), obj.getString(Constant.STOCK), obj.getString(Constant.STOCK_UNIT_ID), obj.getString(Constant.MEASUREMENT_UNIT_NAME), obj.getString(Constant.STOCK_UNIT_NAME), discountpercent));
                                                                                        }
                                                                                        productArrayList.add(new Favorite(jsonObject.getString(Constant.PRODUCT_ID), jsonObject.getString(Constant.CANCELLABLE_STATUS), jsonObject.getString(Constant.TILL_STATUS), jsonObject.getString(Constant.MANUFACTURER), jsonObject.getString(Constant.MADE_IN), jsonObject.getString(Constant.RETURN_STATUS), jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.SLUG), jsonObject.getString(Constant.SUC_CATE_ID), jsonObject.getString(Constant.IMAGE), jsonObject.getJSONArray(Constant.OTHER_IMAGES), jsonObject.getString(Constant.DESCRIPTION), jsonObject.getString(Constant.STATUS), jsonObject.getString(Constant.DATE_ADDED), jsonObject.getBoolean(Constant.IS_FAVORITE), jsonObject.getString(Constant.CATEGORY_ID), priceVariations, jsonObject.getString(Constant.INDICATOR)));
                                                                                    } catch (JSONException e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                }
                                                                            } catch (Exception e) {
                                                                                e.printStackTrace();
                                                                            }

                                                                            favoriteLoadMoreAdapter.notifyDataSetChanged();
                                                                            favoriteLoadMoreAdapter.setLoaded();
                                                                            isLoadMore = false;
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                        }, activity, Constant.GET_FAVORITES_URL, params, false);
                                                        isLoadMore = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            if (offset == 0) {
                                recyclerView.setVisibility(View.GONE);
                                tvAlert.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.GET_FAVORITES_URL, params, true);
    }


    void GetOfflineData() {

        if (databaseHelper.getFavourite().size() >= 1) {
            Map<String, String> params = new HashMap<>();
            params.put(Constant.GET_FAVORITES_OFFLINE, Constant.GetVal);
            params.put(Constant.PRODUCT_IDs, databaseHelper.getFavourite().toString().replace("[", "").replace("]", "").replace("\"", ""));

            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    if (result) {
                        try {
                            JSONObject objectbject = new JSONObject(response);
                            if (!objectbject.getBoolean(Constant.ERROR)) {
                                JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);
                                try {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        try {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            if (jsonObject != null) {
                                                ArrayList<PriceVariation> priceVariations = new ArrayList<>();
                                                JSONArray pricearray = jsonObject.getJSONArray(Constant.VARIANT);

                                                for (int j = 0; j < pricearray.length(); j++) {
                                                    JSONObject obj = pricearray.getJSONObject(j);
                                                    String discountpercent = "0", productPrice = " ";
                                                    if (obj.getString(Constant.DISCOUNTED_PRICE).equals("0"))
                                                        productPrice = obj.getString(Constant.PRICE);
                                                    else {
                                                        discountpercent = ApiConfig.GetDiscount(obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE));
                                                        productPrice = obj.getString(Constant.DISCOUNTED_PRICE);
                                                    }
                                                    priceVariations.add(new PriceVariation(obj.getString(Constant.CART_ITEM_COUNT), obj.getString(Constant.ID), obj.getString(Constant.PRODUCT_ID), obj.getString(Constant.TYPE), obj.getString(Constant.MEASUREMENT), obj.getString(Constant.MEASUREMENT_UNIT_ID), productPrice, obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE), obj.getString(Constant.SERVE_FOR), obj.getString(Constant.STOCK), obj.getString(Constant.STOCK_UNIT_ID), obj.getString(Constant.MEASUREMENT_UNIT_NAME), obj.getString(Constant.STOCK_UNIT_NAME), discountpercent));
                                                }
                                                productArrayList.add(new Product(jsonObject.getString(Constant.TAX_PERCENT), jsonObject.getString(Constant.ROW_ORDER), jsonObject.getString(Constant.TILL_STATUS), jsonObject.getString(Constant.CANCELLABLE_STATUS), jsonObject.getString(Constant.MANUFACTURER), jsonObject.getString(Constant.MADE_IN), jsonObject.getString(Constant.RETURN_STATUS), jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.SLUG), jsonObject.getString(Constant.SUC_CATE_ID), jsonObject.getString(Constant.IMAGE), jsonObject.getJSONArray(Constant.OTHER_IMAGES), jsonObject.getString(Constant.DESCRIPTION), jsonObject.getString(Constant.STATUS), jsonObject.getString(Constant.DATE_ADDED), jsonObject.getBoolean(Constant.IS_FAVORITE), jsonObject.getString(Constant.CATEGORY_ID), priceVariations, jsonObject.getString(Constant.INDICATOR)));
                                            } else {
                                                break;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                recyclerView.setVisibility(View.GONE);
                                tvAlert.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, activity, Constant.GET_OFFLINE_FAVORITES_URL, params, true);
        } else {
            recyclerView.setVisibility(View.GONE);
            tvAlert.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.toolbar_layout) {
            if (isGrid) {
                isGrid = false;
                recyclerView.setAdapter(null);
                resource = R.layout.lyt_item_list;
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            } else {
                isGrid = true;
                recyclerView.setAdapter(null);
                resource = R.layout.lyt_item_grid;
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            }
            if (session.isUserLoggedIn()) {
                favoriteLoadMoreAdapter = new FavoriteLoadMoreAdapter(getContext(), favoriteArrayList, resource);
                recyclerView.setAdapter(favoriteLoadMoreAdapter);
                favoriteLoadMoreAdapter.notifyDataSetChanged();
            } else {
                offlineFavoriteAdapter = new OfflineFavoriteAdapter(getContext(), productArrayList, resource);
                recyclerView.setAdapter(offlineFavoriteAdapter);
                offlineFavoriteAdapter.notifyDataSetChanged();
            }
            activity.invalidateOptionsMenu();
            return true;
        }
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.title_fav);
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_layout).setVisible(true);
        menu.findItem(R.id.toolbar_search).setVisible(true);

        Drawable myDrawable = null;
        if (isGrid) {
            myDrawable = getResources().getDrawable(R.drawable.ic_list); // The ID of your drawable.
        } else {
            myDrawable = getResources().getDrawable(R.drawable.ic_grid); // The ID of your drawable.
        }
        menu.findItem(R.id.toolbar_layout).setIcon(myDrawable);
        super.onPrepareOptionsMenu(menu);
    }
}