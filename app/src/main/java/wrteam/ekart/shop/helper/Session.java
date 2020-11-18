package wrteam.ekart.shop.helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AlertDialog;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.MainActivity;


public class Session {
    public static final String PREFER_NAME = "eKart";
    public static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public static final String KEY_ID = "id";
    public static final String KEY_FCM_ID = "fcm_id";
    public static final String KEY_EMAIL = "txtemail";
    public static final String KEY_MOBILE = "mobileno";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_CITY_ID = "city_id";
    public static final String KEY_AREA_ID = "area_id";
    public static final String KEY_PINCODE = "pincode";
    public static final String KEY_Password = "password";
    public static final String KEY_REFER_CODE = "refer_code";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;


    public Session(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static void setCount(String id, int value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(id, value);
        editor.apply();
    }

    public static int getCount(String id, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(id, 0);
    }

    public String getData(String id) {
        return pref.getString(id, "");
    }

    public String getCoordinates(String id) {
        return pref.getString(id, "0");
    }

    public void setData(String id, String val) {
        editor.putString(id, val);
        editor.commit();
    }

    public void createUserLoginSession(String profile, String fcmId, String id, String name, String email, String mobile, String password, String referCode) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_FCM_ID, fcmId);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_Password, password);
        editor.putString(KEY_REFER_CODE, referCode);
        editor.putString(Constant.PROFILE, profile);
        editor.commit();
    }

//String dob, String city, String area, String cityId, String areaId, String address, String pincode, String status, String apikey, String latitude, String longitude,String createdat, ;
//editor.putString(KEY_LATITUDE, latitude);
//editor.putString(KEY_LONGITUDE, longitude);
//editor.putString(KEY_DOB, dob);
//editor.putString(KEY_ADDRESS, address);
//editor.putString(KEY_PINCODE, pincode);
//editor.putString(KEY_STATUS, status);
//editor.putString(KEY_CREATEDAT, createdat);
//editor.putString(KEY_APIKEY, apikey);
//editor.putString(KEY_CITY, city);
//editor.putString(KEY_AREA, area);
//editor.putString(KEY_CITY_ID, cityId);
//editor.putString(KEY_AREA_ID, areaId);

    public void logoutUser(Activity activity) {

        editor.clear();
        editor.commit();

        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("from", "");
        activity.startActivity(intent);

    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }


    public void logoutUserConfirmation(final Activity activity) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);
        // Setting Dialog Message
        alertDialog.setTitle(R.string.logout);
        alertDialog.setMessage(R.string.logout_msg);
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                editor.clear();
                editor.commit();

                Intent i = new Intent(activity, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(i);
                activity.finish();
            }
        });
        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();

    }

}