package wrteam.ekart.shop.helper;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class ProgressDisplay {

    public static ProgressBar mProgressBar;

    public ProgressDisplay(Activity context) {

        ViewGroup layout = (ViewGroup) (context).findViewById(android.R.id.content).getRootView();

        mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyle);
        mProgressBar.setIndeterminate(true);


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        RelativeLayout rl = new RelativeLayout(context);
        rl.setGravity(Gravity.CENTER);
        rl.addView(mProgressBar);
        layout.addView(rl, params);
        hideProgress();
    }


    public void showProgress() {
        if (mProgressBar.getVisibility() == View.GONE)
            mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);

    }
}
