package wrteam.ekart.shop.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.fragment.ProductDetailFragment;
import wrteam.ekart.shop.helper.AppController;
import wrteam.ekart.shop.model.PriceVariation;
import wrteam.ekart.shop.model.Product;

/**
 * Created by shree1 on 3/16/2017.
 */

public class AdapterStyle1 extends RecyclerView.Adapter<AdapterStyle1.VideoHolder> {

    public ArrayList<Product> productList;
    public Activity activity;
    public int itemResource;
    Context context;
    ImageLoader netImageLoader = AppController.getInstance().getImageLoader();

    public AdapterStyle1(Context context, Activity activity, ArrayList<Product> productList, int itemResource) {
        this.context = context;
        this.activity = activity;
        this.productList = productList;
        this.itemResource = itemResource;

    }

    @Override
    public int getItemCount() {
        int product;
        if (productList.size() > 4) {
            product = 4;
        } else {
            product = productList.size();
        }
        return product;
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, final int position) {
        final Product product = productList.get(position);
        final ArrayList<PriceVariation> priceVariations = product.getPriceVariations();
        holder.thumbnail.setImageUrl(product.getImage(), netImageLoader);
        holder.v_title.setText(product.getName());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppCompatActivity activity1 = (AppCompatActivity) context;
                Fragment fragment = new ProductDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", product.getId());
                bundle.putString("from", "section");
                bundle.putInt("vpos", 0);
                fragment.setArguments(bundle);
                activity1.getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();


            }
        });
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(itemResource, parent, false);
        return new VideoHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        public NetworkImageView thumbnail;
        public TextView v_title, v_date, description;
        public RelativeLayout relativeLayout;

        public VideoHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            v_title = itemView.findViewById(R.id.title);
            v_date = itemView.findViewById(R.id.date);
            relativeLayout = itemView.findViewById(R.id.play_layout);

        }


    }
}
