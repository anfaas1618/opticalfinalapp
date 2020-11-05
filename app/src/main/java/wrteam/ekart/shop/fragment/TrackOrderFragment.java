package wrteam.ekart.shop.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.LoginActivity;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.AppController;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.VolleyCallback;
import wrteam.ekart.shop.model.OrderTracker;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static wrteam.ekart.shop.activity.MainActivity.active;
import static wrteam.ekart.shop.activity.MainActivity.bottomNavigationView;
import static wrteam.ekart.shop.activity.MainActivity.fm;
import static wrteam.ekart.shop.activity.MainActivity.homeClicked;
import static wrteam.ekart.shop.activity.MainActivity.homeFragment;


public class TrackOrderFragment extends Fragment {
    public static ArrayList<OrderTracker> orderTrackerslist, cancelledlist, deliveredlist, processedlist, shippedlist, returnedList;
    View root;
    LinearLayout lytempty, lytdata;
    Session session;
    String[] tabs;
    TabLayout tabLayout;
    ViewPager viewPager;
    TrackOrderFragment.ViewPagerAdapter adapter;
    SwipeRefreshLayout swipeLayout;
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_track_order, container, false);

        activity = getActivity();

        session = new Session(activity);
        tabs = new String[]{getString(R.string.all), getString(R.string.in_process1), getString(R.string.shipped1), getString(R.string.delivered1), getString(R.string.cancelled1), getString(R.string.returned1)};
        lytempty = root.findViewById(R.id.lytempty);
        lytdata = root.findViewById(R.id.lytdata);

        viewPager = root.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(5);
        tabLayout = root.findViewById(R.id.tablayout);
        swipeLayout = root.findViewById(R.id.swipeLayout);
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        if (session.isUserLoggedIn()) {
            if (AppController.isConnected(activity)) {
                GetOrderDetails();
            }
        } else {
            startActivity(new Intent(activity, LoginActivity.class).putExtra("from", "tracker"));
        }

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppController.isConnected(activity)) {
                    swipeLayout.setRefreshing(false);
                    GetOrderDetails();
                }
            }
        });

        root.findViewById(R.id.btnorder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fm.beginTransaction().show(homeFragment).hide(active).commit();
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                homeClicked = true;
            }
        });

        return root;
    }

    private void GetOrderDetails() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GETORDERS, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));

        orderTrackerslist = new ArrayList<>();
        cancelledlist = new ArrayList<>();
        deliveredlist = new ArrayList<>();
        processedlist = new ArrayList<>();
        shippedlist = new ArrayList<>();
        returnedList = new ArrayList<>();
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String laststatusname = null, laststatusdate = null;
                                JSONArray statusarray = jsonObject.getJSONArray("status");
                                ArrayList<OrderTracker> statusarraylist = new ArrayList<>();

                                int cancel = 0, delivered = 0, process = 0, shipped = 0, returned = 0;
                                for (int k = 0; k < statusarray.length(); k++) {
                                    JSONArray sarray = statusarray.getJSONArray(k);
                                    String sname = sarray.getString(0);
                                    String sdate = sarray.getString(1);

                                    statusarraylist.add(new OrderTracker(sname, sdate));
                                    laststatusname = sname;
                                    laststatusdate = sdate;

                                    if (sname.equalsIgnoreCase("cancelled")) {
                                        cancel = 1;
                                        delivered = 0;
                                        process = 0;
                                        shipped = 0;
                                        returned = 0;
                                    } else if (sname.equalsIgnoreCase("delivered")) {
                                        delivered = 1;
                                        process = 0;
                                        shipped = 0;
                                        returned = 0;
                                    } else if (sname.equalsIgnoreCase("processed")) {
                                        process = 1;
                                        shipped = 0;
                                        returned = 0;
                                    } else if (sname.equalsIgnoreCase("shipped")) {
                                        shipped = 1;
                                        returned = 0;
                                    } else if (sname.equalsIgnoreCase("returned")) {

                                        returned = 1;
                                    }
                                }

                                ArrayList<OrderTracker> itemList = new ArrayList<>();
                                JSONArray itemsarray = jsonObject.getJSONArray("items");

                                for (int j = 0; j < itemsarray.length(); j++) {

                                    JSONObject itemobj = itemsarray.getJSONObject(j);
                                    double productPrice = 0.0;
                                    if (itemobj.getString(Constant.DISCOUNTED_PRICE).equals("0"))
                                        productPrice = (Double.parseDouble(itemobj.getString(Constant.PRICE)) * Integer.parseInt(itemobj.getString(Constant.QUANTITY)));
                                    else {
                                        productPrice = (Double.parseDouble(itemobj.getString(Constant.DISCOUNTED_PRICE)) * Integer.parseInt(itemobj.getString(Constant.QUANTITY)));
                                    }
                                    JSONArray statusarray1 = itemobj.getJSONArray("status");
                                    ArrayList<OrderTracker> statusList = new ArrayList<>();

                                    for (int k = 0; k < statusarray1.length(); k++) {
                                        JSONArray sarray = statusarray1.getJSONArray(k);
                                        String sname = sarray.getString(0);
                                        String sdate = sarray.getString(1);
                                        statusList.add(new OrderTracker(sname, sdate));
                                    }
                                    itemList.add(new OrderTracker(itemobj.getString(Constant.ID),
                                            itemobj.getString(Constant.ORDER_ID),
                                            itemobj.getString(Constant.PRODUCT_VARIANT_ID),
                                            itemobj.getString(Constant.QUANTITY),
                                            String.valueOf(productPrice),
                                            itemobj.getString(Constant.DISCOUNT),
                                            itemobj.getString(Constant.SUB_TOTAL),
                                            itemobj.getString(Constant.DELIVER_BY),
                                            itemobj.getString(Constant.NAME),
                                            itemobj.getString(Constant.IMAGE),
                                            itemobj.getString(Constant.MEASUREMENT),
                                            itemobj.getString(Constant.UNIT),
                                            jsonObject.getString(Constant.PAYMENT_METHOD),
                                            itemobj.getString(Constant.ACTIVE_STATUS),
                                            itemobj.getString(Constant.DATE_ADDED),
                                            statusList,
                                            itemobj.getString(Constant.RETURN_STATUS),
                                            itemobj.getString(Constant.CANCELLABLE_STATUS),
                                            itemobj.getString(Constant.TILL_STATUS)));
                                }
                                OrderTracker orderTracker = new OrderTracker(
                                        jsonObject.getString(Constant.OTP),
                                        jsonObject.getString(Constant.USER_ID),
                                        jsonObject.getString(Constant.ID),
                                        jsonObject.getString(Constant.DATE_ADDED),
                                        laststatusname, laststatusdate,
                                        statusarraylist,
                                        jsonObject.getString(Constant.MOBILE),
                                        jsonObject.getString(Constant.DELIVERY_CHARGE),
                                        jsonObject.getString(Constant.PAYMENT_METHOD),
                                        jsonObject.getString(Constant.ADDRESS),
                                        jsonObject.getString(Constant.TOTAL),
                                        jsonObject.getString(Constant.FINAL_TOTAL),
                                        jsonObject.getString(Constant.TAX_AMOUNT),
                                        jsonObject.getString(Constant.TAX_PERCENT),
                                        jsonObject.getString(Constant.KEY_WALLET_BALANCE),
                                        jsonObject.getString(Constant.PROMO_CODE),
                                        jsonObject.getString(Constant.PROMO_DISCOUNT),
                                        jsonObject.getString(Constant.DISCOUNT),
                                        jsonObject.getString(Constant.DISCOUNT_AMT),
                                        jsonObject.getString(Constant.USER_NAME), itemList);
                                orderTrackerslist.add(orderTracker);
                                if (cancel == 1)
                                    cancelledlist.add(orderTracker);
                                if (delivered == 1)
                                    deliveredlist.add(orderTracker);
                                if (process == 1)
                                    processedlist.add(orderTracker);
                                if (shipped == 1)
                                    shippedlist.add(orderTracker);
                                if (returned == 1)
                                    returnedList.add(orderTracker);
                            }
                            setupViewPager(viewPager);
                            lytdata.setVisibility(View.VISIBLE);
                            tabLayout.setupWithViewPager(viewPager);
                        } else {
                            lytempty.setVisibility(View.VISIBLE);
                            lytdata.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.ORDERPROCESS_URL, params, true);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new TrackOrderFragment.ViewPagerAdapter(getFragmentManager());
        adapter.addFrag(new OrderListFragment(), tabs[0]);
        adapter.addFrag(new OrderListFragment(), tabs[1]);
        adapter.addFrag(new OrderListFragment(), tabs[2]);
        adapter.addFrag(new OrderListFragment(), tabs[3]);
        adapter.addFrag(new OrderListFragment(), tabs[4]);
        adapter.addFrag(new OrderListFragment(), tabs[5]);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string._title_order_track);
        getActivity().invalidateOptionsMenu();
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
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(true);
    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle data = new Bundle();
            OrderListFragment fragment = new OrderListFragment();
            data.putInt("pos", position);
            fragment.setArguments(data);
            return fragment;
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

}