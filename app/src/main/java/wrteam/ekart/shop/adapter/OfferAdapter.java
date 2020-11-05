package wrteam.ekart.shop.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.helper.Constant;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {
    public ArrayList<String> offerlist;
    int layout;
    String from = "";
    Activity activity;

    public OfferAdapter(ArrayList<String> offerlist, int layout) {
        this.offerlist = offerlist;
        this.layout = layout;
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

        holder.offerImage.setImageUrl(offerlist.get(position), Constant.imageLoader);
    }

    @Override
    public int getItemCount() {
        return offerlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        NetworkImageView offerImage;


        public ViewHolder(View itemView) {
            super(itemView);
            offerImage = itemView.findViewById(R.id.offerImage);

        }

    }
}
