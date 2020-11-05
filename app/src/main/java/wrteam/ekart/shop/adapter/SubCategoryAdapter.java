package wrteam.ekart.shop.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.fragment.ProductListFragment;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.model.Category;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {
    public ArrayList<Category> categorylist;
    int layout;
    String from = "";
    Activity activity;
    Context context;


    public SubCategoryAdapter(Context context, Activity activity, ArrayList<Category> categorylist, int layout, String from) {
        this.context = context;
        this.categorylist = categorylist;
        this.layout = layout;
        this.activity = activity;
        this.from = from;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Category model = categorylist.get(position);
        holder.txttitle.setText(model.getName());
        holder.imgcategory.setDefaultImageResId(R.drawable.placeholder);
        holder.imgcategory.setErrorImageResId(R.drawable.placeholder);
        holder.imgcategory.setImageUrl(model.getImage(), Constant.imageLoader);

        holder.lytMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity1 = (AppCompatActivity) context;
                Fragment fragment = new ProductListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", model.getId());
                bundle.putString("name", model.getName());
                bundle.putString("from", "regular");
                fragment.setArguments(bundle);
                activity1.getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txttitle;
        NetworkImageView imgcategory;
        LinearLayout lytMain;

        public ViewHolder(View itemView) {
            super(itemView);
            lytMain = itemView.findViewById(R.id.lytMain);
            imgcategory = itemView.findViewById(R.id.imgcategory);
            txttitle = itemView.findViewById(R.id.txttitle);
        }

    }
}
