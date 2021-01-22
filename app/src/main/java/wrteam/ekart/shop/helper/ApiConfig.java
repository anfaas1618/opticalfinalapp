package wrteam.ekart.shop.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Key;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import co.paystack.android.PaystackSdk;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.DrawerActivity;
import wrteam.ekart.shop.model.Favorite;
import wrteam.ekart.shop.model.PriceVariation;
import wrteam.ekart.shop.model.Product;
import wrteam.ekart.shop.model.Slider;
import wrteam.ekart.shop.model.SystemSettings;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
import static com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES;

public class ApiConfig extends Application {

    public static final String TAG = ApiConfig.class.getSimpleName();
    static ApiConfig mInstance;
    AppEnvironment appEnvironment;
    RequestQueue mRequestQueue;
    SharedPreferences sharedPref;

    public static String VolleyErrorMessage(VolleyError error) {
        String message = "";
        try {
            if (error instanceof NetworkError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (error instanceof ServerError) {
                message = "The server could not be found. Please try again after some time!!";
            } else if (error instanceof AuthFailureError) {
//                message = "Cannot connect to Internet...Please check your connection!";
            } else if (error instanceof ParseError) {
                message = "Parsing error! Please try again after some time!!";
            } else if (error instanceof TimeoutError) {
                message = "Connection TimeOut! Please check your internet connection.";
            } else
                message = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public static String getMonth(int monthNo) {
        String month = "";

        switch (monthNo) {
            case 1:
                month = "Jan";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "Apr";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "Jul";
                break;
            case 8:
                month = "Aug";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Oct";
                break;
            case 11:
                month = "Nov";
                break;
            case 12:
                month = "Dec";
                break;
            default:
                break;
        }
        return month;
    }

    public static String getDayOfWeek(int dayNo) {
        String month = "";

        switch (dayNo) {
            case 1:
                month = "Sun";
                break;
            case 2:
                month = "Mon";
                break;
            case 3:
                month = "Tue";
                break;
            case 4:
                month = "Wed";
                break;
            case 5:
                month = "Thu";
                break;
            case 6:
                month = "Fri";
                break;
            case 7:
                month = "Sat";
                break;
            default:
                break;
        }
        return month;
    }

    public static void updateNavItemCounter(NavigationView nav, @IdRes int itemId, int count) {
        TextView view = nav.getMenu().findItem(itemId).getActionView().findViewById(R.id.counter);
        view.setText(String.valueOf(count));
        if (count <= 0) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static ArrayList<String> getDates(String startDate, String endDate) {
        ArrayList<String> dates = new ArrayList<>();
        @SuppressLint("SimpleDateFormat")
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(startDate);
            date2 = df1.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        assert date1 != null;
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        assert date2 != null;
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            dates.add(cal1.get(Calendar.DATE) + "-" + (cal1.get(Calendar.MONTH) + 1) + "-" + cal1.get(Calendar.YEAR) + "-" + cal1.get(Calendar.DAY_OF_WEEK));
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public static void removeAddress(final Activity activity, String addressId) {

        Map<String, String> params = new HashMap<>();
        params.put(Constant.DELETE_ADDRESS, Constant.GetVal);
        params.put(Constant.ID, addressId);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, activity, Constant.GET_ADDRESS_URL, params, false);
    }

    public static void getCartItemCount(final Activity activity, Session session) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_USER_CART, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        Constant.TOTAL_CART_ITEM = Integer.parseInt(jsonObject.getString(Constant.TOTAL));
                    } else {
                        Constant.TOTAL_CART_ITEM = 0;
                    }
                    activity.invalidateOptionsMenu();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, activity, Constant.CART_URL, params, false);
    }

    public static void AddOrRemoveFavorite(Activity activity, Session session, String productID, boolean isAdd) {
        Map<String, String> params = new HashMap<String, String>();
        if (isAdd) {
            params.put(Constant.ADD_TO_FAVORITES, Constant.GetVal);
        } else {
            params.put(Constant.REMOVE_FROM_FAVORITES, Constant.GetVal);
        }
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.PRODUCT_ID, productID);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {

            }
        }, activity, Constant.GET_FAVORITES_URL, params, false);
    }

    public static void RequestToVolley(final VolleyCallback callback, final Activity activity, final String url, final Map<String, String> params, final boolean isprogress) {
        if (ProgressDisplay.mProgressBar != null) {
            ProgressDisplay.mProgressBar.setVisibility(View.GONE);
        }
        final ProgressDisplay progressDisplay = new ProgressDisplay(activity);
        progressDisplay.hideProgress();
        if (ApiConfig.isConnected(activity)) {
            if (isprogress)
                progressDisplay.showProgress();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    callback.onSuccess(true, response);
                    if (isprogress)
                        progressDisplay.hideProgress();

                }
            },
                    error -> {
                        if (isprogress)
                            progressDisplay.hideProgress();
                        callback.onSuccess(false, "");
                        String message = VolleyErrorMessage(error);
                        if (!message.equals(""))
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }) {

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params1 = new HashMap<String, String>();
                    params1.put(Constant.AUTHORIZATION, "Bearer " + createJWT("eKart", "eKart Authentication"));
                    return params1;
                }

                @Override
                protected Map<String, String> getParams() {
                    params.put(Constant.AccessKey, Constant.AccessKeyVal);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
            ApiConfig.getInstance().getRequestQueue().getCache().clear();
            ApiConfig.getInstance().addToRequestQueue(stringRequest);
        }

    }

    public static void RequestToVolley(final VolleyCallback callback, final String url, final Map<String, String> params) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(true, response);

            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        callback.onSuccess(false, "");


                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                params.put(Constant.AccessKey, Constant.AccessKeyVal);
                return params;
            }
        };
        getInstance().getRequestQueue().getCache().clear();
        getInstance().addToRequestQueue(stringRequest);


    }

    public static String toTitleCase(String str) {
        if (str == null) {
            return null;
        }
        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    public static String createJWT(String issuer, String subject) {
        try {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            byte[] apiKeySecretBytes = Constant.JWT_KEY.getBytes();
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
            JwtBuilder builder = Jwts.builder()
                    .setIssuedAt(now)
                    .setSubject(subject)
                    .setIssuer(issuer)
                    .signWith(signatureAlgorithm, signingKey);

            return builder.compact();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean CheckValidattion(String item, boolean isemailvalidation, boolean ismobvalidation) {
        boolean result = false;
        if (item.length() == 0) {
            result = true;
        } else if (isemailvalidation) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(item).matches()) {
                result = true;
            }
        } else if (ismobvalidation) {
            if (!android.util.Patterns.PHONE.matcher(item).matches()) {
                result = true;
            }
        }
        return result;
    }

    @SuppressLint("DefaultLocale")
    public static String GetDiscount(String oldprice, String newprice) {
        double dold = Double.parseDouble(oldprice);
        double dnew = Double.parseDouble(newprice);

        return " (" + String.format("%.2f", (((dnew / dold) - 1) * 100)) + "%)";
    }

    public static void GetTimeSlotConfig(final Session session, Activity activity) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.SETTINGS, Constant.GetVal);
        params.put(Constant.GET_TIME_SLOT_CONFIG, Constant.GetVal);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    if (!jsonObject1.getBoolean(Constant.ERROR)) {
                        JSONObject jsonObject = new JSONObject(jsonObject1.getJSONObject(Constant.TIME_SLOT_CONFIG).toString());

                        session.setData(Constant.IS_TIME_SLOTS_ENABLE, jsonObject.getString(Constant.IS_TIME_SLOTS_ENABLE));
                        session.setData(Constant.DELIVERY_STARTS_FROM, jsonObject.getString(Constant.DELIVERY_STARTS_FROM));
                        session.setData(Constant.ALLOWED_DAYS, jsonObject.getString(Constant.ALLOWED_DAYS));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.SETTING_URL, params, false);
    }

    public static void AddMultipleProductInCart(final Session session, final Activity activity, HashMap<String, String> map) {
        if (map.size() > 0) {
            String ids = map.keySet().toString().replace("[", "").replace("]", "").replace(" ", "");
            String qty = map.values().toString().replace("[", "").replace("]", "").replace(" ", "");

            Map<String, String> params = new HashMap<String, String>();
            params.put(Constant.ADD_MULTIPLE_ITEMS, Constant.GetVal);
            params.put(Constant.USER_ID, session.getData(Constant.ID));
            params.put(Constant.PRODUCT_VARIANT_ID, ids);
            params.put(Constant.QTY, qty);

            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    if (result) {
                        getCartItemCount(activity, session);
                    }
                }
            }, activity, Constant.CART_URL, params, false);
        }
    }

    public static void addMarkers(int currentPage, ArrayList<Slider> imglist, LinearLayout
            mMarkersLayout, Context context) {

        if (context != null) {
            TextView[] markers = new TextView[imglist.size()];

            mMarkersLayout.removeAllViews();

            for (int i = 0; i < markers.length; i++) {
                markers[i] = new TextView(context);
                markers[i].setText(Html.fromHtml("&#8226;"));
                markers[i].setTextSize(35);
                markers[i].setTextColor(context.getResources().getColor(R.color.overlay_white));
                mMarkersLayout.addView(markers[i]);
            }
            if (markers.length > 0)
                markers[currentPage].setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
    }

    public static Drawable buildCounterDrawable(int count, int backgroundImageId, Activity
            activity) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);
        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = view.findViewById(R.id.count);
            textView.setVisibility(View.VISIBLE);
            textView.setText("" + count);
        }
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(activity.getResources(), bitmap);
    }

    public static void GetSettings(final Activity activity) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.SETTINGS, Constant.GetVal);
        params.put(Constant.GET_TIMEZONE, Constant.GetVal);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONObject object = objectbject.getJSONObject(Constant.SETTINGS);
                            Constant.systemSettings = new Gson().fromJson(object.toString(), SystemSettings.class);

                            if (DrawerActivity.tvWallet != null) {
                                DrawerActivity.tvWallet.setText(activity.getResources().getString(R.string.wallet_balance) + "\t:\t" + Constant.systemSettings.getCurrency() + Constant.WALLET_BALANCE);
                            }
                            String versionName = "";
                            try {
                                PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                                versionName = packageInfo.versionName;
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            if ((ApiConfig.compareVersion(versionName, Constant.systemSettings.getCurrent_version()) < 0) || (ApiConfig.compareVersion(versionName, Constant.systemSettings.getMinimum_version_required()) < 0)) {
                                OpenBottomDialog(activity);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.SETTING_URL, params, false);
    }

    public static void OpenBottomDialog(final Activity activity) {
        View sheetView = activity.getLayoutInflater().inflate(R.layout.lyt_update_app_alert, null);
        ViewGroup parentViewGroup = (ViewGroup) sheetView.getParent();
        if (parentViewGroup != null) {
            parentViewGroup.removeAllViews();
        }

        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
        FrameLayout bottomSheet = mBottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);

        ImageView imgclose = sheetView.findViewById(R.id.imgclose);
        TextView txttitle = sheetView.findViewById(R.id.tvTitle);
        Button btnNotNow = sheetView.findViewById(R.id.btnNotNow);
        Button btnUpadateNow = sheetView.findViewById(R.id.btnUpdateNow);
        if (Constant.systemSettings.getIs_version_system_on().equals("0")) {
            btnNotNow.setVisibility(View.VISIBLE);
            imgclose.setVisibility(View.VISIBLE);
            mBottomSheetDialog.setCancelable(true);
        } else
            mBottomSheetDialog.setCancelable(false);


        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetDialog.isShowing())
                    mBottomSheetDialog.dismiss();
            }
        });
        btnNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetDialog.isShowing())
                    mBottomSheetDialog.dismiss();
            }
        });

        btnUpadateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.PLAY_STORE_LINK + activity.getPackageName())));
            }
        });
    }

    public static double getWalletBalance(final Activity activity, Session session) {

        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_USER_DATA, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        ApiConfig.RequestToVolley((result, response) -> {
//                System.out.println("=================*wallet " + response);
            if (result) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean(Constant.ERROR)) {
                        Constant.WALLET_BALANCE = Double.parseDouble(object.getString(Constant.KEY_BALANCE));
                        DrawerActivity.tvWallet.setText(activity.getResources().getString(R.string.wallet_balance) + "\t:\t" + Constant.systemSettings.getCurrency() + Constant.WALLET_BALANCE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.USER_DATA_URL, params, false);
        return Constant.WALLET_BALANCE;
    }

    public static void clearFCM(Activity activity, Session session) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.REMOVE_FCM_ID, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        ApiConfig.RequestToVolley((result, response) -> {
        }, activity, Constant.REMOVE_FCM_URL, params, false);
    }

    public static String getAddress(double lat, double lng, Activity activity) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        String address = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() != 0) {
                Address obj = addresses.get(0);
                address = obj.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return address;
    }

    public static int compareVersion(String version1, String version2) {
        String[] arr1 = version1.split("\\.");
        String[] arr2 = version2.split("\\.");

        int i = 0;
        while (i < arr1.length || i < arr2.length) {
            if (i < arr1.length && i < arr2.length) {
                if (Integer.parseInt(arr1[i]) < Integer.parseInt(arr2[i])) {
                    return -1;
                } else if (Integer.parseInt(arr1[i]) > Integer.parseInt(arr2[i])) {
                    return 1;
                }
            } else if (i < arr1.length) {
                if (Integer.parseInt(arr1[i]) != 0) {
                    return 1;
                }
            } else {
                if (Integer.parseInt(arr2[i]) != 0) {
                    return -1;
                }
            }

            i++;
        }

        return 0;
    }

    public static synchronized ApiConfig getInstance() {
        return mInstance;
    }

    public static Boolean isConnected(final Activity activity) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                Toast.makeText(activity, activity.getString(R.string.no_internet_connection_try_later), Toast.LENGTH_SHORT).show();
                return false;
            } else {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                for (NetworkInfo networkInfo : info) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<Product> GetProductList(JSONArray jsonArray) {
        ArrayList<Product> productArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ArrayList<PriceVariation> priceVariations = new ArrayList<>();
                    JSONArray pricearray = jsonObject.getJSONArray(Constant.VARIANT);

                    for (int j = 0; j < pricearray.length(); j++) {
                        JSONObject obj = pricearray.getJSONObject(j);
                        String discountpercent = "0";
                        if (!obj.getString(Constant.DISCOUNTED_PRICE).equals("0")) {
                            discountpercent = ApiConfig.GetDiscount(obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE));
                        }
                        priceVariations.add(new PriceVariation(obj.getString(Constant.CART_ITEM_COUNT), obj.getString(Constant.ID), obj.getString(Constant.PRODUCT_ID), obj.getString(Constant.TYPE), obj.getString(Constant.MEASUREMENT), obj.getString(Constant.MEASUREMENT_UNIT_ID), obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE), obj.getString(Constant.SERVE_FOR), obj.getString(Constant.STOCK), obj.getString(Constant.STOCK_UNIT_ID), obj.getString(Constant.MEASUREMENT_UNIT_NAME), obj.getString(Constant.STOCK_UNIT_NAME), discountpercent));
                    }
                    productArrayList.add(new Product(jsonObject.getString(Constant.TAX_PERCENT), jsonObject.getString(Constant.ROW_ORDER), jsonObject.getString(Constant.TILL_STATUS), jsonObject.getString(Constant.CANCELLABLE_STATUS), jsonObject.getString(Constant.MANUFACTURER), jsonObject.getString(Constant.MADE_IN), jsonObject.getString(Constant.RETURN_STATUS), jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.SLUG), jsonObject.getString(Constant.SUC_CATE_ID), jsonObject.getString(Constant.IMAGE), jsonObject.getJSONArray(Constant.OTHER_IMAGES), jsonObject.getString(Constant.DESCRIPTION), jsonObject.getString(Constant.STATUS), jsonObject.getString(Constant.DATE_ADDED), jsonObject.getBoolean(Constant.IS_FAVORITE), jsonObject.getString(Constant.CATEGORY_ID), priceVariations, jsonObject.getString(Constant.INDICATOR)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productArrayList;

    }

    public static ArrayList<Favorite> GetFavoriteProductList(JSONArray jsonArray) {
        ArrayList<Favorite> productArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ArrayList<PriceVariation> priceVariations = new ArrayList<>();
                    JSONArray pricearray = jsonObject.getJSONArray(Constant.VARIANT);

                    for (int j = 0; j < pricearray.length(); j++) {
                        JSONObject obj = pricearray.getJSONObject(j);
                        String discountpercent = "0";
                        if (!obj.getString(Constant.DISCOUNTED_PRICE).equals("0")) {
                            discountpercent = ApiConfig.GetDiscount(obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE));
                        }
                        priceVariations.add(new PriceVariation(obj.getString(Constant.CART_ITEM_COUNT), obj.getString(Constant.ID), obj.getString(Constant.PRODUCT_ID), obj.getString(Constant.TYPE), obj.getString(Constant.MEASUREMENT), obj.getString(Constant.MEASUREMENT_UNIT_ID), obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE), obj.getString(Constant.SERVE_FOR), obj.getString(Constant.STOCK), obj.getString(Constant.STOCK_UNIT_ID), obj.getString(Constant.MEASUREMENT_UNIT_NAME), obj.getString(Constant.STOCK_UNIT_NAME), discountpercent));
                    }
                    productArrayList.add(new Favorite(jsonObject.getString(Constant.PRODUCT_ID), jsonObject.getString(Constant.CANCELLABLE_STATUS), jsonObject.getString(Constant.TILL_STATUS), jsonObject.getString(Constant.MANUFACTURER), jsonObject.getString(Constant.MADE_IN), jsonObject.getString(Constant.RETURN_STATUS), jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.SLUG), jsonObject.getString(Constant.SUC_CATE_ID), jsonObject.getString(Constant.IMAGE), jsonObject.getJSONArray(Constant.OTHER_IMAGES), jsonObject.getString(Constant.DESCRIPTION), jsonObject.getString(Constant.STATUS), jsonObject.getString(Constant.DATE_ADDED), jsonObject.getBoolean(Constant.IS_FAVORITE), jsonObject.getString(Constant.CATEGORY_ID), priceVariations, jsonObject.getString(Constant.INDICATOR)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productArrayList;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        appEnvironment = AppEnvironment.SANDBOX;
        sharedPref = this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        PaystackSdk.initialize(getApplicationContext());
    }

    public AppEnvironment getAppEnvironment() {
        return appEnvironment;
    }

    public String getData(String id) {
        return sharedPref.getString(id, "");
    }

    public String getDeviceToken() {
        return sharedPref.getString("DEVICETOKEN", "");
    }

    public void setDeviceToken(String token) {
        sharedPref.edit().putString("DEVICETOKEN", token).apply();
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
}
