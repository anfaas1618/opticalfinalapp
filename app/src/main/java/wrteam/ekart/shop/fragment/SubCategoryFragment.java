package wrteam.ekart.shop.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import wrteam.ekart.shop.adapter.SubCategoryAdapter;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.AppController;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.VolleyCallback;
import wrteam.ekart.shop.model.Category;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class SubCategoryFragment extends Fragment {

    public static ArrayList<Category> categoryArrayList;
    TextView txtnodata;
    RecyclerView subCategoryrecycleview;
    SwipeRefreshLayout swipeLayout;
    View root;
    String cateId;
    Session session;
    Activity activity;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_sub_category, container, false);
        activity = getActivity();


        cateId = getArguments().getString("id");

        session = new Session(activity);

        setHasOptionsMenu(true);

        progressBar = root.findViewById(R.id.progressBar);
        subCategoryrecycleview = root.findViewById(R.id.subCategoryrecycleview);
        subCategoryrecycleview.setLayoutManager(new GridLayoutManager(getContext(), Constant.GRIDCOLUMN));

        swipeLayout = root.findViewById(R.id.swipeLayout);
        txtnodata = root.findViewById(R.id.txtnodata);

        if (AppController.isConnected(activity)) {
            GetCategory();
        }

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetCategory();
                swipeLayout.setRefreshing(false);
            }
        });

        return root;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_cart).setIcon(ApiConfig.buildCounterDrawable(Constant.TOTAL_CART_ITEM, R.drawable.ic_cart, activity));

        activity.invalidateOptionsMenu();
    }

    private void GetCategory() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.CATEGORY_ID, cateId);

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
                            subCategoryrecycleview.setAdapter(new SubCategoryAdapter(getContext(), activity, categoryArrayList, R.layout.lyt_category, "sub_cate"));

                            progressBar.setVisibility(View.GONE);
                        } else {
                            txtnodata.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            txtnodata.setText(object.getString(Constant.MESSAGE));
                        }
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.SubcategoryUrl, params, false);
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
}