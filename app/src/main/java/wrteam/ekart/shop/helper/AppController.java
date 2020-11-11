package wrteam.ekart.shop.helper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import co.paystack.android.PaystackSdk;
import wrteam.ekart.shop.R;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    static AppController mInstance;
    AppEnvironment appEnvironment;
    RequestQueue mRequestQueue;
    SharedPreferences sharedPref;
    com.android.volley.toolbox.ImageLoader mImageLoader;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static Boolean isConnected(final Activity activity) {
        Boolean check = false;
        ConnectivityManager ConnectionManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        Session session = new Session(activity);

        if (networkInfo != null && networkInfo.isConnected() == true) {
            check = true;
        } else {
            Toast.makeText(activity, activity.getString(R.string.no_internet_connection_try_later), Toast.LENGTH_SHORT).show();
        }
        return check;
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

    public com.android.volley.toolbox.ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, new BitmapCache());
        }
        return this.mImageLoader;
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
