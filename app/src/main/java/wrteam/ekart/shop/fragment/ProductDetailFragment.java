package wrteam.ekart.shop.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.MainActivity;
import wrteam.ekart.shop.adapter.AdapterStyle1;
import wrteam.ekart.shop.adapter.SliderAdapter;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.AppController;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.DatabaseHelper;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.VolleyCallback;
import wrteam.ekart.shop.model.Favorite;
import wrteam.ekart.shop.model.PriceVariation;
import wrteam.ekart.shop.model.Product;
import wrteam.ekart.shop.model.Slider;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static wrteam.ekart.shop.helper.ApiConfig.AddOrRemoveFavorite;
import static wrteam.ekart.shop.helper.ApiConfig.GetSettings;


public class ProductDetailFragment extends Fragment {
    static ArrayList<Slider> sliderArrayList;
    TextView tvMfg, tvMadeIn, txtProductName, txtqty, txtPrice, txtOriginalPrice, txtDiscountedPrice, txtMeasurement, txtstatus, tvReturnable, tvCancellable, tvTitleMadeIn, tvTitleMfg, tvTaxPercent;
    WebView webDescription;
    ViewPager viewPager;
    Spinner spinner;
    ImageView imgIndicator;
    SpannableString spannableString;
    LinearLayout mMarkersLayout, lytMfg, lytMadeIn;
    RelativeLayout lytqty, lytmainprice;
    ScrollView scrollView;
    Session session;
    boolean favorite;
    ImageView imgFav;
    ImageButton imgAdd, imgMinus;
    LinearLayout lytshare, lytsave, lytSimilar;
    int size, count;
    View root;
    int vpos;
    String from, id;
    boolean isLogin;
    Product product;
    PriceVariation priceVariation;
    ArrayList<PriceVariation> priceVariationslist;
    DatabaseHelper databaseHelper;
    int position = 0;
    Button btnCart;
    Activity activity;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    TextView tvMore;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_product_detail, container, false);

        setHasOptionsMenu(true);
        activity = getActivity();

        Constant.CartValues = new HashMap<>();

        session = new Session(activity);
        isLogin = session.isUserLoggedIn();
        databaseHelper = new DatabaseHelper(activity);


        from = getArguments().getString(Constant.FROM);
        vpos = getArguments().getInt("vpos", 0);
        id = getArguments().getString("id");

        if (from.equals("fragment") || from.equals("favorite") || from.equals("search")) {
            position = getArguments().getInt("position");
        }

        lytqty = root.findViewById(R.id.lytqty);
        scrollView = root.findViewById(R.id.scrollView);
        mMarkersLayout = root.findViewById(R.id.layout_markers);
        sliderArrayList = new ArrayList<>();
        viewPager = root.findViewById(R.id.viewPager);
        txtProductName = root.findViewById(R.id.txtproductname);
        txtOriginalPrice = root.findViewById(R.id.txtoriginalprice);
        txtDiscountedPrice = root.findViewById(R.id.txtdiscountPrice);
        webDescription = root.findViewById(R.id.txtDescription);
        txtPrice = root.findViewById(R.id.txtprice);
        txtMeasurement = root.findViewById(R.id.txtmeasurement);
        imgFav = root.findViewById(R.id.imgFav);
        lytmainprice = root.findViewById(R.id.lytmainprice);
        txtqty = root.findViewById(R.id.txtqty);
        txtstatus = root.findViewById(R.id.txtstatus);
        imgAdd = root.findViewById(R.id.btnaddqty);
        imgMinus = root.findViewById(R.id.btnminusqty);
        spinner = root.findViewById(R.id.spinner);
        imgIndicator = root.findViewById(R.id.imgIndicator);
        lytshare = root.findViewById(R.id.lytshare);
        lytsave = root.findViewById(R.id.lytsave);
        lytSimilar = root.findViewById(R.id.lytSimilar);
        tvReturnable = root.findViewById(R.id.tvReturnable);
        tvCancellable = root.findViewById(R.id.tvCancellable);
        tvMadeIn = root.findViewById(R.id.tvMadeIn);
        tvTitleMadeIn = root.findViewById(R.id.tvTitleMadeIn);
        tvMfg = root.findViewById(R.id.tvMfg);
        tvTitleMfg = root.findViewById(R.id.tvTitleMfg);
        lytMfg = root.findViewById(R.id.lytMfg);
        lytMadeIn = root.findViewById(R.id.lytMadeIn);
        btnCart = root.findViewById(R.id.btnCart);
        recyclerView = root.findViewById(R.id.recyclerView);
        relativeLayout = root.findViewById(R.id.relativeLayout);
        tvMore = root.findViewById(R.id.tvMore);
        tvTaxPercent = root.findViewById(R.id.tvTaxPercent);

        GetProductDetail(id);
        GetSettings(activity);

        lytmainprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.performClick();
            }
        });

        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSimilar();
            }
        });

        lytSimilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowSimilar();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fm.beginTransaction().add(R.id.container, new CartFragment()).addToBackStack(null).commit();
            }
        });

        lytshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ShareProduct().execute();
            }
        });

        lytsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin) {
                    favorite = product.isIs_favorite();
                    if (AppController.isConnected(activity)) {
                        if (favorite) {
                            favorite = false;
                            product.setIs_favorite(false);
                            imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                        } else {
                            favorite = true;
                            product.setIs_favorite(true);
                            imgFav.setImageResource(R.drawable.ic_is_favorite);
                        }
                        AddOrRemoveFavorite(activity, session, product.getId(), favorite);
                    }
                } else {
                    favorite = databaseHelper.getFavouriteById(product.getId());
                    if (favorite) {
                        favorite = false;
                        imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                    } else {
                        favorite = true;
                        imgFav.setImageResource(R.drawable.ic_is_favorite);
                    }
                    databaseHelper.AddOrRemoveFavorite(product.getId(), favorite);
                }
                if (from.equals("fragment")) {
                    ProductListFragment.productArrayList.get(position).setIs_favorite(favorite);
                    ProductListFragment.mAdapter.notifyDataSetChanged();
                } else if (from.equals("favorite")) {
                    if (session.isUserLoggedIn()) {
                        Favorite favProduct = new Favorite();
                        favProduct.setId(product.getId());
                        favProduct.setProduct_id(product.getId());
                        favProduct.setName(product.getName());
                        favProduct.setSlug(product.getSlug());
                        favProduct.setSubcategory_id(product.getSubcategory_id());
                        favProduct.setImage(product.getImage());
                        favProduct.setStatus(product.getStatus());
                        favProduct.setDate_added(product.getDate_added());
                        favProduct.setCategory_id(product.getCategory_id());
                        favProduct.setIndicator(product.getIndicator());
                        favProduct.setManufacturer(product.getManufacturer());
                        favProduct.setMade_in(product.getMade_in());
                        favProduct.setReturn_status(product.getReturn_status());
                        favProduct.setCancelable_status(product.getCancelable_status());
                        favProduct.setTill_status(product.getTill_status());
                        favProduct.setPriceVariations(product.getPriceVariations());
                        favProduct.setOther_images(product.getOther_images());
                        favProduct.setIs_favorite(true);
                        if (favorite) {
                            FavoriteFragment.favoriteArrayList.add(favProduct);
                        } else {
                            FavoriteFragment.favoriteArrayList.remove(position);
                        }
                        FavoriteFragment.favoriteLoadMoreAdapter.notifyDataSetChanged();
                    } else {
                        if (favorite) {
                            FavoriteFragment.productArrayList.add(product);
                        } else {
                            FavoriteFragment.productArrayList.remove(position);
                        }
                        FavoriteFragment.offlineFavoriteAdapter.notifyDataSetChanged();
                    }
                } else if (from.equals("search")) {
                    SearchFragment.productArrayList.get(position).setIs_favorite(favorite);
                    SearchFragment.productAdapter.notifyDataSetChanged();
                }
            }
        });

        imgMinus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (AppController.isConnected(activity)) {
                    Constant.CLICK = true;
                    count = Integer.parseInt(txtqty.getText().toString());
                    if (!(count <= 0)) {
                        if (count != 0) {
                            count--;
                            txtqty.setText("" + count);
                            if (isLogin) {
                                if (Constant.CartValues.containsKey(priceVariationslist.get(vpos).getId())) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        Constant.CartValues.replace(priceVariationslist.get(vpos).getId(), "" + count);
                                    } else {
                                        Constant.CartValues.remove(priceVariationslist.get(vpos).getId());
                                        Constant.CartValues.put(priceVariationslist.get(vpos).getId(), "" + count);
                                    }
                                } else {
                                    Constant.CartValues.put(priceVariationslist.get(vpos).getId(), "" + count);
                                }

                                ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                            } else {
                                databaseHelper.AddOrderData(priceVariationslist.get(vpos).getId(), priceVariation.getProduct_id(), "" + count);
                            }
                            NotifyData(count);
                        }

                    }
                }

            }
        });

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (AppController.isConnected(activity)) {
                    count = Integer.parseInt(txtqty.getText().toString());
                    if (!(count >= Float.parseFloat(priceVariationslist.get(vpos).getStock()))) {
                        if (count < Constant.MAX_PRODUCT_LIMIT) {
                            Constant.CLICK = true;
                            count++;
                            txtqty.setText("" + count);
                            if (isLogin) {
                                if (Constant.CartValues.containsKey(priceVariationslist.get(vpos).getId())) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        Constant.CartValues.replace(priceVariationslist.get(vpos).getId(), "" + count);
                                    } else {
                                        Constant.CartValues.remove(priceVariationslist.get(vpos).getId());
                                        Constant.CartValues.put(priceVariationslist.get(vpos).getId(), "" + count);
                                    }
                                } else {
                                    Constant.CartValues.put(priceVariationslist.get(vpos).getId(), "" + count);
                                }
                                ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                            } else {
                                databaseHelper.AddOrderData(priceVariationslist.get(vpos).getId(), priceVariation.getProduct_id(), "" + count);
                            }
                        } else {
                            Toast.makeText(getContext(), getString(R.string.limit_alert), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
                    }
                    NotifyData(count);
                }
            }
        });

        return root;
    }

    public void ShowSimilar() {
        Fragment fragment = new ProductListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", product.getId());
        bundle.putString("cat_id", product.getCategory_id());
        bundle.putString(Constant.FROM, "similar");
        bundle.putString("name", "Similar Products");
        fragment.setArguments(bundle);
        MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
    }


    void GetSimilarData(Product product) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_SIMILAR_PRODUCT, Constant.GetVal);
        params.put(Constant.PRODUCT_ID, product.getId());
        params.put(Constant.CATEGORY_ID, product.getCategory_id());
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                            AdapterStyle1 adapter = new AdapterStyle1(getContext(), activity, ApiConfig.GetProductList(objectbject.getJSONArray(Constant.DATA)), R.layout.offer_layout);
                            recyclerView.setAdapter(adapter);
                            relativeLayout.setVisibility(View.VISIBLE);
                        } else {
                            relativeLayout.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.GET_SIMILAR_PRODUCT_URL, params, false);
    }

    public void NotifyData(int count) {
        switch (from) {
            case "fragment":
                ProductListFragment.productArrayList.get(position).getPriceVariations().get(vpos).setQty(count);
                ProductListFragment.mAdapter.notifyItemChanged(position, ProductListFragment.productArrayList.get(position));
                if (session.isUserLoggedIn()) {
                    ApiConfig.getCartItemCount(activity, session);
                } else {
                    databaseHelper.getTotalItemOfCart(activity);
                }
                activity.invalidateOptionsMenu();
                break;
            case "favorite":
                if (session.isUserLoggedIn()) {
                    FavoriteFragment.favoriteArrayList.get(position).getPriceVariations().get(vpos).setQty(count);
                    FavoriteFragment.favoriteLoadMoreAdapter.notifyItemChanged(position, FavoriteFragment.favoriteArrayList.get(position));
                } else {
                    FavoriteFragment.productArrayList.get(position).getPriceVariations().get(vpos).setQty(count);
                    FavoriteFragment.offlineFavoriteAdapter.notifyItemChanged(position, FavoriteFragment.productArrayList.get(position));
                    databaseHelper.getTotalItemOfCart(activity);
                }
                activity.invalidateOptionsMenu();
                break;
            case "search":
                SearchFragment.productArrayList.get(position).getPriceVariations().get(vpos).setQty(count);
                SearchFragment.productAdapter.notifyItemChanged(position, SearchFragment.productArrayList.get(position));
                if (!session.isUserLoggedIn()) {
                    databaseHelper.getTotalItemOfCart(activity);
                }
                activity.invalidateOptionsMenu();
                break;
            case "section":
            case "share":
                if (!session.isUserLoggedIn()) {
                    databaseHelper.getTotalItemOfCart(activity);
                } else {
                    ApiConfig.getCartItemCount(activity, session);
                }
                activity.invalidateOptionsMenu();
                break;
        }
    }

    void GetProductDetail(final String productid) {
        root.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.PRODUCT_ID, productid);
        if (session.isUserLoggedIn()) {
            params.put(Constant.USER_ID, session.getData(Constant.ID));
        }

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {

                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            product = ApiConfig.GetProductList(jsonArray).get(0);
                            priceVariationslist = product.getPriceVariations();

                            SetProductDetails(product);
                            GetSimilarData(product);

                            root.findViewById(R.id.progressBar).setVisibility(View.GONE);
                        } else {
                            root.findViewById(R.id.progressBar).setVisibility(View.GONE);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        root.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    }
                }
            }
        }, activity, Constant.GET_PRODUCT_DETAIL_URL, params, false);
    }


    @SuppressLint("SetTextI18n")
    void SetProductDetails(final Product product) {
        try {
            sliderArrayList = new ArrayList<>();

            JSONArray jsonArray = product.getOther_images();
            size = jsonArray.length();

            sliderArrayList.add(new Slider(product.getImage()));

            if (product.getMade_in().length() > 0) {
                lytMadeIn.setVisibility(View.VISIBLE);
                tvMadeIn.setText(product.getMade_in());
            }

            if (product.getManufacturer().length() > 0) {
                lytMfg.setVisibility(View.VISIBLE);
                tvMfg.setText(product.getManufacturer());
            }

            if (isLogin) {
                if (product.isIs_favorite()) {
                    favorite = true;
                    imgFav.setImageResource(R.drawable.ic_is_favorite);
                } else {
                    favorite = false;
                    imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                }
            } else {
                if (databaseHelper.getFavouriteById(product.getId())) {
                    imgFav.setImageResource(R.drawable.ic_is_favorite);
                } else {
                    imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                }
            }

            if (isLogin) {
                if (Constant.CartValues.containsKey(product.getPriceVariations().get(0).getId())) {
                    txtqty.setText("" + Constant.CartValues.get(product.getPriceVariations().get(0).getId()));
                } else {
                    txtqty.setText(product.getPriceVariations().get(0).getCart_count());
                }
            } else {
                txtqty.setText(databaseHelper.CheckOrderExists(product.getPriceVariations().get(0).getId(), product.getPriceVariations().get(0).getProduct_id()));
            }

            if (product.getReturn_status().equalsIgnoreCase("1")) {
                tvReturnable.setText(Constant.ORDER_DAY_LIMIT + " Days Returnable.");
            } else {
                tvReturnable.setText("Not Returnable.");
            }

            if (product.getCancelable_status().equalsIgnoreCase("1")) {
                tvCancellable.setText("Order Can Cancel Till Order " + ApiConfig.toTitleCase(product.getTill_status()) + ".");
            } else {
                tvCancellable.setText("Non Cancellable.");
            }


            for (int i = 0; i < jsonArray.length(); i++) {
                sliderArrayList.add(new Slider(jsonArray.getString(i)));
            }

            viewPager.setAdapter(new SliderAdapter(sliderArrayList, activity, R.layout.lyt_detail_slider, "detail"));
            ApiConfig.addMarkers(0, sliderArrayList, mMarkersLayout, getContext());


            if (priceVariationslist.size() == 1) {
                spinner.setVisibility(View.GONE);
                lytmainprice.setEnabled(false);
                priceVariation = priceVariationslist.get(0);
                session.setData(Constant.PRODUCT_VARIANT_ID, "" + 0);
                SetSelectedData(priceVariation);
            }

            if (!product.getIndicator().equals("0")) {
                imgIndicator.setVisibility(View.VISIBLE);
                if (product.getIndicator().equals("1"))
                    imgIndicator.setImageResource(R.drawable.ic_veg_icon);
                else if (product.getIndicator().equals("2"))
                    imgIndicator.setImageResource(R.drawable.ic_non_veg_icon);
            }
            ProductDetailFragment.CustomAdapter customAdapter = new ProductDetailFragment.CustomAdapter();
            spinner.setAdapter(customAdapter);

            webDescription.setVerticalScrollBarEnabled(true);
            webDescription.loadDataWithBaseURL("", product.getDescription(), "text/html", "UTF-8", "");
            webDescription.setBackgroundColor(getResources().getColor(R.color.white));
            txtProductName.setText(product.getName());

            if (product.getTax_percentage().length() > 0 && !product.getTax_percentage().isEmpty()) {
                tvTaxPercent.setText(product.getTax_percentage() + "% Tax Applicable on Product Price");
            } else {
                tvTaxPercent.setVisibility(View.GONE);
            }


            spinner.setSelection(vpos);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    priceVariation = product.getPriceVariations().get(i);
                    vpos = i;
                    session.setData(Constant.PRODUCT_VARIANT_ID, "" + i);
                    SetSelectedData(priceVariation);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            scrollView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.app_name);
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


    @SuppressLint("SetTextI18n")
    public void SetSelectedData(PriceVariation priceVariation) {


        txtMeasurement.setText(" ( " + priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name() + " ) ");
        txtPrice.setText(getString(R.string.offer_price) + Constant.SETTING_CURRENCY_SYMBOL + priceVariation.getProductPrice());
        txtstatus.setText(priceVariation.getServe_for());

        if (priceVariation.getDiscounted_price().equals("0") || priceVariation.getDiscounted_price().equals("")) {
            txtDiscountedPrice.setVisibility(View.GONE);
        } else {
            spannableString = new SpannableString(getString(R.string.mrp) + Constant.SETTING_CURRENCY_SYMBOL + priceVariation.getPrice());
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            txtOriginalPrice.setText(spannableString);
            double diff = Double.parseDouble(priceVariation.getPrice()) - Double.parseDouble(priceVariation.getProductPrice());
            txtDiscountedPrice.setText(getString(R.string.you_save) + Constant.SETTING_CURRENCY_SYMBOL + diff + priceVariation.getDiscountpercent());
        }


        if (isLogin) {
//            System.out.println("priceVariation.getId()) : " + Constant.CartValues);
            if (Constant.CartValues.containsKey(priceVariation.getId())) {
                txtqty.setText(Constant.CartValues.get(priceVariation.getId()));
            } else {
                txtqty.setText(priceVariation.getCart_count());
            }
        } else {
            txtqty.setText(databaseHelper.CheckOrderExists(priceVariation.getId(), priceVariation.getProduct_id()));
        }

        if (priceVariation.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
            txtstatus.setVisibility(View.VISIBLE);
            lytqty.setVisibility(View.GONE);
        } else {
            txtstatus.setVisibility(View.GONE);
            lytqty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(true);
        menu.findItem(R.id.toolbar_cart).setIcon(ApiConfig.buildCounterDrawable(Constant.TOTAL_CART_ITEM, R.drawable.ic_cart, activity));
        activity.invalidateOptionsMenu();
    }

    @Override
    public void onPause() {
        super.onPause();
        ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
    }

    public class ShareProduct extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Bitmap bitmap = null;
                URL url = null;
                url = new URL(product.getImage());
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
                Date now = new Date();
                File file = new File(activity.getExternalCacheDir(), formatter.format(now) + ".png");
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();

                Uri uri = FileProvider.getUriForFile(getContext(), activity.getPackageName() + ".provider", file);

                String message = product.getName() + "\n";
                message = message + Constant.share_url + "itemdetail/" + product.getId() + "/" + product.getSlug();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                intent.setDataAndType(uri, activity.getContentResolver().getType(uri));
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(intent, getString(R.string.share_via)));

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }
    }

    public class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return product.getPriceVariations().size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint({"ViewHolder", "SetTextI18n"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.lyt_spinner_item, null);
            TextView measurement = view.findViewById(R.id.txtmeasurement);
            TextView price = view.findViewById(R.id.txtprice);

            PriceVariation extra = product.getPriceVariations().get(i);
            measurement.setText(extra.getMeasurement() + " " + extra.getMeasurement_unit_name());
            price.setText(Constant.SETTING_CURRENCY_SYMBOL + extra.getProductPrice());

            if (extra.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
                measurement.setTextColor(getResources().getColor(R.color.red));
                price.setTextColor(getResources().getColor(R.color.red));
            } else {
                measurement.setTextColor(getResources().getColor(R.color.black));
                price.setTextColor(getResources().getColor(R.color.black));
            }

            return view;
        }
    }
}
