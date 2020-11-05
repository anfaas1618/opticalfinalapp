package wrteam.ekart.shop.adapter;

import android.annotation.SuppressLint;
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
import wrteam.ekart.shop.model.Product;


/**
 * Created by shree1 on 3/16/2017.
 */

public class AdapterStyle2 extends RecyclerView.Adapter<AdapterStyle2.VideoHolder> {

    public ArrayList<Product> productList;
    public Activity activity;
    public ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    Context context;

    public AdapterStyle2(Context context, Activity activity, ArrayList<Product> productList) {
        this.context = context;
        this.activity = activity;
        this.productList = productList;

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(VideoHolder holder, final int position) {

        if (productList.size() > 0) {
            holder.tvStyle2_1.setText(productList.get(0).getName());
            holder.imgStyle2_1.setImageUrl(productList.get(0).getImage(), imageLoader);
        }
        if (productList.size() > 1) {
            holder.tvStyle2_2.setText(productList.get(1).getName());
            holder.imgStyle2_2.setImageUrl(productList.get(1).getImage(), imageLoader);
        }


        if (productList.size() > 2) {
            holder.tvStyle2_3.setText(productList.get(2).getName());
            holder.imgStyle2_3.setImageUrl(productList.get(2).getImage(), imageLoader);
        }
        holder.layoutStyle2_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppCompatActivity activity1 = (AppCompatActivity) context;
                Fragment fragment = new ProductDetailFragment();
                final Bundle bundle = new Bundle();
                bundle.putString("from", "section");
                bundle.putInt("vpos", 0);
                bundle.putString("id", productList.get(0).getId());
                fragment.setArguments(bundle);
                activity1.getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }
        });

        holder.layoutStyle2_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity1 = (AppCompatActivity) context;
                Fragment fragment = new ProductDetailFragment();
                final Bundle bundle = new Bundle();
                bundle.putString("from", "section");
                bundle.putInt("vpos", 0);
                bundle.putString("id", productList.get(1).getId());
                fragment.setArguments(bundle);
                activity1.getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }
        });

        holder.layoutStyle2_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity1 = (AppCompatActivity) context;
                Fragment fragment = new ProductDetailFragment();
                final Bundle bundle = new Bundle();
                bundle.putString("from", "section");
                bundle.putInt("vpos", 0);
                bundle.putString("id", productList.get(2).getId());
                fragment.setArguments(bundle);
                activity1.getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_style_2, parent, false);
        return new VideoHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return getItemId(position);
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        public NetworkImageView imgStyle2_1, imgStyle2_2, imgStyle2_3;
        public TextView tvStyle2_1, tvStyle2_2, tvStyle2_3, tvSubStyle2_1, tvSubStyle2_2, tvSubStyle2_3;
        public RelativeLayout layoutStyle2_1, layoutStyle2_2, layoutStyle2_3;

        public VideoHolder(View itemView) {
            super(itemView);
            imgStyle2_1 = itemView.findViewById(R.id.imgStyle2_1);
            imgStyle2_2 = itemView.findViewById(R.id.imgStyle2_2);
            imgStyle2_3 = itemView.findViewById(R.id.imgStyle2_3);
            tvStyle2_1 = itemView.findViewById(R.id.tvStyle2_1);
            tvStyle2_2 = itemView.findViewById(R.id.tvStyle2_2);
            tvStyle2_3 = itemView.findViewById(R.id.tvStyle2_3);
            tvSubStyle2_1 = itemView.findViewById(R.id.tvSubStyle2_1);
            tvSubStyle2_2 = itemView.findViewById(R.id.tvSubStyle2_2);
            tvSubStyle2_3 = itemView.findViewById(R.id.tvSubStyle2_3);
            layoutStyle2_1 = itemView.findViewById(R.id.layoutStyle2_1);
            layoutStyle2_2 = itemView.findViewById(R.id.layoutStyle2_2);
            layoutStyle2_3 = itemView.findViewById(R.id.layoutStyle2_3);
        }


    }
}
