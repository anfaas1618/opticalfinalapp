package wrteam.ekart.shop.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.Session;

public class SplashActivity extends AppCompatActivity {

    Session session;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {

            switch (data.getPath().split("/")[1]) {
                case "product": // Handle the item detail deep link
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", data.getPath().split("/")[2]);
                    intent.putExtra(Constant.FROM, "share");
                    intent.putExtra("vpos", 0);
                    startActivity(intent);
                    finish();
                    break;

                case "refer": // Handle the item detail deep link
                    Constant.FRND_CODE = data.getPath().split("/")[2];
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", Constant.FRND_CODE);
                    assert clipboard != null;
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(SplashActivity.this, R.string.refer_code_copied, Toast.LENGTH_LONG).show();

                    Intent referIntent = new Intent(this, LoginActivity.class);
                    referIntent.putExtra(Constant.FROM, "register");
                    startActivity(referIntent);
                    finish();
                    break;
            }
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_splash);
            activity = SplashActivity.this;
            session = new Session(activity);
            ApiConfig.GetSettings(activity);

            int SPLASH_TIME_OUT = 1500;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constant.FROM, "");
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_TIME_OUT);

        }
    }


}
