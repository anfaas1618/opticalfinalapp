package wrteam.ekart.shop.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.razorpay.Checkout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.MainActivity;
import wrteam.ekart.shop.activity.MidtransActivity;
import wrteam.ekart.shop.activity.PayPalWebActivity;
import wrteam.ekart.shop.activity.PayStackActivity;
import wrteam.ekart.shop.activity.StripeActivity;
import wrteam.ekart.shop.adapter.DateAdapter;
import wrteam.ekart.shop.adapter.SlotAdapter;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.PaymentModelClass;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.VolleyCallback;
import wrteam.ekart.shop.model.BookingDate;
import wrteam.ekart.shop.model.Slot;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static wrteam.ekart.shop.helper.ApiConfig.GetTimeSlotConfig;

public class PaymentFragment extends Fragment {
    public static String razorPayId, paymentMethod = "", deliveryTime = "", deliveryDay = "", pCode = "", TAG = CheckoutFragment.class.getSimpleName();
    public static Map<String, String> sendparams;
    public static RecyclerView recyclerView;
    public static SlotAdapter adapter;
    public LinearLayout paymentLyt, deliveryTimeLyt, lytPayOption, lytTax, lytOrderList, lytCLocation, processLyt, lytFlutterWave, CODLinearLyt, lytPayU, lytPayPal, lytRazorPay, lytPayStack, lytMidTrans, lytStripe;
    public ArrayList<String> variantIdList, qtyList, dateList;
    TextView tvSubTotal, txttotalitems, tvSelectDeliveryDate, tvWltBalance, tvProceedOrder, tvConfirmOrder, tvPayment, tvDelivery;
    double subtotal = 0.0, usedBalance = 0.0, totalAfterTax = 0.0, taxAmt = 0.0, pCodeDiscount = 0.0;
    RadioButton rbCod, rbPayU, rbPayPal, rbRazorPay, rbPayStack, rbFlutterWave, rbMidTrans, rbStripe;
    PaymentModelClass paymentModelClass;
    ArrayList<BookingDate> bookingDates;
    RelativeLayout confirmLyt, lytWallet;
    RecyclerView recyclerViewDates;
    Calendar StartDate, EndDate;
    ScrollView scrollPaymentLyt;
    ArrayList<Slot> slotList;
    DateAdapter dateAdapter;
    int mYear, mMonth, mDay;
    String address = null;
    ImageView imgRefresh;
    Activity activity;
    CheckBox chWallet;
    ProgressBar pBar;
    Button btnApply;
    Session session;
    double total;
    View root;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_payment, container, false);
        activity = getActivity();
        paymentModelClass = new PaymentModelClass(activity);
        Constant.selectedDatePosition = 0;
        session = new Session(getActivity());
        getAllWidgets(root);
        setHasOptionsMenu(true);
        total = getArguments().getDouble("total");
        subtotal = getArguments().getDouble("subtotal");
        taxAmt = getArguments().getDouble("taxAmt");
        Constant.SETTING_TAX = getArguments().getDouble("tax");
        pCodeDiscount = getArguments().getDouble("pCodeDiscount");
        pCode = getArguments().getString("pCode");
        address = getArguments().getString("address");
        variantIdList = getArguments().getStringArrayList("variantIdList");
        qtyList = getArguments().getStringArrayList("qtyList");

        tvSubTotal.setText(Constant.systemSettings.getCurrency() + Constant.formater.format(subtotal));
        txttotalitems.setText(Constant.TOTAL_CART_ITEM + " Items");

        if (ApiConfig.isConnected(getActivity())) {
            ApiConfig.getWalletBalance(getActivity(), session);

            GetPaymentConfig();
            chWallet.setTag("false");
            tvWltBalance.setText("Total Balance: " + Constant.systemSettings.getCurrency() + Constant.formater.format(Constant.WALLET_BALANCE));
            if (Constant.WALLET_BALANCE == 0) {
                lytWallet.setVisibility(View.GONE);
            } else {
                lytWallet.setVisibility(View.VISIBLE);
            }

            tvProceedOrder.setOnClickListener(v -> PlaceOrderProcess());

            chWallet.setOnClickListener(view -> {
                if (chWallet.getTag().equals("false")) {
                    chWallet.setChecked(true);
                    lytWallet.setVisibility(View.VISIBLE);

                    if (Constant.WALLET_BALANCE >= subtotal) {
                        usedBalance = subtotal;
                        tvWltBalance.setText(getString(R.string.remaining_wallet_balance) + Constant.systemSettings.getCurrency() + Constant.formater.format((Constant.WALLET_BALANCE - usedBalance)));
                        paymentMethod = Constant.WALLET;
                        lytPayOption.setVisibility(View.GONE);
                    } else {
                        usedBalance = Constant.WALLET_BALANCE;
                        tvWltBalance.setText(getString(R.string.remaining_wallet_balance) + Constant.systemSettings.getCurrency() + "0.00");
                        lytPayOption.setVisibility(View.VISIBLE);
                    }
                    subtotal = (subtotal - usedBalance);
                    tvSubTotal.setText(Constant.systemSettings.getCurrency() + Constant.formater.format(subtotal));
                    chWallet.setTag("true");

                } else {
                    walletUncheck();
                }

            });

        }
        confirmLyt.setVisibility(View.VISIBLE);
        scrollPaymentLyt.setVisibility(View.VISIBLE);


        return root;
    }

    public void getAllWidgets(View root) {
        recyclerView = root.findViewById(R.id.recyclerView);
        pBar = root.findViewById(R.id.pBar);
        lytTax = root.findViewById(R.id.lytTax);


        lytPayStack = root.findViewById(R.id.lytPayStack);
        rbPayStack = root.findViewById(R.id.rbPayStack);
        rbFlutterWave = root.findViewById(R.id.rbFlutterWave);
        rbCod = root.findViewById(R.id.rbcod);
        rbPayU = root.findViewById(R.id.rbPayU);
        rbPayPal = root.findViewById(R.id.rbPayPal);
        rbRazorPay = root.findViewById(R.id.rbRazorPay);
        rbMidTrans = root.findViewById(R.id.rbMidTrans);
        rbStripe = root.findViewById(R.id.rbStripe);
        lytPayPal = root.findViewById(R.id.lytPayPal);
        lytRazorPay = root.findViewById(R.id.lytRazorPay);
        lytPayU = root.findViewById(R.id.lytPayU);
        CODLinearLyt = root.findViewById(R.id.CODLinearLyt);
        lytFlutterWave = root.findViewById(R.id.lytFlutterWave);
        lytMidTrans = root.findViewById(R.id.lytMidTrans);
        lytStripe = root.findViewById(R.id.lytStripe);


        tvDelivery = root.findViewById(R.id.tvSummary);
        tvPayment = root.findViewById(R.id.tvPayment);
        chWallet = root.findViewById(R.id.chWallet);
        lytPayOption = root.findViewById(R.id.lytPayOption);
        lytOrderList = root.findViewById(R.id.lytOrderList);
        lytCLocation = root.findViewById(R.id.lytCLocation);
        lytWallet = root.findViewById(R.id.lytWallet);
        paymentLyt = root.findViewById(R.id.paymentLyt);
        tvProceedOrder = root.findViewById(R.id.tvProceedOrder);
        tvConfirmOrder = root.findViewById(R.id.tvConfirmOrder);
        processLyt = root.findViewById(R.id.processLyt);
        tvSelectDeliveryDate = root.findViewById(R.id.tvSelectDeliveryDate);
        deliveryTimeLyt = root.findViewById(R.id.deliveryTimeLyt);
        imgRefresh = root.findViewById(R.id.imgRefresh);
        recyclerViewDates = root.findViewById(R.id.recyclerViewDates);
        tvSubTotal = root.findViewById(R.id.tvSubTotal);
        txttotalitems = root.findViewById(R.id.txttotalitems);
        confirmLyt = root.findViewById(R.id.confirmLyt);
        scrollPaymentLyt = root.findViewById(R.id.scrollPaymentLyt);
        tvWltBalance = root.findViewById(R.id.tvWltBalance);
        btnApply = root.findViewById(R.id.btnApply);
    }

    public void GetPaymentConfig() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SETTINGS, Constant.GetVal);
        params.put(Constant.GET_PAYMENT_METHOD, Constant.GetVal);
        //  System.out.println("=====params " + params.toString());
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            if (objectbject.has(Constant.PAYMENT_METHODS)) {
                                JSONObject object = objectbject.getJSONObject(Constant.PAYMENT_METHODS);
                                if (object.has(Constant.cod_payment_method)) {
                                    Constant.COD = object.getString(Constant.cod_payment_method);
                                }
                                if (object.has(Constant.payu_method)) {
                                    Constant.PAYUMONEY = object.getString(Constant.payu_method);
                                    Constant.MERCHANT_KEY = object.getString(Constant.PAY_M_KEY);
                                    Constant.MERCHANT_ID = object.getString(Constant.PAYU_M_ID);
                                    Constant.MERCHANT_SALT = object.getString(Constant.PAYU_SALT);
                                }
                                if (object.has(Constant.razor_pay_method)) {
                                    Constant.RAZORPAY = object.getString(Constant.razor_pay_method);
                                    Constant.RAZOR_PAY_KEY_VALUE = object.getString(Constant.RAZOR_PAY_KEY);
                                }
                                if (object.has(Constant.paypal_method)) {
                                    Constant.PAYPAL = object.getString(Constant.paypal_method);
                                }
                                if (object.has(Constant.paystack_method)) {
                                    Constant.PAYSTACK = object.getString(Constant.paystack_method);
                                    Constant.PAYSTACK_KEY = object.getString(Constant.paystack_public_key);
                                }
                                if (object.has(Constant.flutterwave_payment_method)) {
                                    Constant.FLUTTERWAVE = object.getString(Constant.flutterwave_payment_method);
                                    Constant.FLUTTERWAVE_ENCRYPTION_KEY_VAL = object.getString(Constant.flutterwave_encryption_key);
                                    Constant.FLUTTERWAVE_PUBLIC_KEY_VAL = object.getString(Constant.flutterwave_public_key);
                                    Constant.FLUTTERWAVE_SECRET_KEY_VAL = object.getString(Constant.flutterwave_secret_key);
                                    Constant.FLUTTERWAVE_SECRET_KEY_VAL = object.getString(Constant.flutterwave_secret_key);
                                    Constant.FLUTTERWAVE_CURRENCY_CODE_VAL = object.getString(Constant.flutterwave_currency_code);
                                }
                                if (object.has(Constant.midtrans_payment_method)) {
                                    Constant.MIDTRANS = object.getString(Constant.midtrans_payment_method);
                                }
                                if (object.has(Constant.stripe_payment_method)) {
                                    Constant.STRIPE = object.getString(Constant.stripe_payment_method);
                                }

                                setPaymentMethod();
                            } else {
                                Toast.makeText(activity, getString(R.string.alert_payment_methods_blank), Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.SETTING_URL, params, false);
    }

    public void setPaymentMethod() {
        if (subtotal > 0) {
            if (Constant.FLUTTERWAVE.equals("0") && Constant.PAYPAL.equals("0") && Constant.PAYUMONEY.equals("0") && Constant.COD.equals("0") && Constant.RAZORPAY.equals("0") && Constant.PAYSTACK.equals("0") && Constant.MIDTRANS.equals("0") && Constant.STRIPE.equals("0")) {
                lytPayOption.setVisibility(View.GONE);
            } else {
                lytPayOption.setVisibility(View.VISIBLE);

                if (Constant.COD.equals("1")) {
                    CODLinearLyt.setVisibility(View.VISIBLE);
                }
                if (Constant.PAYUMONEY.equals("1")) {
                    lytPayU.setVisibility(View.VISIBLE);
                }
                if (Constant.RAZORPAY.equals("1")) {
                    lytRazorPay.setVisibility(View.VISIBLE);
                }
                if (Constant.PAYSTACK.equals("1")) {
                    lytPayStack.setVisibility(View.VISIBLE);
                }
                if (Constant.FLUTTERWAVE.equals("1")) {
                    lytFlutterWave.setVisibility(View.VISIBLE);
                }
                if (Constant.PAYPAL.equals("1")) {
                    lytPayPal.setVisibility(View.VISIBLE);
                }
                if (Constant.MIDTRANS.equals("1")) {
                    lytMidTrans.setVisibility(View.VISIBLE);
                }
                if (Constant.STRIPE.equals("1")) {
                    lytStripe.setVisibility(View.VISIBLE);
                }

                rbCod.setOnClickListener(v -> {
                    rbCod.setChecked(true);
                    rbPayU.setChecked(false);
                    rbPayPal.setChecked(false);
                    rbRazorPay.setChecked(false);
                    rbPayStack.setChecked(false);
                    rbFlutterWave.setChecked(false);
                    rbStripe.setChecked(false);
                    rbMidTrans.setChecked(false);
                    paymentMethod = rbCod.getTag().toString();

                });
                rbPayU.setOnClickListener(v -> {
                    rbCod.setChecked(false);
                    rbPayU.setChecked(true);
                    rbPayPal.setChecked(false);
                    rbRazorPay.setChecked(false);
                    rbPayStack.setChecked(false);
                    rbFlutterWave.setChecked(false);
                    rbStripe.setChecked(false);
                    rbMidTrans.setChecked(false);
                    paymentMethod = rbPayU.getTag().toString();

                });

                rbPayPal.setOnClickListener(v -> {
                    rbCod.setChecked(false);
                    rbPayU.setChecked(false);
                    rbPayPal.setChecked(true);
                    rbRazorPay.setChecked(false);
                    rbPayStack.setChecked(false);
                    rbFlutterWave.setChecked(false);
                    rbStripe.setChecked(false);
                    rbMidTrans.setChecked(false);
                    paymentMethod = rbPayPal.getTag().toString();

                });

                rbRazorPay.setOnClickListener(v -> {
                    rbCod.setChecked(false);
                    rbPayU.setChecked(false);
                    rbPayPal.setChecked(false);
                    rbRazorPay.setChecked(true);
                    rbPayStack.setChecked(false);
                    rbFlutterWave.setChecked(false);
                    rbStripe.setChecked(false);
                    rbMidTrans.setChecked(false);
                    paymentMethod = rbRazorPay.getTag().toString();
                    Checkout.preload(getContext());
                });

                rbPayStack.setOnClickListener(v -> {
                    rbCod.setChecked(false);
                    rbPayU.setChecked(false);
                    rbPayPal.setChecked(false);
                    rbRazorPay.setChecked(false);
                    rbPayStack.setChecked(true);
                    rbFlutterWave.setChecked(false);
                    rbStripe.setChecked(false);
                    rbMidTrans.setChecked(false);
                    paymentMethod = rbPayStack.getTag().toString();

                });

                rbFlutterWave.setOnClickListener(v -> {
                    rbCod.setChecked(false);
                    rbPayU.setChecked(false);
                    rbPayPal.setChecked(false);
                    rbRazorPay.setChecked(false);
                    rbPayStack.setChecked(false);
                    rbFlutterWave.setChecked(true);
                    rbStripe.setChecked(false);
                    rbMidTrans.setChecked(false);
                    paymentMethod = rbFlutterWave.getTag().toString();

                });

                rbStripe.setOnClickListener(v -> {
                    rbCod.setChecked(false);
                    rbPayU.setChecked(false);
                    rbPayPal.setChecked(false);
                    rbRazorPay.setChecked(false);
                    rbPayStack.setChecked(false);
                    rbFlutterWave.setChecked(false);
                    rbStripe.setChecked(true);
                    rbMidTrans.setChecked(false);
                    paymentMethod = rbStripe.getTag().toString();

                });

                rbMidTrans.setOnClickListener(v -> {
                    rbCod.setChecked(false);
                    rbPayU.setChecked(false);
                    rbPayPal.setChecked(false);
                    rbRazorPay.setChecked(false);
                    rbPayStack.setChecked(false);
                    rbFlutterWave.setChecked(false);
                    rbStripe.setChecked(false);
                    rbMidTrans.setChecked(true);
                    paymentMethod = rbMidTrans.getTag().toString();

                });
            }

            getTimeSlots();
        } else {
            lytWallet.setVisibility(View.GONE);
            lytPayOption.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    public void walletUncheck() {
        paymentMethod = "";

        rbCod.setChecked(false);
        rbPayU.setChecked(false);
        rbPayPal.setChecked(false);
        rbRazorPay.setChecked(false);
        rbPayStack.setChecked(false);
        rbFlutterWave.setChecked(false);

        lytPayOption.setVisibility(View.VISIBLE);
        tvWltBalance.setText(getString(R.string.total) + Constant.systemSettings.getCurrency() + Constant.WALLET_BALANCE);
        subtotal = (subtotal + usedBalance);
        tvSubTotal.setText(Constant.systemSettings.getCurrency() + Constant.formater.format(subtotal));
        chWallet.setChecked(false);
        chWallet.setTag("false");
    }

    public void getTimeSlots() {
        GetTimeSlotConfig(session, getActivity());
        GetTimeSlots();

        if (session.getData(Constant.IS_TIME_SLOTS_ENABLE).equals(Constant.GetVal)) {

            deliveryTimeLyt.setVisibility(View.VISIBLE);

            StartDate = Calendar.getInstance();
            EndDate = Calendar.getInstance();
            mYear = StartDate.get(Calendar.YEAR);
            mMonth = StartDate.get(Calendar.MONTH);
            mDay = StartDate.get(Calendar.DAY_OF_MONTH);

            int DeliveryStartFrom = Integer.parseInt(session.getData(Constant.DELIVERY_STARTS_FROM)) - 1;
            int DeliveryAllowFrom = Integer.parseInt(session.getData(Constant.ALLOWED_DAYS));

            StartDate.add(Calendar.DATE, DeliveryStartFrom);

            EndDate.add(Calendar.DATE, (DeliveryStartFrom + DeliveryAllowFrom));

            dateList = ApiConfig.getDates(StartDate.get(Calendar.DATE) + "-" + (StartDate.get(Calendar.MONTH) + 1) + "-" + StartDate.get(Calendar.YEAR), EndDate.get(Calendar.DATE) + "-" + (EndDate.get(Calendar.MONTH) + 1) + "-" + EndDate.get(Calendar.YEAR));
            setDateList(dateList);

        } else {
            deliveryTimeLyt.setVisibility(View.GONE);
            deliveryDay = "Date : N/A";
            deliveryTime = "Time : N/A";

        }
    }

    public void setDateList(ArrayList<String> datesList) {
        bookingDates = new ArrayList<>();
        for (int i = 0; i < datesList.size(); i++) {
            String[] date = datesList.get(i).split("-");

            BookingDate bookingDate1 = new BookingDate();
            bookingDate1.setDate(date[0]);
            bookingDate1.setMonth(date[1]);
            bookingDate1.setYear(date[2]);
            bookingDate1.setDay(date[3]);

            bookingDates.add(bookingDate1);
        }
        dateAdapter = new DateAdapter(getActivity(), bookingDates);

        recyclerViewDates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewDates.setAdapter(dateAdapter);

    }


    @SuppressLint("SetTextI18n")
    public void PlaceOrderProcess() {
        totalAfterTax = (total + Constant.SETTING_DELIVERY_CHARGE + taxAmt);
        if (deliveryDay.length() == 0) {
            Toast.makeText(getContext(), getString(R.string.select_delivery_day), Toast.LENGTH_SHORT).show();
            return;
        } else if (deliveryTime.length() == 0) {
            Toast.makeText(getContext(), getString(R.string.select_delivery_time), Toast.LENGTH_SHORT).show();
            return;
        } else if (paymentMethod.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.select_payment_method), Toast.LENGTH_SHORT).show();
            return;
        }
        sendparams = new HashMap<>();
        sendparams.put(Constant.PLACE_ORDER, Constant.GetVal);
        sendparams.put(Constant.USER_ID, session.getData(Constant.ID));
        sendparams.put(Constant.TAX_AMOUNT, "" + taxAmt);
        sendparams.put(Constant.TOTAL, "" + total);
        sendparams.put(Constant.TAX_PERCENT, "" + Constant.SETTING_TAX);
        sendparams.put(Constant.FINAL_TOTAL, "" + Constant.formater.format(subtotal));
        sendparams.put(Constant.PRODUCT_VARIANT_ID, String.valueOf(variantIdList));
        sendparams.put(Constant.QUANTITY, String.valueOf(qtyList));
        sendparams.put(Constant.MOBILE, session.getData(Constant.MOBILE));
        sendparams.put(Constant.DELIVERY_CHARGE, "" + Constant.SETTING_DELIVERY_CHARGE);
        sendparams.put(Constant.DELIVERY_TIME, (deliveryDay + " - " + deliveryTime));
        sendparams.put(Constant.KEY_WALLET_USED, chWallet.getTag().toString());
        sendparams.put(Constant.KEY_WALLET_BALANCE, String.valueOf(usedBalance));
        sendparams.put(Constant.PAYMENT_METHOD, paymentMethod);
        if (!pCode.isEmpty()) {
            sendparams.put(Constant.PROMO_CODE, pCode);
            sendparams.put(Constant.PROMO_DISCOUNT, Constant.formater.format(pCodeDiscount));
        }
        sendparams.put(Constant.ADDRESS, address);
        sendparams.put(Constant.LONGITUDE, session.getCoordinates(Constant.LONGITUDE));
        sendparams.put(Constant.LATITUDE, session.getCoordinates(Constant.LATITUDE));
        sendparams.put(Constant.EMAIL, session.getData(Constant.EMAIL));

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_order_confirm, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final AlertDialog dialog = alertDialog.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvDialogCancel, tvDialogConfirm, tvDialogItemTotal, tvDialogTaxPercent, tvDialogTaxAmt, tvDialogDeliveryCharge, tvDialogTotal, tvDialogPCAmount, tvDialogWallet, tvDialogFinalTotal;
        LinearLayout lytDialogPromo, lytDialogWallet;
        EditText tvSpecialNote;

        lytDialogPromo = dialogView.findViewById(R.id.lytDialogPromo);
        lytDialogWallet = dialogView.findViewById(R.id.lytDialogWallet);
        tvDialogItemTotal = dialogView.findViewById(R.id.tvDialogItemTotal);
        tvDialogTaxPercent = dialogView.findViewById(R.id.tvDialogTaxPercent);
        tvDialogTaxAmt = dialogView.findViewById(R.id.tvDialogTaxAmt);
        tvDialogDeliveryCharge = dialogView.findViewById(R.id.tvDialogDeliveryCharge);
        tvDialogTotal = dialogView.findViewById(R.id.tvDialogTotal);
        tvDialogPCAmount = dialogView.findViewById(R.id.tvDialogPCAmount);
        tvDialogWallet = dialogView.findViewById(R.id.tvDialogWallet);
        tvDialogFinalTotal = dialogView.findViewById(R.id.tvDialogFinalTotal);
        tvDialogCancel = dialogView.findViewById(R.id.tvDialogCancel);
        tvDialogConfirm = dialogView.findViewById(R.id.tvDialogConfirm);
        tvSpecialNote = dialogView.findViewById(R.id.tvSpecialNote);

        if (pCodeDiscount > 0) {
            lytDialogPromo.setVisibility(View.VISIBLE);
            tvDialogPCAmount.setText("- " + Constant.systemSettings.getCurrency() + pCodeDiscount);
        } else {
            lytDialogPromo.setVisibility(View.GONE);
        }

        if (chWallet.getTag().toString().equals("true")) {
            lytDialogWallet.setVisibility(View.VISIBLE);
            tvDialogWallet.setText("- " + Constant.systemSettings.getCurrency() + usedBalance);
        } else {
            lytDialogWallet.setVisibility(View.GONE);
        }

        tvDialogItemTotal.setText(Constant.systemSettings.getCurrency() + Constant.formater.format(total));
        tvDialogDeliveryCharge.setText(Constant.SETTING_DELIVERY_CHARGE > 0 ? Constant.systemSettings.getCurrency() + Constant.formater.format(Constant.SETTING_DELIVERY_CHARGE) : getString(R.string.free));
        tvDialogTaxPercent.setText(getString(R.string.tax) + "(" + Constant.SETTING_TAX + "%) :");
        tvDialogTaxAmt.setText(Constant.systemSettings.getCurrency() + Constant.formater.format(taxAmt));
        tvDialogTotal.setText(Constant.systemSettings.getCurrency() + Constant.formater.format(totalAfterTax));
        tvDialogFinalTotal.setText(Constant.systemSettings.getCurrency() + Constant.formater.format(subtotal));
        tvDialogConfirm.setOnClickListener(v -> {
            sendparams.put(Constant.ORDER_NOTE, tvSpecialNote.getText().toString().trim());
            if (paymentMethod.equals(getResources().getString(R.string.codpaytype)) || paymentMethod.equals(getString(R.string.wallettype))) {
                ApiConfig.RequestToVolley((result, response) -> {
                    if (result) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (!object.getBoolean(Constant.ERROR)) {
                                if (chWallet.getTag().toString().equals("true")) {
                                    ApiConfig.getWalletBalance(getActivity(), session);
                                }
                                dialog.dismiss();
                                MainActivity.fm.beginTransaction().add(R.id.container, new OrderPlacedFragment()).commit();
                            } else {
                                Toast.makeText(getActivity(), object.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, getActivity(), Constant.ORDERPROCESS_URL, sendparams, true);
                dialog.dismiss();
            } else {
                sendparams.put(Constant.USER_NAME, session.getData(Constant.NAME));
                if (paymentMethod.equals(getString(R.string.pay_u))) {
                    dialog.dismiss();
                    sendparams.put(Constant.MOBILE, session.getData(Constant.MOBILE));
                    sendparams.put(Constant.USER_NAME, session.getData(Constant.NAME));
                    sendparams.put(Constant.EMAIL, session.getData(Constant.EMAIL));

                    paymentModelClass.OnPayClick(getActivity(), sendparams, Constant.PAYMENT, sendparams.get(Constant.FINAL_TOTAL));
                } else if (paymentMethod.equals(getString(R.string.paypal))) {
                    dialog.dismiss();
                    sendparams.put(Constant.FROM, Constant.PAYMENT);
                    sendparams.put(Constant.STATUS, Constant.AWAITING_PAYMENT);
                    PlaceOrder(activity, getString(R.string.midtrans), System.currentTimeMillis() + Constant.randomNumeric(3), true, sendparams, "paypal");
                } else if (paymentMethod.equals(getString(R.string.razor_pay))) {
                    dialog.dismiss();
                    CreateOrderId(subtotal);

                } else if (paymentMethod.equals(getString(R.string.paystack))) {
                    dialog.dismiss();
                    sendparams.put(Constant.FROM, Constant.PAYMENT);
                    Intent intent = new Intent(activity, PayStackActivity.class);
                    intent.putExtra(Constant.PARAMS, (Serializable) sendparams);
                    startActivity(intent);
                } else if (paymentMethod.equals(getString(R.string.midtrans))) {
                    dialog.dismiss();
                    sendparams.put(Constant.FROM, Constant.PAYMENT);
                    sendparams.put(Constant.STATUS, Constant.AWAITING_PAYMENT);
                    PlaceOrder(activity, getString(R.string.midtrans), System.currentTimeMillis() + Constant.randomNumeric(3), true, sendparams, "midtrans");
                } else if (paymentMethod.equals(getString(R.string.stripe))) {
                    dialog.dismiss();
                    sendparams.put(Constant.FROM, Constant.PAYMENT);
                    sendparams.put(Constant.STATUS, Constant.AWAITING_PAYMENT);
                    PlaceOrder(activity, getString(R.string.stripe), System.currentTimeMillis() + Constant.randomNumeric(3), true, sendparams, "stripe");
                } else if (paymentMethod.equals(getString(R.string.flutterwave))) {
                    dialog.dismiss();
                    StartFlutterWavePayment();
                }
            }
        });

        tvDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void CreateOrderId(double payble) {

        String[] amount = String.valueOf(payble * 100).split("\\.");
        Map<String, String> params = new HashMap<>();
        params.put("amount", amount[0]);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            startPayment(object.getString("id"), object.getString("amount"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, getActivity(), Constant.Get_RazorPay_OrderId, params, true);

    }

    public void startPayment(String orderId, String payAmount) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(Constant.RAZOR_PAY_KEY_VALUE);
        checkout.setImage(R.mipmap.ic_launcher);

        try {
            JSONObject options = new JSONObject();
            options.put(Constant.NAME, session.getData(Constant.NAME));
            options.put(Constant.ORDER_ID, orderId);
            options.put(Constant.CURRENCY, "INR");
            options.put(Constant.AMOUNT, payAmount);

            JSONObject preFill = new JSONObject();
            preFill.put(Constant.EMAIL, session.getData(Constant.EMAIL));
            preFill.put(Constant.CONTACT, session.getData(Constant.MOBILE));
            options.put("prefill", preFill);

            checkout.open(getActivity(), options);
        } catch (Exception e) {
            Log.d(TAG, "Error in starting Razorpay Checkout", e);
        }
    }


    public void PlaceOrder(final Activity activity, final String paymentType, final String txnid, boolean issuccess, final Map<String, String> sendparams, final String status) {
        if (issuccess) {
            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    if (result) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (!object.getBoolean(Constant.ERROR)) {
                                if (status.equals("stripe")) {
                                    CreateStripePayment(object.getString(Constant.ORDER_ID), Constant.formater.format(subtotal));
                                } else if (status.equals("midtrans")) {
                                    CreateMidtransPayment(object.getString(Constant.ORDER_ID), Constant.formater.format(subtotal));
                                } else if (status.equals("paypal")) {
                                    StartPayPalPayment(sendparams);
                                } else {
                                    AddTransaction(activity, object.getString(Constant.ORDER_ID), paymentType, txnid, status, activity.getString(R.string.order_success), sendparams);
                                    MainActivity.fm.beginTransaction().add(R.id.container, new OrderPlacedFragment()).commit();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, activity, Constant.ORDERPROCESS_URL, sendparams, false);
        } else {
            AddTransaction(activity, "", getString(R.string.razor_pay), txnid, status, getString(R.string.order_failed), sendparams);
        }
    }

    public void CreateMidtransPayment(String orderId, String grossAmount) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.ORDER_ID, orderId);
        params.put(Constant.GROSS_AMOUNT, "" + (int) Math.round(Double.parseDouble(grossAmount)));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            Intent intent = new Intent(activity, MidtransActivity.class);
                            intent.putExtra(Constant.URL, jsonObject.getJSONObject(Constant.DATA).getString(Constant.REDIRECT_URL));
                            intent.putExtra(Constant.ORDER_ID, orderId);
                            intent.putExtra(Constant.FROM, Constant.PAYMENT);
                            intent.putExtra(Constant.PARAMS, (Serializable) sendparams);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.MIDTRANS_PAYMENT_URL, params, true);
    }

    public void CreateStripePayment(String orderId, String grossAmount) {
        Intent intent = new Intent(activity, StripeActivity.class);
        intent.putExtra(Constant.ORDER_ID, orderId);
        intent.putExtra(Constant.FROM, Constant.PAYMENT);
        intent.putExtra(Constant.PARAMS, (Serializable) sendparams);
        startActivity(intent);
    }

    public void AddTransaction(Activity activity, String orderId, String paymentType, String txnid, final String status, String message, Map<String, String> sendparams) {
        Map<String, String> transparams = new HashMap<>();
        transparams.put(Constant.ADD_TRANSACTION, Constant.GetVal);
        transparams.put(Constant.USER_ID, sendparams.get(Constant.USER_ID));
        transparams.put(Constant.ORDER_ID, orderId);
        transparams.put(Constant.TYPE, paymentType);
        transparams.put(Constant.TAX_PERCENT, "" + Constant.SETTING_TAX);
        transparams.put(Constant.TRANS_ID, txnid);
        transparams.put(Constant.AMOUNT, sendparams.get(Constant.FINAL_TOTAL));
        transparams.put(Constant.STATUS, status);
        transparams.put(Constant.MESSAGE, message);
        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        transparams.put("transaction_date", df.format(c));

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            if (status.equals(Constant.FAILED)) {

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.ORDERPROCESS_URL, transparams, false);
    }

    public void StartPayPalPayment(final Map<String, String> sendParams) {

        final Map<String, String> params = new HashMap<>();
        params.put(Constant.FIRST_NAME, sendParams.get(Constant.USER_NAME));
        params.put(Constant.LAST_NAME, sendParams.get(Constant.USER_NAME));
        params.put(Constant.PAYER_EMAIL, sendParams.get(Constant.EMAIL));
        params.put(Constant.ITEM_NAME, "Card Order");
        params.put(Constant.ITEM_NUMBER, System.currentTimeMillis() + Constant.randomNumeric(3));
        params.put(Constant.AMOUNT, sendParams.get(Constant.FINAL_TOTAL));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                Intent intent = new Intent(getContext(), PayPalWebActivity.class);
                intent.putExtra(Constant.URL, response);
                intent.putExtra(Constant.ORDER_ID, params.get(Constant.ITEM_NUMBER));
                intent.putExtra(Constant.FROM, Constant.PAYMENT);
                intent.putExtra(Constant.PARAMS, (Serializable) sendparams);
                startActivity(intent);
            }
        }, getActivity(), Constant.PAPAL_URL, params, true);
    }

    public void GetTimeSlots() {
        slotList = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put("get_time_slots", Constant.GetVal);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);

                        if (!object.getBoolean(Constant.ERROR)) {
                            JSONArray jsonArray = object.getJSONArray("time_slots");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object1 = jsonArray.getJSONObject(i);
                                slotList.add(new Slot(object1.getString("id"), object1.getString("title"), object1.getString("last_order_time")));
                            }

                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                            adapter = new SlotAdapter(deliveryTime, getActivity(), slotList);
                            recyclerView.setAdapter(adapter);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, getActivity(), Constant.SETTING_URL, params, true);
    }

    void StartFlutterWavePayment() {
        new RavePayManager(this)
                .setAmount(subtotal)
                .setEmail(session.getData(Constant.EMAIL))
                .setCurrency(Constant.FLUTTERWAVE_CURRENCY_CODE_VAL)
                .setfName(session.getData(Constant.FIRST_NAME))
                .setlName(session.getData(Constant.LAST_NAME))
                .setNarration(getString(R.string.app_name) + getString(R.string.shopping))
                .setPublicKey(Constant.FLUTTERWAVE_PUBLIC_KEY_VAL)
                .setEncryptionKey(Constant.FLUTTERWAVE_ENCRYPTION_KEY_VAL)
                .setTxRef(System.currentTimeMillis() + "Ref")
                .acceptAccountPayments(true)
                .acceptCardPayments(true)
                .acceptAccountPayments(true)
                .acceptAchPayments(true)
                .acceptBankTransferPayments(true)
                .acceptBarterPayments(true)
                .acceptGHMobileMoneyPayments(true)
                .acceptRwfMobileMoneyPayments(true)
                .acceptSaBankPayments(true)
                .acceptFrancMobileMoneyPayments(true)
                .acceptZmMobileMoneyPayments(true)
                .acceptUssdPayments(true)
                .acceptUkPayments(true)
                .acceptMpesaPayments(true)
                .shouldDisplayFee(true)
                .onStagingEnv(false)
                .showStagingLabel(false)
                .initialize();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != RaveConstants.RAVE_REQUEST_CODE && data != null) {
            paymentModelClass.TrasactionMethod(data, getActivity(), Constant.PAYMENT);
        } else if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null && data.getStringExtra("response") != null) {
            try {
                JSONObject details = new JSONObject(data.getStringExtra("response"));
                JSONObject jsonObject = details.getJSONObject(Constant.DATA);

                if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                    Toast.makeText(getContext(), getString(R.string.order_placed1), Toast.LENGTH_LONG).show();
                    new PaymentModelClass(getActivity()).PlaceOrder(getActivity(), getString(R.string.flutterwave), jsonObject.getString("txRef"), true, sendparams, Constant.SUCCESS);
                } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                    new PaymentModelClass(getActivity()).PlaceOrder(getActivity(), "", "", false, sendparams, Constant.PENDING);
                    Toast.makeText(getContext(), getString(R.string.order_error), Toast.LENGTH_LONG).show();
                } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                    new PaymentModelClass(getActivity()).PlaceOrder(getActivity(), "", "", false, sendparams, Constant.FAILED);
                    Toast.makeText(getContext(), getString(R.string.order_cancel), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.payment);
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

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(false);
        menu.findItem(R.id.toolbar_layout).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
    }


}
