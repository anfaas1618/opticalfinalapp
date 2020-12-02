package wrteam.ekart.shop.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.adapter.ProductAdapter;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.VolleyCallback;
import wrteam.ekart.shop.model.Product;


public class SearchFragment extends Fragment {
    public static ArrayList<Product> productArrayList;
    public static ProductAdapter productAdapter;
    public ProgressBar progressBar;
    View root;
    RecyclerView recycleview;
    TextView noResult, msg;
    Session session;
    Activity activity;
    SearchView searchview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);
        activity = getActivity();


        setHasOptionsMenu(true);

        recycleview = root.findViewById(R.id.recycleview);
        productArrayList = new ArrayList<>();
        noResult = root.findViewById(R.id.noResult);
        msg = root.findViewById(R.id.msg);
        progressBar = root.findViewById(R.id.pBar);
        searchview = root.findViewById(R.id.searchview);
        progressBar.setVisibility(View.GONE);
        session = new Session(getContext());

        Constant.CartValues = new HashMap<>();

        recycleview.setLayoutManager(new LinearLayoutManager(getContext()));
        
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    SearchRequest(newText);
                } else {
                    if (activity != null && productArrayList.size() > 0) {
                        productArrayList.clear();
                        productAdapter.notifyDataSetChanged();
                    }
                }
                if (Constant.CartValues.size() > 0) {
                    ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                }
                return false;
            }
        });

        return root;
    }


    public void SearchRequest(final String query) {  //json request for product search
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.TYPE, Constant.PRODUCT_SEARCH);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.SEARCH, query);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        productArrayList = new ArrayList<>();
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {

                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                            productArrayList = ApiConfig.GetProductList(jsonArray);
                            productAdapter = new ProductAdapter(productArrayList, R.layout.lyt_item_list, activity);
                            recycleview.setAdapter(productAdapter);
                            noResult.setVisibility(View.GONE);
                            msg.setVisibility(View.GONE);
                        } else {
                            noResult.setVisibility(View.VISIBLE);
                            msg.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            productArrayList.clear();
                            recycleview.setAdapter(new ProductAdapter(productArrayList, R.layout.lyt_item_list, activity));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.PRODUCT_SEARCH_URL, params, false);

    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.search);
        activity.invalidateOptionsMenu();

        searchview.setIconifiedByDefault(true);
        searchview.setFocusable(true);
        searchview.setIconified(false);
        searchview.requestFocusFromTouch();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Constant.CartValues.size() > 0) {
            ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
        }
    }
}