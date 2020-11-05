package wrteam.ekart.shop.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.MainActivity;
import wrteam.ekart.shop.adapter.CategoryAdapter;
import wrteam.ekart.shop.adapter.OfferAdapter;
import wrteam.ekart.shop.adapter.SectionAdapter;
import wrteam.ekart.shop.adapter.SliderAdapter;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.AppController;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.VolleyCallback;
import wrteam.ekart.shop.model.Category;
import wrteam.ekart.shop.model.Slider;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static wrteam.ekart.shop.helper.ApiConfig.GetTimeSlotConfig;


public class HomeFragment extends Fragment {

    public static Session session;
    public static ArrayList<Category> categoryArrayList, sectionList;
    Activity activity;
    NestedScrollView nestedScrollView;
    RelativeLayout progressBar;
    SwipeRefreshLayout swipeLayout;
    View root;
    int timerDelay = 0, timerWaiting = 0;
    SearchView searchview;
    private RecyclerView categoryRecyclerView, sectionView, offerView;
    private ArrayList<Slider> sliderArrayList;
    private ViewPager mPager;
    private LinearLayout mMarkersLayout;
    private int size;
    private Timer swipeTimer;
    private Handler handler;
    private Runnable Update;
    private int currentPage = 0;
    private LinearLayout lytCategory, lytSearchview;
    private Menu menu;
    private boolean searchVisible = false;

    public static void UpdateToken(final String token, Activity activity) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.TYPE, Constant.REGISTER_DEVICE);
        params.put(Constant.TOKEN, token);
        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            session.setData(Session.KEY_FCM_ID, token);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.RegisterUrl, params, false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);
        session = new Session(getContext());

        timerDelay = 3000;
        timerWaiting = 3000;

        activity = getActivity();
        GetTimeSlotConfig(session, activity);
        setHasOptionsMenu(true);

        progressBar = root.findViewById(R.id.progressBar);
        swipeLayout = root.findViewById(R.id.swipeLayout);

        categoryRecyclerView = root.findViewById(R.id.categoryrecycleview);
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext (), LinearLayoutManager.HORIZONTAL, false));
        sectionView = root.findViewById(R.id.sectionView);
        sectionView.setLayoutManager(new LinearLayoutManager(getContext()));
        sectionView.setNestedScrollingEnabled(false);

        offerView = root.findViewById(R.id.offerView);
        offerView.setLayoutManager(new LinearLayoutManager(getContext()));
        offerView.setNestedScrollingEnabled(false);

        nestedScrollView = root.findViewById(R.id.nestedScrollView);
        mMarkersLayout = root.findViewById(R.id.layout_markers);
        lytCategory = root.findViewById(R.id.lytCategory);
        lytSearchview = root.findViewById(R.id.lytSearchview);

        searchview = root.findViewById(R.id.searchview);

        if (nestedScrollView != null) {

            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY) {
                        searchVisible = true;
                        menu.findItem(R.id.toolbar_search).setVisible(true);
                        activity.invalidateOptionsMenu();
                    }
                    if (scrollY < oldScrollY) {
                        searchVisible = false;
                        menu.findItem(R.id.toolbar_search).setVisible(false);
                        activity.invalidateOptionsMenu();
                    }
                }
            });
        }

        searchview.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchview.setIconified(true);
                searchview.onActionViewCollapsed();
                MainActivity.fm.beginTransaction().add(R.id.container, new SearchFragment()).addToBackStack(null).commit();
            }
        });

        lytSearchview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fm.beginTransaction().add(R.id.container, new SearchFragment()).addToBackStack(null).commit();
            }
        });

        mPager = root.findViewById(R.id.pager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                ApiConfig.addMarkers(position, sliderArrayList, mMarkersLayout, getContext());
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (swipeTimer != null) {
                    swipeTimer.cancel();
                }
                if (AppController.isConnected(getActivity())) {
                    GetSlider();
                    GetCategory();
                    SectionProductRequest();
                    GetOfferImage();
                }
                swipeLayout.setRefreshing(false);
            }
        });


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                if (!token.equals(session.getData(Session.KEY_FCM_ID))) {
                    UpdateToken(token, getActivity());
                }
            }
        });

        if (AppController.isConnected(getActivity())) {
            ApiConfig.GetSettings(getActivity());
            GetSlider();
            GetCategory();
            SectionProductRequest();
            GetOfferImage();
        }

        return root;
    }

    public void GetOfferImage() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_OFFER_IMAGE, Constant.GetVal);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        ArrayList<String> offerList = new ArrayList<>();
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                offerList.add(object.getString(Constant.IMAGE));
                            }
                            offerView.setAdapter(new OfferAdapter(offerList, R.layout.offer_lyt));

                            progressBar.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        }, getActivity(), Constant.OFFER_URL, params, false);
    }

    private void GetCategory() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
//                System.out.println ("======cate " + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        categoryArrayList = new ArrayList<>();
                        categoryArrayList.clear();
                        if (!object.getBoolean(Constant.ERROR)) {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            Gson gson = new Gson();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Category category = gson.fromJson(jsonObject.toString(), Category.class);
                                categoryArrayList.add(category);
                            }
                            categoryRecyclerView.setAdapter(new CategoryAdapter(getContext(), getActivity(), categoryArrayList, R.layout.lyt_category));

                            progressBar.setVisibility(View.GONE);
                        } else {
                            lytCategory.setVisibility(View.GONE);
                        }
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, getActivity(), Constant.CategoryUrl, params, false);
    }

    public void SectionProductRequest() {  //json request for product search

        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_ALL_SECTIONS, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object1 = new JSONObject(response);
                        if (!object1.getBoolean(Constant.ERROR)) {
                            sectionList = new ArrayList<>();
                            JSONArray jsonArray = object1.getJSONArray(Constant.SECTIONS);
                            for (int j = 0; j < jsonArray.length(); j++) {
                                Category section = new Category();
                                JSONObject jsonObject = jsonArray.getJSONObject(j);
                                section.setName(jsonObject.getString(Constant.TITLE));
                                section.setStyle(jsonObject.getString(Constant.SECTION_STYLE));
                                section.setSubtitle(jsonObject.getString(Constant.SHORT_DESC));
                                JSONArray productArray = jsonObject.getJSONArray(Constant.PRODUCTS);
                                section.setProductList(ApiConfig.GetProductList(productArray));
                                sectionList.add(section);

                                progressBar.setVisibility(View.GONE);
                            }
                            sectionView.setVisibility(View.VISIBLE);
                            SectionAdapter sectionAdapter = new SectionAdapter(getContext(), getActivity(), sectionList);
                            sectionView.setAdapter(sectionAdapter);

                            progressBar.setVisibility(View.GONE);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        }, getActivity(), Constant.FeaturedProductUrl, params, false);
    }

    private void GetSlider() {

        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_SLIDER_IMAGE, Constant.GetVal);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {

                    sliderArrayList = new ArrayList<>();
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            size = jsonArray.length();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                sliderArrayList.add(new Slider(jsonObject.getString(Constant.TYPE), jsonObject.getString(Constant.TYPE_ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.IMAGE)));
                            }
                            mPager.setAdapter(new SliderAdapter(sliderArrayList, getActivity(), R.layout.lyt_slider, "home"));
                            ApiConfig.addMarkers(0, sliderArrayList, mMarkersLayout, getContext());
                            handler = new Handler();
                            Update = new Runnable() {
                                public void run() {
                                    if (currentPage == size) {
                                        currentPage = 0;
                                    }
                                    try {
                                        mPager.setCurrentItem(currentPage++, true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            swipeTimer = new Timer();
                            swipeTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    handler.post(Update);
                                }
                            }, timerDelay, timerWaiting);
                        }
//                        System.out.println("TIMING : : : " + timerDelay + " , " + timerWaiting);

                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();

                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        }, getActivity(), Constant.SliderUrl, params, false);

    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.app_name);
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
        this.menu = menu;
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(searchVisible);
    }

}