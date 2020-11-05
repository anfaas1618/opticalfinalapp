package wrteam.ekart.shop.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.MainActivity;
import wrteam.ekart.shop.fragment.FullScreenViewFragment;
import wrteam.ekart.shop.fragment.ProductDetailFragment;
import wrteam.ekart.shop.fragment.SubCategoryFragment;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.model.Slider;

public class SliderAdapter extends PagerAdapter {

    ArrayList<Slider> dataList;
    Activity activity;
    int layout;
    String from;

    public SliderAdapter(ArrayList<Slider> dataList, Activity activity, int layout, String from) {
        this.dataList = dataList;
        this.activity = activity;
        this.layout = layout;
        this.from = from;
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View imageLayout = LayoutInflater.from(activity).inflate(layout, view, false);

        assert imageLayout != null;
        NetworkImageView imgslider = imageLayout.findViewById(R.id.imgslider);
        CardView lytmain = imageLayout.findViewById(R.id.lytmain);

        final Slider singleItem = dataList.get(position);

        imgslider.setImageUrl(singleItem.getImage(), Constant.imageLoader);
        view.addView(imageLayout, 0);

        lytmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equalsIgnoreCase("detail")) {

                    Fragment fragment = new FullScreenViewFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("pos", position);
                    fragment.setArguments(bundle);

                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();

                } else {

                    if (singleItem.getType().equals("category")) {

                        Fragment fragment = new SubCategoryFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("id", singleItem.getType_id());
                        bundle.putString("name", singleItem.getName());
                        fragment.setArguments(bundle);

                        MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();


                    } else if (singleItem.getType().equals("product")) {

                        Fragment fragment = new ProductDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("id", singleItem.getType_id());
                        bundle.putString("from", "share");
                        bundle.putInt("vpos", 0);
                        fragment.setArguments(bundle);

                        MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();

                    }

                }
            }
        });

        return imageLayout;
    }


    @Override
    public int getCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
