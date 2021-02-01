package wrteam.ekart.shop.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.squareup.picasso.Picasso;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.fragment.AddressListFragment;
import wrteam.ekart.shop.fragment.CartFragment;
import wrteam.ekart.shop.fragment.FaqFragment;
import wrteam.ekart.shop.fragment.NotificationFragment;
import wrteam.ekart.shop.fragment.ProfileFragment;
import wrteam.ekart.shop.fragment.ReferEarnFragment;
import wrteam.ekart.shop.fragment.TransactionFragment;
import wrteam.ekart.shop.fragment.WalletTransactionFragment;
import wrteam.ekart.shop.fragment.WebViewFragment;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.ui.CircleTransform;

@SuppressLint("StaticFieldLeak")
public class DrawerActivity extends AppCompatActivity {
    public static TextView tvName, tvWallet;
    public static DrawerLayout drawer_layout;
    public static ImageView imgProfile;
    public static NavigationView navigationView;
    public ActionBarDrawerToggle drawerToggle;
    public TextView tvMobile;
    protected FrameLayout frameLayout;
    Session session;
    LinearLayout lytProfile;
    Activity activity;
    ReviewManager manager;
    Task<ReviewInfo> request;
    ReviewInfo reviewInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        frameLayout = findViewById(R.id.content_frame);
        navigationView = findViewById(R.id.nav_view);
        drawer_layout = findViewById(R.id.drawer_layout);
        View header = navigationView.getHeaderView(0);
        tvWallet = header.findViewById(R.id.tvWallet);
        tvName = header.findViewById(R.id.header_name);
        tvMobile = header.findViewById(R.id.tvMobile);
        lytProfile = header.findViewById(R.id.lytProfile);
        imgProfile = header.findViewById(R.id.imgProfile);

        activity = DrawerActivity.this;
        session = new Session(activity);

        manager = ReviewManagerFactory.create(this);
        request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
            } else {
                // There was some problem, continue regardless of the result.
            }
        });

        if (session.isUserLoggedIn()) {
            tvName.setText(session.getData(Constant.NAME));
            tvMobile.setText(session.getData(Constant.MOBILE));
            tvWallet.setVisibility(View.VISIBLE);

            Picasso.get()
                    .load(session.getData(Constant.PROFILE))
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .transform(new CircleTransform())
                    .into(imgProfile);

            ApiConfig.getWalletBalance(activity, session);

        } else {
            tvWallet.setVisibility(View.GONE);
            tvName.setText(getResources().getString(R.string.is_login));
            tvMobile.setText(getResources().getString(R.string.is_mobile));
            Picasso.get()
                    .load("-")
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.logo_login)
                    .error(R.drawable.logo_login)
                    .transform(new CircleTransform())
                    .into(imgProfile);
        }

        lytProfile.setOnClickListener(v -> {
            drawer_layout.closeDrawers();
            if (session.isUserLoggedIn())
                MainActivity.fm.beginTransaction().add(R.id.container, new ProfileFragment()).addToBackStack(null).commit();
            else
                startActivity(new Intent(getApplicationContext(), LoginActivity.class).putExtra(Constant.FROM, "drawer"));
        });
        setupNavigationDrawer();
    }


    @SuppressLint("NonConstantResourceId")
    void setupNavigationDrawer() {
        Menu nav_Menu = navigationView.getMenu();

        if (session.isUserLoggedIn()) {
            nav_Menu.findItem(R.id.menu_logout).setVisible(true);
            nav_Menu.setGroupVisible(R.id.group1, true);
            nav_Menu.setGroupVisible(R.id.group2, true);
        } else {
            nav_Menu.findItem(R.id.menu_logout).setVisible(false);
            nav_Menu.setGroupVisible(R.id.group1, false);
            nav_Menu.setGroupVisible(R.id.group2, false);
        }

        if (session.isUserLoggedIn()) {
            if (session.getData(Constant.is_refer_earn_on).equals("0")) {
                nav_Menu.findItem(R.id.menu_refer).setVisible(false);
            }
        }

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawer_layout.closeDrawers();
            Fragment fragment;
            Bundle bundle;
            switch (menuItem.getItemId()) {
                case R.id.menu_transaction_history:
                    fragment = new TransactionFragment();
                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;
                case R.id.menu_wallet_history:
                    fragment = new WalletTransactionFragment();
                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;
                case R.id.menu_notifications:
                    getSupportFragmentManager().beginTransaction().add(R.id.container, new NotificationFragment()).addToBackStack(null).commit();
                    break;
                case R.id.menu_faq:
                    getSupportFragmentManager().beginTransaction().add(R.id.container, new FaqFragment()).addToBackStack(null).commit();
                    break;
                case R.id.menu_terms:
                    fragment = new WebViewFragment();
                    bundle = new Bundle();
                    bundle.putString("type", "Terms & Conditions");
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;
                case R.id.menu_contact:
                    fragment = new WebViewFragment();
                    bundle = new Bundle();
                    bundle.putString("type", "Contact Us");
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;
                case R.id.menu_about_us:
                    fragment = new WebViewFragment();
                    bundle = new Bundle();
                    bundle.putString("type", "About Us");
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;
                case R.id.menu_privacy:
                    fragment = new WebViewFragment();
                    bundle = new Bundle();
                    bundle.putString("type", "Privacy Policy");
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;
                case R.id.menu_home:
                    MainActivity.homeClicked = false;
                    MainActivity.categoryClicked = false;
                    MainActivity.favoriteClicked = false;
                    MainActivity.trackingClicked = false;
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constant.FROM, "");
                    startActivity(intent);
                    finish();
                    break;
                case R.id.menu_tracker:
                    startActivity(new Intent(activity, MainActivity.class).putExtra(Constant.FROM, "tracker").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.menu_refer:
                    if (session.isUserLoggedIn())
                        getSupportFragmentManager().beginTransaction().add(R.id.container, new ReferEarnFragment()).addToBackStack(null).commit();
                    else
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    break;
                case R.id.menu_manage_address:
                    fragment = new AddressListFragment();
                    bundle = new Bundle();
                    bundle.putString(Constant.FROM, "MainActivity");
                    fragment.setArguments(bundle);
                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;
                case R.id.menu_cart:
                    fragment = new CartFragment();
                    bundle = new Bundle();
                    bundle.putString(Constant.FROM, "mainActivity");
                    fragment.setArguments(bundle);
                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;

                case R.id.menu_change_pass:
                    Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                    if (session.isUserLoggedIn())
                        intent1.putExtra(Constant.FROM, "changepsw");
                    startActivity(intent1);
                    break;
                case R.id.menu_share:
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.take_a_look) + "\"" + getString(R.string.app_name) + "\" - " + Constant.PLAY_STORE_LINK + getPackageName());
                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
                    break;
                case R.id.menu_rate:
                    Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);
                    flow.addOnCompleteListener(task -> {
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                    });
                    break;
                case R.id.menu_logout:
                    session.logoutUserConfirmation(activity);
                    ApiConfig.clearFCM(activity, session);
                    break;
            }

            return true;
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
}
