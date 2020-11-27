package wrteam.ekart.shop.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

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

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.DrawerActivity;
import wrteam.ekart.shop.model.Favorite;
import wrteam.ekart.shop.model.OrderTracker;
import wrteam.ekart.shop.model.PriceVariation;
import wrteam.ekart.shop.model.Product;
import wrteam.ekart.shop.model.Slider;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
import static com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES;

public class ApiConfig {
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static String user_location = "";
    public static double latitude1 = 0, longitude1 = 0;
    public static GPSTracker gps;

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
        ArrayList<String> dates = new ArrayList<String>();
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
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            dates.add(cal1.get(Calendar.DATE) + "-" + (cal1.get(Calendar.MONTH) + 1) + "-" + cal1.get(Calendar.YEAR) + "-" + cal1.get(Calendar.DAY_OF_WEEK));
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public static void removeAddress(final Activity activity, String addressId) {

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.DELETE_ADDRESS, Constant.GetVal);
        params.put(Constant.ID, addressId);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, activity, Constant.GET_ADDRESS_URL, params, false);
    }

    public static void getCartItemCount(final Activity activity, Session session) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_USER_CART, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
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

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {

                }
            }
        }, activity, Constant.GET_FAVORITES_URL, params, false);
    }

    public static void RequestToVolley(final VolleyCallback callback, final Activity activity, final String url, final Map<String, String> params, final boolean isprogress) {
        if (ProgressDisplay.mProgressBar != null) {
            ProgressDisplay.mProgressBar.setVisibility(View.GONE);
        }
        final ProgressDisplay progressDisplay = new ProgressDisplay(activity);
        progressDisplay.hideProgress();
        if (AppController.isConnected(activity)) {
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
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (isprogress)
                                progressDisplay.hideProgress();
                            callback.onSuccess(false, "");
                            String message = VolleyErrorMessage(error);
                            if (!message.equals(""))
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
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
            AppController.getInstance().getRequestQueue().getCache().clear();
            AppController.getInstance().addToRequestQueue(stringRequest);
        }

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

    public static String GetDiscount(String oldprice, String newprice) {
        double dold = Double.parseDouble(oldprice);
        double dnew = Double.parseDouble(newprice);

        //return String.valueOf(((dnew / dold) - 1) * 100);
        return " (" + String.format("%.2f", (((dnew / dold) - 1) * 100)) + "%)";
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
        return productArrayList;
    }

    public static void GetTimeSlotConfig(final Session session, Activity activity) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.SETTINGS, Constant.GetVal);
        params.put(Constant.GET_TIME_SLOT_CONFIG, Constant.GetVal);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
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

    public static void setOrderTrackerLayout(Activity activity, OrderTracker
            order, RecyclerView.ViewHolder holder) {
        for (int i = 0; i < order.getOrderStatusArrayList().size(); i++) {
            int img = activity.getResources().getIdentifier("img" + i, "id", activity.getPackageName());
            int view = activity.getResources().getIdentifier("l" + i, "id", activity.getPackageName());
            int txt = activity.getResources().getIdentifier("txt" + i, "id", activity.getPackageName());
            int textview = activity.getResources().getIdentifier("txt" + i + "" + i, "id", activity.getPackageName());
            System.out.println("=============== " + order.getOrderStatusArrayList().get(i).toString());
            View v = holder.itemView;
            if (img != 0 && v.findViewById(img) != null) {
                ImageView imageView = v.findViewById(img);
                imageView.setColorFilter(activity.getResources().getColor(R.color.colorAccent));
            }
            if (view != 0 && v.findViewById(view) != null) {
                View view1 = v.findViewById(view);
                view1.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
            }
            if (txt != 0 && v.findViewById(txt) != null) {
                TextView view1 = v.findViewById(txt);
                view1.setTextColor(activity.getResources().getColor(R.color.black));
            }
            if (textview != 0 && v.findViewById(textview) != null) {
                TextView view1 = v.findViewById(textview);
                String str = order.getOrderStatusArrayList().get(i).getStatusdate();
                String[] splited = str.split("\\s+");
                view1.setText(splited[0] + "\n" + splited[1]);
            }
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
        params.put(Constant.GET_SETTINGS, Constant.GetVal);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
//                System.out.println("============pay method " + response);
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONObject object = objectbject.getJSONObject(Constant.SETTINGS);
                            Constant.VERSION_CODE = object.getString(Constant.KEY_VERSION_CODE);
                            Constant.REQUIRED_VERSION = object.getString(Constant.KEY_VERSION_CODE);
                            Constant.VERSION_STATUS = object.getString(Constant.KEY_UPDATE_STATUS);
                            Constant.REFER_EARN_BONUS = object.getString(Constant.KEY_REFER_EARN_BONUS);
                            Constant.REFER_EARN_ACTIVE = object.getString(Constant.KEY_REFER_EARN_STATUS);
                            Constant.REFER_EARN_METHOD = object.getString(Constant.KEY_REFER_EARN_METHOD);
                            Constant.REFER_EARN_ORDER_AMOUNT = object.getString(Constant.KEY_MIN_REFER_ORDER_AMOUNT);
                            Constant.MAX_EARN_AMOUNT = object.getString(Constant.KEY_MAX_EARN_AMOUNT);
                            Constant.MINIMUM_WITHDRAW_AMOUNT = Double.parseDouble(object.getString(Constant.KEY_MIN_WIDRAWAL));
                            Constant.SETTING_CURRENCY_SYMBOL = object.getString(Constant.CURRENCY);
                            Constant.SETTING_AREA_WISE_DELIVERY_CHARGE = Double.parseDouble(object.getString(Constant.AREA_WISE_DELIVERY_CHARGE));
                            Constant.SETTING_TAX = Double.parseDouble(object.getString(Constant.TAX));
                            Constant.SETTING_DELIVERY_CHARGE = Double.parseDouble(object.getString(Constant.DELIEVERY_CHARGE));
                            Constant.SETTING_MAIL_ID = object.getString(Constant.REPLY_TO);
                            Constant.SETTING_MINIMUM_ORDER_AMOUNT = object.getDouble(Constant.MINIMUM_ORDER_AMOUNT);
                            Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY = Double.parseDouble(object.getString(Constant.MINIMUM_AMOUNT));
                            Constant.ORDER_DAY_LIMIT = Integer.parseInt(object.getString(Constant.KEY_ORDER_RETURN_DAY_LIMIT));
                            Constant.MAX_PRODUCT_LIMIT = Integer.parseInt(object.getString(Constant.MAX_CART_ITEMS_COUNT));


                            if (DrawerActivity.tvWallet != null) {
                                DrawerActivity.tvWallet.setText(activity.getResources().getString(R.string.wallet_balance) + "\t:\t" + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
                            }
                            String versionName = "";
                            try {
                                PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                                versionName = packageInfo.versionName;
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            if (ApiConfig.compareVersion(versionName, Constant.VERSION_CODE) < 0) {
                                OpenBottomDialog(activity);
                            } else if (ApiConfig.compareVersion(versionName, Constant.REQUIRED_VERSION) < 0) {
                                OpenBottomDialog(activity);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.ORDERPROCESS_URL, params, false);
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
        if (Constant.VERSION_STATUS.equals("0")) {
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

    public static void displayLocationSettingsRequest(final Activity activity) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {

                            status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAG", "PendingIntent unable to execute request.");
                        }
                        break;

                }
            }
        });
    }

    public static double getWalletBalance(final Activity activity, Session session) {

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_USER_DATA, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(boolean result, String response) {
//                System.out.println("=================*wallet " + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            Constant.WALLET_BALANCE = Double.parseDouble(object.getString(Constant.KEY_BALANCE));
                            DrawerActivity.tvWallet.setText(activity.getResources().getString(R.string.wallet_balance) + "\t:\t" + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.USER_DATA_URL, params, true);
        return Constant.WALLET_BALANCE;
    }

    public static void clearFCM(Activity activity, Session session) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.REMOVE_FCM_ID, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {

                }
            }
        }, activity, Constant.REMOVE_FCM_URL, params, false);
    }

    public static void getLocation(final Activity activity) {
        try {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }
            } else {
                gps = new GPSTracker(activity);
                if (gps.canGetLocation()) {
                    user_location = gps.getAddressLine(activity);

                }
                if (gps.getIsGPSTrackingEnabled()) {
                    latitude1 = gps.latitude;
                    longitude1 = gps.longitude;
                    new Session(activity).setData(Constant.LATITUDE, "" + gps.latitude);
                    new Session(activity).setData(Constant.LONGITUDE, "" + gps.longitude);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isGPSEnable(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return GpsStatus;
    }

    public static String getAddress(double lat, double lng, Activity activity) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        String address = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() != 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                address = add;
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


}
