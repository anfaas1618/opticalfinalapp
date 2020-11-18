package wrteam.ekart.shop.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.fragment.WebViewFragment;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.AppController;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.DatabaseHelper;
import wrteam.ekart.shop.helper.GPSTracker;
import wrteam.ekart.shop.helper.ProgressDisplay;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.Utils;
import wrteam.ekart.shop.helper.VolleyCallback;
import wrteam.ekart.shop.ui.PinView;

public class LoginActivity extends AppCompatActivity {

    ProgressDisplay progress;
    LinearLayout lytchangpsw, lytforgot, lytlogin, signUpLyt, lytotp, lytverify, lytResetPass, lytPrivacy;
    EditText edtResetPass, edtResetCPass, edtnewpsw, edtRefer, edtoldpsw, edtforgotmobile, edtloginpassword, edtLoginMobile, edtname, edtemail, edtmobile, edtpsw, edtcpsw, edtMobVerify;
    Button btnotpverify, btnEmailVerify, btnsubmit, btnResetPass;
    CountryCodePicker edtCode, edtFCode;
    String from, mobile, fromto;
    PinView edtotp;
    TextView txtmobileno, tvResend, tvSignUp, tvForgotPass, tvPrivacyPolicy, tvResendPass, tvTime;
    ScrollView scrollView;
    GPSTracker gps;
    Session session;
    Toolbar toolbar;
    CheckBox chPrivacy;
    ////Firebase
    String phoneNumber, firebase_otp, otpFor = "";
    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    DatabaseHelper databaseHelper;
    Activity activity;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity = LoginActivity.this;
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        databaseHelper = new DatabaseHelper(activity);

        from = getIntent().getStringExtra("from");
        fromto = getIntent().getStringExtra("fromto");
        mobile = getIntent().getStringExtra("txtmobile");
        firebase_otp = getIntent().getStringExtra("OTP");

        gps = new GPSTracker(activity);
        session = new Session(getApplicationContext());
        chPrivacy = findViewById(R.id.chPrivacy);
        txtmobileno = findViewById(R.id.txtmobileno);
        edtnewpsw = findViewById(R.id.edtnewpsw);
        edtoldpsw = findViewById(R.id.edtoldpsw);
        edtCode = findViewById(R.id.edtCode);
        edtFCode = findViewById(R.id.edtFCode);
        edtResetPass = findViewById(R.id.edtResetPass);
        edtResetCPass = findViewById(R.id.edtResetCPass);
        edtforgotmobile = findViewById(R.id.edtforgotmobile);
        edtloginpassword = findViewById(R.id.edtloginpassword);
        edtLoginMobile = findViewById(R.id.edtLoginMobile);
        lytchangpsw = findViewById(R.id.lytchangpsw);
        lytforgot = findViewById(R.id.lytforgot);
        lytlogin = findViewById(R.id.lytlogin);
        lytResetPass = findViewById(R.id.lytResetPass);
        lytPrivacy = findViewById(R.id.lytPrivacy);
        scrollView = findViewById(R.id.scrollView);
        edtotp = findViewById(R.id.edtotp);
        btnResetPass = findViewById(R.id.btnResetPass);
        btnsubmit = findViewById(R.id.btnsubmit);
        btnEmailVerify = findViewById(R.id.btnEmailVerify);
        btnotpverify = findViewById(R.id.btnotpverify);
        edtMobVerify = findViewById(R.id.edtMobVerify);
        lytverify = findViewById(R.id.lytverify);
        signUpLyt = findViewById(R.id.signUpLyt);
        lytotp = findViewById(R.id.lytotp);
        edtotp = findViewById(R.id.edtotp);
        edtname = findViewById(R.id.edtname);
        edtemail = findViewById(R.id.edtemail);
        edtmobile = findViewById(R.id.edtmobile);
        edtpsw = findViewById(R.id.edtpsw);
        edtcpsw = findViewById(R.id.edtcpsw);
        edtRefer = findViewById(R.id.edtRefer);
        tvResend = findViewById(R.id.tvResend);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        tvPrivacyPolicy = findViewById(R.id.tvPrivacy);

        tvResendPass = findViewById(R.id.tvResendPass);
        tvTime = findViewById(R.id.tvTime);
        tvSignUp.setText(underlineSpannable(getString(R.string.not_registered)));
        tvForgotPass.setText(underlineSpannable(getString(R.string.forgottext)));
        tvResendPass.setText(underlineSpannable(getString(R.string.resend_cap)));

        edtLoginMobile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone, 0, 0, 0);

        edtoldpsw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, R.drawable.ic_show, 0);
        edtnewpsw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, R.drawable.ic_show, 0);

        edtloginpassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, R.drawable.ic_show, 0);
        edtpsw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, R.drawable.ic_show, 0);
        edtcpsw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, R.drawable.ic_show, 0);
        edtResetPass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, R.drawable.ic_show, 0);
        edtResetCPass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, R.drawable.ic_show, 0);
        Utils.setHideShowPassword(edtpsw);
        Utils.setHideShowPassword(edtcpsw);
        Utils.setHideShowPassword(edtloginpassword);
        Utils.setHideShowPassword(edtoldpsw);
        Utils.setHideShowPassword(edtnewpsw);
        Utils.setHideShowPassword(edtResetPass);
        Utils.setHideShowPassword(edtResetCPass);

        progress = new ProgressDisplay(this);

//        edtCode.setCountryForNameCode("BD");
//        edtFCode.setCountryForNameCode("BD");
//        Constant.country_code = edtCode.getSelectedCountryCode(); // your country code
//        session.setData(Constant.COUNTRY_CODE, Constant.country_code);
//        you will find the code name of your country from : https://countrycode.org/


        if (from != null) {
            switch (from) {
                case "forgot":
                    lytforgot.setVisibility(View.VISIBLE);
                    break;
                case "changepsw":
                    lytchangpsw.setVisibility(View.VISIBLE);
                    break;
                case "reset_pass":
                    lytResetPass.setVisibility(View.VISIBLE);
                    break;
                case "register":
                    lytverify.setVisibility(View.VISIBLE);
                    break;
                case "otp_verify":
                case "otp_forgot":
                    lytotp.setVisibility(View.VISIBLE);
                    txtmobileno.setText(getResources().getString(R.string.please_type_verification_code_sent_to) + "  +" + Constant.country_code + " - " + mobile);
                    break;
                case "drawer":
                case "checkout":
                    lytlogin.setVisibility(View.VISIBLE);
                    break;
                default:
                    signUpLyt.setVisibility(View.VISIBLE);
                    edtmobile.setText(mobile);
                    edtRefer.setText(Constant.FRND_CODE);
                    break;
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            lytlogin.setVisibility(View.VISIBLE);

        }
        StartFirebaseLogin();
        PrivacyPolicy();
    }

    public void generateOTP() {
        final String mobile = edtMobVerify.getText().toString().trim();
        final String code = edtCode.getSelectedCountryCode();
        if (ApiConfig.CheckValidattion(mobile, false, false)) {
            edtMobVerify.setError(getString(R.string.enter_mobile_no));
        } else if (ApiConfig.CheckValidattion(mobile, false, true)) {
            edtMobVerify.setError(getString(R.string.enter_valid_mobile_no));
        } else if (AppController.isConnected(activity)) {
            session.setData(Constant.COUNTRY_CODE, code);
            Constant.country_code = code;
            Map<String, String> params = new HashMap<String, String>();
            params.put(Constant.TYPE, Constant.VERIFY_USER);
            params.put(Constant.MOBILE, mobile);
            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    if (result) {
                        try {
                            //System.out.println ("=================*verify  " + response);
                            JSONObject object = new JSONObject(response);
                            otpFor = "otp_verify";
                            phoneNumber = "+" + Constant.country_code + mobile;
                            if (!object.getBoolean(Constant.ERROR)) {
                                sentRequest(phoneNumber);
                            } else {
                                setSnackBar(getString(R.string.verify_alert_1) + getString(R.string.app_name) + getString(R.string.verify_alert_2), getString(R.string.btn_ok), from);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, activity, Constant.RegisterUrl, params, true);
        }
    }


    public void sentRequest(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,                     // Phone number to verify
                60,                           // Timeout duration
                TimeUnit.SECONDS,                // Unit of timeout
                activity,        // Activity (for callback binding)
                mCallback);
    }

    void StartFirebaseLogin() {
        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NotNull PhoneAuthCredential phoneAuthCredential) {
                //System.out.println ("====verification complete call  " + phoneAuthCredential.getSmsCode ());
            }

            @Override
            public void onVerificationFailed(@NotNull FirebaseException e) {
                setSnackBar(e.getLocalizedMessage(), getString(R.string.btn_ok), Constant.FAILED);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Constant.verificationCode = s;
                String mobileno = "";

                if (otpFor.equals("resend")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.otp_resend_alert), Toast.LENGTH_SHORT).show();
                } else {
                    if (otpFor.equals("otp_forgot")) {
                        mobileno = edtforgotmobile.getText().toString();
                    } else {
                        mobileno = edtMobVerify.getText().toString();
                    }
                    startActivity(new Intent(activity, LoginActivity.class)
                            .putExtra("from", otpFor)
                            .putExtra("txtmobile", mobileno)
                            .putExtra("OTP", s));
                }
            }
        };
    }

    public void ChangePassword() {
        final Session sessionpsw = new Session(activity);
        String oldpsw = edtoldpsw.getText().toString();
        String password = edtnewpsw.getText().toString();

        if (ApiConfig.CheckValidattion(oldpsw, false, false)) {
            edtoldpsw.setError(getString(R.string.enter_old_pass));
        } else if (ApiConfig.CheckValidattion(password, false, false)) {
            edtnewpsw.setError(getString(R.string.enter_new_pass));
        } else if (!oldpsw.equals(sessionpsw.getData(Session.KEY_Password))) {
            edtoldpsw.setError(getString(R.string.no_match_old_pass));
        } else if (AppController.isConnected(activity)) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put(Constant.TYPE, Constant.CHANGE_PASSWORD);
            params.put(Constant.PASSWORD, password);
            params.put(Constant.ID, sessionpsw.getData(Session.KEY_ID));

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
            // Setting Dialog Message
            alertDialog.setTitle(getString(R.string.change_pass));
            alertDialog.setMessage(getString(R.string.reset_alert_msg));
            alertDialog.setCancelable(false);
            final AlertDialog alertDialog1 = alertDialog.create();

            // Setting OK Button
            alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ApiConfig.RequestToVolley(new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result, String response) {
                            //  System.out.println("=================*changepsw " + response);
                            if (result) {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if (!object.getBoolean(Constant.ERROR)) {
                                        sessionpsw.logoutUser(activity);
                                    }
                                    Toast.makeText(activity, object.getString("message"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, activity, Constant.RegisterUrl, params, true);

                }
            });
            alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog1.dismiss();
                }
            });
            // Showing Alert Message
            alertDialog.show();
        }
    }

    public void ResetPassword() {

        String reset_psw = edtResetPass.getText().toString();
        String reset_c_psw = edtResetCPass.getText().toString();

        if (ApiConfig.CheckValidattion(reset_psw, false, false)) {
            edtResetPass.setError(getString(R.string.enter_new_pass));
        } else if (ApiConfig.CheckValidattion(reset_c_psw, false, false)) {
            edtResetCPass.setError(getString(R.string.enter_confirm_pass));
        } else if (!reset_psw.equals(reset_c_psw)) {
            edtResetPass.setError(getString(R.string.pass_not_match));
        } else if (AppController.isConnected(activity)) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put(Constant.TYPE, Constant.CHANGE_PASSWORD);
            params.put(Constant.PASSWORD, reset_c_psw);
            //params.put(Constant.ID, session.getData(Session.KEY_ID));
            params.put(Constant.ID, Constant.U_ID);

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
            // Setting Dialog Message
            alertDialog.setTitle(getString(R.string.reset_pass));
            alertDialog.setMessage(getString(R.string.reset_alert_msg));
            alertDialog.setCancelable(false);
            final AlertDialog alertDialog1 = alertDialog.create();
            // Setting OK Button
            alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ApiConfig.RequestToVolley(new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result, String response) {

                            if (result) {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if (!object.getBoolean(Constant.ERROR)) {
                                        setSnackBar(getString(R.string.msg_reset_pass_success), getString(R.string.btn_ok), from);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, activity, Constant.RegisterUrl, params, true);

                }
            });
            alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog1.dismiss();
                }
            });
            // Showing Alert Message
            alertDialog.show();
        }
    }

    public void RecoverPassword() {
        final String mobile = edtforgotmobile.getText().toString().trim();
        String code = edtFCode.getSelectedCountryCode();
        if (ApiConfig.CheckValidattion(mobile, false, false)) {
            edtforgotmobile.setError(getString(R.string.enter_mobile_no));
        } else if (mobile.length() != 0 && ApiConfig.CheckValidattion(mobile, false, true)) {
            edtforgotmobile.setError(getString(R.string.enter_valid_mobile_no));

        } else {
            session.setData(Constant.COUNTRY_CODE, code);
            Constant.country_code = code;
            Map<String, String> params = new HashMap<String, String>();
            params.put(Constant.TYPE, Constant.VERIFY_USER);
            params.put(Constant.MOBILE, mobile);
            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    if (result) {
                        try {
                            //System.out.println("=================*verify  " + response);
                            JSONObject object = new JSONObject(response);
                            otpFor = "otp_forgot";
                            phoneNumber = ("+" + Constant.country_code + mobile);
                            if (object.getBoolean(Constant.ERROR)) {
                                Constant.U_ID = object.getString(Constant.ID);
                                sentRequest(phoneNumber);
                            } else {
                                setSnackBar(getString(R.string.alert_register_num1) + getString(R.string.app_name) + getString(R.string.alert_register_num2), getString(R.string.btn_ok), from);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, activity, Constant.RegisterUrl, params, true);
        }
    }

    public void UserLogin() {
        String email = edtLoginMobile.getText().toString();
        final String password = edtloginpassword.getText().toString();

        if (ApiConfig.CheckValidattion(email, false, false)) {
            edtLoginMobile.setError(getString(R.string.enter_mobile_no));
        } else if (ApiConfig.CheckValidattion(email, false, true)) {
            edtLoginMobile.setError(getString(R.string.enter_valid_mobile_no));
        } else if (ApiConfig.CheckValidattion(password, false, false)) {
            edtloginpassword.setError(getString(R.string.enter_pass));
        } else if (AppController.isConnected(activity)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(Constant.MOBILE, email);
            params.put(Constant.PASSWORD, password);
            params.put(Constant.FCM_ID, "" + AppController.getInstance().getDeviceToken());
            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    //System.out.println ("============login res " + response);
                    if (result) {
                        try {
                            JSONObject objectbject = new JSONObject(response);
                            if (!objectbject.getBoolean(Constant.ERROR)) {
                                StartMainActivity(objectbject, password);
                            }
                            Toast.makeText(activity, objectbject.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, activity, Constant.LoginUrl, params, true);
        }
    }


    public void setSnackBar(String message, String action, final String type) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("reset_pass") || type.equals("forgot") || type.equals("register")) {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                snackbar.dismiss();
            }
        });
        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    public void OTP_Varification() {
        String otptext = edtotp.getText().toString().trim();

        if (ApiConfig.CheckValidattion(otptext, false, false)) {
            edtotp.setError(getString(R.string.enter_otp));
        } else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(firebase_otp, otptext);
            signInWithPhoneAuthCredential(credential, otptext);

        }

    }

    void signInWithPhoneAuthCredential(PhoneAuthCredential credential, final String otptext) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            if (from.equalsIgnoreCase("otp_verify")) {
                                startActivity(new Intent(activity, LoginActivity.class).putExtra("from", "info").putExtra("txtmobile", mobile).putExtra("OTP", otptext));
                            } else {
                                startActivity(new Intent(activity, LoginActivity.class).putExtra("from", "reset_pass"));
                            }
                            edtotp.setError(null);
                        } else {

                            //verification unsuccessful.. display an error message
                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            edtotp.setError(message);

                        }
                    }
                });
    }

    public void UserSignUpSubmit(String latitude, String longitude) {

        String name = edtname.getText().toString().trim();
        String email = "" + edtemail.getText().toString().trim();
        String mobile = edtmobile.getText().toString().trim();
        final String password = edtpsw.getText().toString().trim();
        String cpassword = edtcpsw.getText().toString().trim();


        if (ApiConfig.CheckValidattion(name, false, false)) {
            edtname.setError(getString(R.string.enter_name));
            scrollView.scrollTo(0, edtname.getBottom());
        } else if (ApiConfig.CheckValidattion(email, false, false))
            edtemail.setError(getString(R.string.enter_email));
        else if (ApiConfig.CheckValidattion(email, true, false)) {
            edtemail.setError(getString(R.string.enter_valid_email));
        } else if (ApiConfig.CheckValidattion(password, false, false)) {
            edtpsw.setError(getString(R.string.enter_pass));
            scrollView.scrollTo(0, edtpsw.getBottom());
        } else if (ApiConfig.CheckValidattion(cpassword, false, false)) {
            edtcpsw.setError(getString(R.string.enter_confirm_pass));
            scrollView.scrollTo(0, edtcpsw.getBottom());
        } else if (!password.equals(cpassword)) {
            edtcpsw.setError(getString(R.string.pass_not_match));
            scrollView.scrollTo(0, edtcpsw.getBottom());
        } else if (latitude.equals("0") || longitude.equals("0"))
            Toast.makeText(activity, getString(R.string.alert_select_location), Toast.LENGTH_LONG).show();
        else if (!chPrivacy.isChecked()) {
            Toast.makeText(activity, getString(R.string.alert_privacy_msg), Toast.LENGTH_LONG).show();
        } else if (AppController.isConnected(activity)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(Constant.TYPE, Constant.REGISTER);
            params.put(Constant.NAME, name);
            params.put(Constant.EMAIL, email);
            params.put(Constant.PASSWORD, password);
            params.put(Constant.COUNTRY_CODE, Constant.country_code);
            params.put(Constant.MOBILE, mobile);
            params.put(Constant.FCM_ID, "" + AppController.getInstance().getDeviceToken());
            params.put(Constant.REFERRAL_CODE, Constant.randomAlphaNumeric(8));
            params.put(Constant.FRIEND_CODE, edtRefer.getText().toString().trim());
            System.out.println("==========params " + params);
            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    System.out.println("==========params *register " + response);
                    if (result) {
                        try {
                            JSONObject objectbject = new JSONObject(response);
                            if (!objectbject.getBoolean(Constant.ERROR)) {

                                StartMainActivity(objectbject, password);

                            }
                            Toast.makeText(activity, objectbject.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, activity, Constant.RegisterUrl, params, true);
        }
    }

    public void OnBtnClick(View view) {
        int id = view.getId();

        if (id == R.id.tvSignUp) {
            startActivity(new Intent(activity, LoginActivity.class).putExtra("from", "register").putExtra("from", "register"));
        } else if (id == R.id.tvForgotPass) {
            startActivity(new Intent(activity, LoginActivity.class).putExtra("from", "forgot"));
        } else if (id == R.id.btnchangepsw) {

            ChangePassword();

        } else if (id == R.id.btnResetPass) {
            hideKeyboard(activity, view);
            ResetPassword();

        } else if (id == R.id.btnrecover) {
            hideKeyboard(activity, view);
            RecoverPassword();

        } else if (id == R.id.tvResendPass) {
            hideKeyboard(activity, view);
            RecoverPassword();

        } else if (id == R.id.btnlogin) {
            hideKeyboard(activity, view);
            UserLogin();

        } else if (id == R.id.btnEmailVerify) {
            hideKeyboard(activity, view);
            generateOTP();

        } else if (id == R.id.tvResend) {
            otpFor = "resend";
            sentRequest("+" + Constant.country_code + mobile);

        } else if (id == R.id.btnotpverify) {
            hideKeyboard(activity, view);
            OTP_Varification();

        } else if (id == R.id.btnsubmit) {
            double saveLatitude = Double.parseDouble(new Session(getApplicationContext()).getCoordinates(Session.KEY_LATITUDE));
            double saveLongitude = Double.parseDouble(new Session(getApplicationContext()).getCoordinates(Session.KEY_LONGITUDE));

            if (saveLatitude == 0 || saveLongitude == 0) {
                UserSignUpSubmit(String.valueOf(gps.latitude), String.valueOf(gps.longitude));
            } else {
                UserSignUpSubmit(new Session(getApplicationContext()).getCoordinates(Session.KEY_LATITUDE), new Session(getApplicationContext()).getCoordinates(Session.KEY_LONGITUDE));
            }
        }

    }

    public void StartMainActivity(JSONObject objectbject, String password) {
        try {
            new Session(activity).createUserLoginSession(objectbject.getString(Constant.PROFILE)
                    , AppController.getInstance().getDeviceToken(),
                    objectbject.getString(Constant.USER_ID),
                    objectbject.getString(Constant.NAME),
                    objectbject.getString(Constant.EMAIL),
                    objectbject.getString(Constant.MOBILE),
                    password,
                    objectbject.getString(Constant.REFERRAL_CODE));

            ApiConfig.AddMultipleProductInCart( session, activity, databaseHelper.getDataCartList());
            ApiConfig.getCartItemCount( activity, session);

            ArrayList<String> favorites = databaseHelper.getFavourite();
            for (int i = 0; i < favorites.size(); i++) {
                ApiConfig.AddOrRemoveFavorite(activity, session, favorites.get(i), true);
            }

            databaseHelper.DeleteAllFavoriteData();
            databaseHelper.DeleteAllOrderData();

            session.setData(Constant.COUNTRY_CODE, Constant.country_code);

            MainActivity.homeClicked = false;
            MainActivity.categoryClicked = false;
            MainActivity.favoriteClicked = false;
            MainActivity.trackingClicked = false;

            Intent intent = new Intent(activity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("from", "");
            if (fromto != null && fromto.equals("checkout")) {
                intent.putExtra("from", "checkout");
            }
            startActivity(intent);

            finish();
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public SpannableString underlineSpannable(String text) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        return spannableString;
    }

    public void PrivacyPolicy() {
        tvPrivacyPolicy.setClickable(true);
        tvPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        String message = getString(R.string.msg_privacy_terms);
        String s2 = getString(R.string.terms_conditions);
        String s1 = getString(R.string.privacy_policy);
        final Spannable wordtoSpan = new SpannableString(message);

        wordtoSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new WebViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "privacy");
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                ds.isUnderlineText();
            }
        }, message.indexOf(s1), message.indexOf(s1) + s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new WebViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "terms");
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                ds.isUnderlineText();
            }
        }, message.indexOf(s2), message.indexOf(s2) + s2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPrivacyPolicy.setText(wordtoSpan);
    }

    public void hideKeyboard(Activity activity, View root) {
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
    }

}




