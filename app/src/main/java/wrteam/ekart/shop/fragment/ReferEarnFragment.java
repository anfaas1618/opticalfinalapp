package wrteam.ekart.shop.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.Session;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.INPUT_METHOD_SERVICE;


public class ReferEarnFragment extends Fragment {
    View root;
    TextView txtrefercoin, txtcode, txtcopy, txtinvite;
    Session session;
    String preText = "";
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_refer_earn, container, false);
        activity = getActivity();
        setHasOptionsMenu(true);


        session = new Session(getContext());
        txtrefercoin = root.findViewById(R.id.txtrefercoin);
        if (Constant.REFER_EARN_METHOD.equals("rupees")) {
            preText = Constant.SETTING_CURRENCY_SYMBOL + Constant.REFER_EARN_BONUS;
        } else {
            preText = Constant.REFER_EARN_BONUS + "% ";
        }
        txtrefercoin.setText("Refer a friend and earn upto " + preText + " when your friend's first order is successfully delivered. Minimum Order amount should be " + Constant.SETTING_CURRENCY_SYMBOL + Constant.REFER_EARN_ORDER_AMOUNT + ". which allows you to earn upto " + Constant.SETTING_CURRENCY_SYMBOL + Constant.MAX_EARN_AMOUNT + ".");
        txtcode = root.findViewById(R.id.txtcode);
        txtcopy = root.findViewById(R.id.txtcopy);
        txtinvite = root.findViewById(R.id.txtinvite);

        txtinvite.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(getContext(), R.drawable.ic_share), null, null, null);
        txtcode.setText(session.getData(Session.KEY_REFER_CODE));
        txtcopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", txtcode.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), R.string.refer_code_copied, Toast.LENGTH_SHORT).show();
            }
        });

        txtinvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txtcode.getText().toString().equals("code")) {
                    try {
                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.refer_share_msg_1)
                                + getResources().getString(R.string.app_name) + getString(R.string.refer_share_msg_2)
                                + "\n " + Constant.share_url + "refer/" + txtcode.getText().toString());
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.invite_frnd_title)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.refer_code_alert_msg), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.refere);
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
        menu.findItem(R.id.toolbar_cart).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

}