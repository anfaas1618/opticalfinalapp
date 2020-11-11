package wrteam.ekart.shop.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.adapter.ProductLoadMoreAdapter;
import wrteam.ekart.shop.adapter.SubCategoryAdapter;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.AppController;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.VolleyCallback;
import wrteam.ekart.shop.model.Category;
import wrteam.ekart.shop.model.Product;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static wrteam.ekart.shop.helper.ApiConfig.AddMultipleProductInCart;
import static wrteam.ekart.shop.helper.ApiConfig.GetSettings;


public class ProductListFragment extends Fragment {
    public static ArrayList<Product> productArrayList;
    public static ArrayList<Category> categoryArrayList;
    public static ProductLoadMoreAdapter mAdapter;
    View root;
    Session session;
    int total;
    int position;
    NestedScrollView nestedScrollView;
    boolean isSort = true;
    Activity activity;
    ProgressBar progressBar;
    int offset = 0;
    String id, filterBy, from;
    RecyclerView recyclerView, subCategoryrecycleview;
    SwipeRefreshLayout swipeLayout;
    int filterIndex;
    TextView tvAlert;
    boolean isLoadMore = false;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_product_list, container, false);
        setHasOptionsMenu(true);
        offset = 0;
        activity = getActivity();

        session = new Session(activity);

        from = getArguments().getString("from");
        id = getArguments().getString("id");

        swipeLayout = root.findViewById(R.id.swipeLayout);
        tvAlert = root.findViewById(R.id.tvAlert);
        nestedScrollView = root.findViewById(R.id.nestedScrollView);
        progressBar = root.findViewById(R.id.progressBar);
        subCategoryrecycleview = root.findViewById(R.id.subCategoryrecycleview);
        subCategoryrecycleview.setLayoutManager(new GridLayoutManager(getContext(), Constant.GRIDCOLUMN));

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));


        GetSettings(activity);

        filterIndex = -1;

        if (from.equals("regular")) {
            if (AppController.isConnected(activity)) {
                GetData();
            }
        } else if (from.equals("category")) {
            GetCategory();
            GetProducts();
        } else if (from.equals("similar")) {
            isSort = false;
            if (AppController.isConnected(activity)) {
                GetSimilarData();
            }
        } else if (from.equals("section")) {
            if (AppController.isConnected(activity)) {
                isSort = false;

                position = getArguments().getInt("position", 0);
                productArrayList = HomeFragment.sectionList.get(position).getProductList();
                mAdapter = new ProductLoadMoreAdapter(activity, productArrayList, R.layout.lyt_item_list);
                recyclerView.setAdapter(mAdapter);
            }

        }

        swipeLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(false);
                offset = 0;
                if (from.equals("regular")) {
                    GetData();
                } else if (from.equals("category")) {
                    GetCategory();
                    GetProducts();
                } else {
                    GetSimilarData();
                }

            }
        });

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.toolbar_sort) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(activity.getResources().getString(R.string.filterby));
            builder.setSingleChoiceItems(Constant.filtervalues, filterIndex, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    filterIndex = item;
                    switch (item) {
                        case 0:
                            filterBy = Constant.NEW;
                            break;
                        case 1:
                            filterBy = Constant.OLD;
                            break;
                        case 2:
                            filterBy = Constant.HIGH;
                            break;
                        case 3:
                            filterBy = Constant.LOW;
                            break;
                    }
                    if (item != -1)
                        ReLoadData();
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_search).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(isSort);
        menu.findItem(R.id.toolbar_cart).setIcon(ApiConfig.buildCounterDrawable(Constant.TOTAL_CART_ITEM, R.drawable.ic_cart, activity));
    }

    void GetData() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SUB_CATEGORY_ID, id);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
        params.put(Constant.OFFSET, "" + offset);
        if (filterIndex != -1) {
            params.put(Constant.SORT, filterBy);
        }

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            total = Integer.parseInt(objectbject.getString(Constant.TOTAL));
                            if (offset == 0) {
                                productArrayList = new ArrayList<>();
                                tvAlert.setVisibility(View.GONE);
                            }
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            productArrayList.addAll(ApiConfig.GetProductList(jsonArray));
                            if (offset == 0) {
                                mAdapter = new ProductLoadMoreAdapter(activity, productArrayList, R.layout.lyt_item_list);
                                mAdapter.setHasStableIds(true);
                                recyclerView.setAdapter(mAdapter);

                                progressBar.setVisibility(View.GONE);
                                nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                        // if (diff == 0) {
                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                            if (productArrayList.size() < total) {
                                                if (!isLoadMore) {
                                                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productArrayList.size() - 1) {
                                                        //bottom of list!
                                                        productArrayList.add(null);
                                                        mAdapter.notifyItemInserted(productArrayList.size() - 1);

                                                        offset += Integer.parseInt("" + Constant.LOAD_ITEM_LIMIT);
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put(Constant.SUB_CATEGORY_ID, id);
                                                        params.put(Constant.USER_ID, session.getData(Constant.ID));
                                                        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
                                                        params.put(Constant.OFFSET, offset + "");
                                                        if (filterIndex != -1)
                                                            params.put(Constant.SORT, filterBy);

                                                        ApiConfig.RequestToVolley(new VolleyCallback() {
                                                            @Override
                                                            public void onSuccess(boolean result, String response) {

                                                                if (result) {
                                                                    try {
                                                                        JSONObject objectbject = new JSONObject(response);
                                                                        if (!objectbject.getBoolean(Constant.ERROR)) {

                                                                            JSONObject object = new JSONObject(response);
                                                                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                                                                            productArrayList.remove(productArrayList.size() - 1);
                                                                            mAdapter.notifyItemRemoved(productArrayList.size());

                                                                            productArrayList.addAll(ApiConfig.GetProductList(jsonArray));
                                                                            mAdapter.notifyDataSetChanged();
                                                                            mAdapter.setLoaded();
                                                                            isLoadMore = false;
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                        }, activity, Constant.GET_PRODUCT_BY_SUB_CATE, params, false);
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

                                progressBar.setVisibility(View.GONE);
                                tvAlert.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {

                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.GET_PRODUCT_BY_SUB_CATE, params, false);
    }

    void GetSimilarData() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_SIMILAR_PRODUCT, Constant.GetVal);
        params.put(Constant.PRODUCT_ID, id);
        params.put(Constant.CATEGORY_ID, getArguments().getString("cat_id"));
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
        params.put(Constant.OFFSET, "" + offset);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            total = Integer.parseInt(objectbject.getString(Constant.TOTAL));
                            if (offset == 0) {
                                productArrayList = new ArrayList<>();
                                tvAlert.setVisibility(View.GONE);
                            }
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            productArrayList.addAll(ApiConfig.GetProductList(jsonArray));
                            if (offset == 0) {
                                mAdapter = new ProductLoadMoreAdapter(activity, productArrayList, R.layout.lyt_item_list);
                                mAdapter.setHasStableIds(true);
                                recyclerView.setAdapter(mAdapter);

                                progressBar.setVisibility(View.GONE);
                                nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                        // if (diff == 0) {
                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                            if (productArrayList.size() < total) {
                                                if (!isLoadMore) {
                                                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productArrayList.size() - 1) {
                                                        //bottom of list!
                                                        productArrayList.add(null);
                                                        mAdapter.notifyItemInserted(productArrayList.size() - 1);

                                                        offset += Integer.parseInt("" + Constant.LOAD_ITEM_LIMIT);
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put(Constant.GET_SIMILAR_PRODUCT, Constant.GetVal);
                                                        params.put(Constant.PRODUCT_ID, id);
                                                        params.put(Constant.CATEGORY_ID, getArguments().getString("cat_id"));
                                                        params.put(Constant.USER_ID, session.getData(Constant.ID));
                                                        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
                                                        params.put(Constant.OFFSET, "" + offset);

                                                        ApiConfig.RequestToVolley(new VolleyCallback() {
                                                            @Override
                                                            public void onSuccess(boolean result, String response) {

                                                                if (result) {
                                                                    try {
                                                                        JSONObject objectbject = new JSONObject(response);
                                                                        if (!objectbject.getBoolean(Constant.ERROR)) {

                                                                            JSONObject object = new JSONObject(response);
                                                                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                                                                            productArrayList.remove(productArrayList.size() - 1);
                                                                            mAdapter.notifyItemRemoved(productArrayList.size());

                                                                            productArrayList.addAll(ApiConfig.GetProductList(jsonArray));
                                                                            mAdapter.notifyDataSetChanged();
                                                                            mAdapter.setLoaded();
                                                                            isLoadMore = false;
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                        }, activity, Constant.GET_SIMILAR_PRODUCT_URL, params, false);
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
                                progressBar.setVisibility(View.GONE);
                                tvAlert.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {

                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.GET_SIMILAR_PRODUCT_URL, params, false);
    }

    void GetCategory() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.CATEGORY_ID, id);

        categoryArrayList = new ArrayList<>();
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {

                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            Gson gson = new Gson();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Category category = new Category();
                                category.setId(jsonObject.getString(Constant.ID));
                                category.setCategory_id(jsonObject.getString(Constant.CATEGORY_ID));
                                category.setName(jsonObject.getString(Constant.NAME));
                                category.setSlug(jsonObject.getString(Constant.SLUG));
                                category.setSubtitle(jsonObject.getString(Constant.SUBTITLE));
                                category.setImage(jsonObject.getString(Constant.IMAGE));
                                categoryArrayList.add(category);
                            }
                            subCategoryrecycleview.setAdapter(new SubCategoryAdapter(getContext(), activity, categoryArrayList, R.layout.lyt_subcategory, "sub_cate"));

                            progressBar.setVisibility(View.GONE);
                        } else {
                            tvAlert.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.SubcategoryUrl, params, false);
    }

    void GetProducts() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.CATEGORY_ID, id);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
        params.put(Constant.OFFSET, "" + offset);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            total = Integer.parseInt(objectbject.getString(Constant.TOTAL));
                            if (offset == 0) {
                                productArrayList = new ArrayList<>();
                            }
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            productArrayList.addAll(ApiConfig.GetProductList(jsonArray));
                            if (offset == 0) {
                                mAdapter = new ProductLoadMoreAdapter(activity, productArrayList, R.layout.lyt_item_list);
                                mAdapter.setHasStableIds(true);
                                recyclerView.setAdapter(mAdapter);

                                progressBar.setVisibility(View.GONE);
                                nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                        // if (diff == 0) {
                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                            if (productArrayList.size() < total) {
                                                if (!isLoadMore) {
                                                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productArrayList.size() - 1) {
                                                        //bottom of list!
                                                        productArrayList.add(null);
                                                        mAdapter.notifyItemInserted(productArrayList.size() - 1);

                                                        offset += Integer.parseInt("" + Constant.LOAD_ITEM_LIMIT);
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put(Constant.CATEGORY_ID, id);
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
                                                                            productArrayList.remove(productArrayList.size() - 1);
                                                                            mAdapter.notifyItemRemoved(productArrayList.size());

                                                                            productArrayList.addAll(ApiConfig.GetProductList(jsonArray));
                                                                            mAdapter.notifyDataSetChanged();
                                                                            mAdapter.setLoaded();
                                                                            isLoadMore = false;
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                        }, activity, Constant.GET_PRODUCT_BY_CATE, params, false);
                                                        isLoadMore = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {

                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.GET_PRODUCT_BY_CATE, params, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getArguments().getString("name");
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
    public void onPause() {
        super.onPause();
        AddMultipleProductInCart(session, activity, Constant.CartValues);
    }

    void ReLoadData() {
        if (AppController.isConnected(activity)) {
            GetData();
        }
    }

}