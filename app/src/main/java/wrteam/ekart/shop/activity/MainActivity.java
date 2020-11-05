package wrteam.ekart.shop.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.razorpay.PaymentResultListener;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.fragment.CartFragment;
import wrteam.ekart.shop.fragment.CategoryFragment;
import wrteam.ekart.shop.fragment.CheckoutFragment;
import wrteam.ekart.shop.fragment.FavoriteFragment;
import wrteam.ekart.shop.fragment.HomeFragment;
import wrteam.ekart.shop.fragment.OrderPlacedFragment;
import wrteam.ekart.shop.fragment.PaymentFragment;
import wrteam.ekart.shop.fragment.ProductDetailFragment;
import wrteam.ekart.shop.fragment.SearchFragment;
import wrteam.ekart.shop.fragment.SubCategoryFragment;
import wrteam.ekart.shop.fragment.TrackOrderFragment;
import wrteam.ekart.shop.fragment.TrackerDetailFragment;
import wrteam.ekart.shop.fragment.WalletTransactionFragment;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.DatabaseHelper;
import wrteam.ekart.shop.helper.Session;

import static wrteam.ekart.shop.helper.ApiConfig.GetSettings;


public class MainActivity extends DrawerActivity implements OnMapReadyCallback, PaymentResultListener {

    private static final String TAG = "MAIN ACTIVITY";
    public static Toolbar toolbar;
    public static BottomNavigationView bottomNavigationView;
    public static Fragment active;
    public static FragmentManager fm = null;
    public static Fragment homeFragment, categoryFragment, favoriteFragment, trackOrderFragment;
    public static boolean homeClicked = false, categoryClicked = false, favoriteClicked = false, trackingClicked = false;
    public static Activity activity;
    public static Session session;
    boolean doubleBackToExitPressedOnce = false;
    Menu menu;
    DatabaseHelper databaseHelper;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        activity = MainActivity.this;
        session = new Session(activity);
        from = getIntent().getStringExtra("from");
        databaseHelper = new DatabaseHelper(activity);


        if (session.isUserLoggedIn()) {
            ApiConfig.getCartItemCount(activity, session);
        } else {
            databaseHelper.getTotalItemOfCart(activity);
        }

//        setAppLocal("en"); //Change you language code here

        fm = getSupportFragmentManager();

        homeFragment = new HomeFragment();
        categoryFragment = new CategoryFragment();
        favoriteFragment = new FavoriteFragment();
        trackOrderFragment = new TrackOrderFragment();

        if (from.equals("track_order")) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_track_order);
            active = trackOrderFragment;
            trackingClicked = true;
            homeClicked = false;
            favoriteClicked = false;
            categoryClicked = false;
            fm.beginTransaction().add(R.id.container, trackOrderFragment).commit();
        } else if (!from.equals("splash")) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            active = homeFragment;
            homeClicked = true;
            trackingClicked = false;
            favoriteClicked = false;
            categoryClicked = false;
            fm.beginTransaction().add(R.id.container, homeFragment).commit();
        }


        DrawerActivity.imgProfile.setImageUrl(session.getData(Constant.PROFILE), Constant.imageLoader);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        if (!homeClicked) {
                            fm.beginTransaction().add(R.id.container, homeFragment).show(homeFragment).hide(active).commit();
                            homeClicked = true;
                        } else {
                            fm.beginTransaction().show(homeFragment).hide(active).commit();
                        }
                        active = homeFragment;
                        return true;
                    case R.id.navigation_category:
                        if (!categoryClicked) {
                            fm.beginTransaction().add(R.id.container, categoryFragment).show(categoryFragment).hide(active).commit();
                            categoryClicked = true;
                        } else {
                            fm.beginTransaction().show(categoryFragment).hide(active).commit();
                        }
                        active = categoryFragment;
                        return true;

                    case R.id.navigation_favorite:
                        if (!favoriteClicked) {
                            fm.beginTransaction().add(R.id.container, favoriteFragment).show(favoriteFragment).hide(active).commit();
                            favoriteClicked = true;
                        } else {
                            fm.beginTransaction().show(favoriteFragment).hide(active).commit();
                        }
                        active = favoriteFragment;
                        return true;

                    case R.id.navigation_track_order:
                        if (session.isUserLoggedIn()) {
                            if (!trackingClicked) {
                                fm.beginTransaction().add(R.id.container, trackOrderFragment).show(trackOrderFragment).hide(active).commit();
                                trackingClicked = true;
                            } else {
                                fm.beginTransaction().show(trackOrderFragment).hide(active).commit();
                            }
                            active = trackOrderFragment;
                        } else {
                            Toast.makeText(activity, getString(R.string.track_login_msg), Toast.LENGTH_SHORT).show();
                        }
                        return true;
                }
                return false;
            }
        });

        switch (from) {
            case "checkout":
                bottomNavigationView.setVisibility(View.GONE);
                ApiConfig.getCartItemCount(activity, session);
                fm.beginTransaction().add(R.id.container, new CheckoutFragment()).addToBackStack(null).commit();

                break;
            case "share":
            case "product": {
                Fragment fragment = new ProductDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("vpos", getIntent().getIntExtra("vpos", 0));
                bundle.putString("id", getIntent().getStringExtra("id"));
                bundle.putString("from", "share");
                fragment.setArguments(bundle);
                fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                break;
            }
            case "category": {
                Fragment fragment = new SubCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", getIntent().getStringExtra("id"));
                bundle.putString("name", getIntent().getStringExtra("name"));
                fragment.setArguments(bundle);
                fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                break;
            }
            case "order": {
                Fragment fragment = new TrackerDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("model", "");
                bundle.putString("id", getIntent().getStringExtra("id"));
                fragment.setArguments(bundle);
                fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                break;
            }
            case "payment_success": {
                fm.beginTransaction().add(R.id.container, new OrderPlacedFragment()).addToBackStack(null).commit();
                break;
            }
            case "wallet": {
                fm.beginTransaction().add(R.id.container, new WalletTransactionFragment()).addToBackStack(null).commit();
                break;
            }
        }

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.navigation_home:
                    case R.id.navigation_track_order:
                    case R.id.navigation_favorite:
                    case R.id.navigation_category:
                        break;
                }
            }
        });

        drawerToggle = new ActionBarDrawerToggle
                (
                        this,
                        drawer_layout, toolbar,
                        R.string.drawer_open,
                        R.string.drawer_close
                ) {
        };

        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                ApiConfig.updateNavItemCounter(DrawerActivity.navigationView, R.id.menu_transaction_history, Session.getCount(Constant.UNREAD_TRANSACTION_COUNT, getApplicationContext()));
                ApiConfig.updateNavItemCounter(DrawerActivity.navigationView, R.id.menu_wallet_history, Session.getCount(Constant.UNREAD_WALLET_COUNT, getApplicationContext()));
                ApiConfig.updateNavItemCounter(DrawerActivity.navigationView, R.id.menu_notifications, Session.getCount(Constant.UNREAD_NOTIFICATION_COUNT, getApplicationContext()));

                Fragment currentFragment = fm.findFragmentById(R.id.container);
                currentFragment.onResume();
            }
        });

        GetSettings(activity);

    }

//    public void setAppLocal(String languageCode){
//        Resources resources = getResources ();
//        DisplayMetrics dm = resources.getDisplayMetrics ();
//        Configuration configuration = resources.getConfiguration ();
//        configuration.setLocale (new Locale(languageCode.toLowerCase ()));
//        resources.updateConfiguration (configuration,dm);
//    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(navigationView))
            drawer_layout.closeDrawers();
        else
            doubleBack();
    }

    public void doubleBack() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        if (fm.getBackStackEntryCount() == 0) {
            if (active != homeFragment) {

                this.doubleBackToExitPressedOnce = false;
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                homeClicked = true;
                fm.beginTransaction().hide(active).show(homeFragment).commit();
                active = homeFragment;
            } else {
                Toast.makeText(this, getString(R.string.exit_msg), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_cart:
                MainActivity.fm.beginTransaction().add(R.id.container, new CartFragment()).addToBackStack(null).commit();
                break;
            case R.id.toolbar_search:
                MainActivity.fm.beginTransaction().add(R.id.container, new SearchFragment()).addToBackStack(null).commit();
                break;
            case R.id.toolbar_logout:
                session.logoutUser(activity);
                ApiConfig.clearFCM(activity, session);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_search).setVisible(true);
        menu.findItem(R.id.toolbar_cart).setIcon(ApiConfig.buildCounterDrawable(Constant.TOTAL_CART_ITEM, R.drawable.ic_cart, activity));
        invalidateOptionsMenu();

        if (fm.getBackStackEntryCount() > 0) {

            bottomNavigationView.setVisibility(View.GONE);

            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setTitle(Constant.TOOLBAR_TITLE);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fm.popBackStack();
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    DrawerActivity.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
            }, 500);
        } else {
            DrawerActivity.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            bottomNavigationView.setVisibility(View.VISIBLE);
            toolbar.setNavigationIcon(R.drawable.ic_menu);
            toolbar.setTitle(getString(R.string.app_name));
            drawerToggle = new ActionBarDrawerToggle
                    (
                            this,
                            drawer_layout, toolbar,
                            R.string.drawer_open,
                            R.string.drawer_close
                    ) {
            };
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.clear();
        LatLng latLng = new LatLng(Double.parseDouble(session.getCoordinates(Session.KEY_LATITUDE)), Double.parseDouble(session.getCoordinates(Session.KEY_LONGITUDE)));
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Current Location"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(19));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        fragment.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            if (WalletTransactionFragment.payFromWallet) {
                WalletTransactionFragment.payFromWallet = false;
                new WalletTransactionFragment().AddWalletBalance(activity, new Session(activity), WalletTransactionFragment.amount, WalletTransactionFragment.msg, razorpayPaymentID);
            } else {
                PaymentFragment.razorPayId = razorpayPaymentID;
                new PaymentFragment().PlaceOrder(MainActivity.this, PaymentFragment.paymentMethod, PaymentFragment.razorPayId, true, PaymentFragment.sendparams, "Success");
            }
        } catch (Exception e) {
            Log.d(TAG, "onPaymentSuccess  ", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            if (!WalletTransactionFragment.payFromWallet) {
                new PaymentFragment().PlaceOrder(MainActivity.this, PaymentFragment.paymentMethod, "", false, PaymentFragment.sendparams, "Failed");
            }
            Toast.makeText(activity, getString(R.string.order_cancel), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d(TAG, "onPaymentError  ", e);
        }
    }

    @Override
    protected void onResume() {
        ApiConfig.updateNavItemCounter(DrawerActivity.navigationView, R.id.menu_transaction_history, Session.getCount(Constant.UNREAD_TRANSACTION_COUNT, getApplicationContext()));
        ApiConfig.updateNavItemCounter(DrawerActivity.navigationView, R.id.menu_wallet_history, Session.getCount(Constant.UNREAD_WALLET_COUNT, getApplicationContext()));
        ApiConfig.updateNavItemCounter(DrawerActivity.navigationView, R.id.menu_notifications, Session.getCount(Constant.UNREAD_NOTIFICATION_COUNT, getApplicationContext()));
        super.onResume();
    }
}