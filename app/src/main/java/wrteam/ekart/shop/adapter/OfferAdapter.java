package wrteam.ekart.shop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import wrteam.ekart.shop.R;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {
    public ArrayList<String> offerlist;
    int layout;

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
        if (!offerlist.get(position).equals("")) {
            Picasso.get()
                    .load(offerlist.get(position))
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.offerImage);
            holder.lytOfferImage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return offerlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView offerImage;
        LinearLayout lytOfferImage;

        public ViewHolder(View itemView) {
            super(itemView);
            offerImage = itemView.findViewById(R.id.offerImage);
            lytOfferImage = itemView.findViewById(R.id.lytOfferImage);

        }

    }
}
