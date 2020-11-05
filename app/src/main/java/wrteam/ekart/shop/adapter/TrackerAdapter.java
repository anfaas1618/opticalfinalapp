package wrteam.ekart.shop.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.MainActivity;
import wrteam.ekart.shop.fragment.TrackerDetailFragment;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.model.OrderTracker;

public class TrackerAdapter extends RecyclerView.Adapter<TrackerAdapter.CartItemHolder> {

    Activity activity;
    ArrayList<OrderTracker> orderTrackerArrayList;

    public TrackerAdapter(Activity activity, ArrayList<OrderTracker> orderTrackerArrayList) {
        this.activity = activity;
        this.orderTrackerArrayList = orderTrackerArrayList;
    }

    @Override
    public CartItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_trackorder, null);
        CartItemHolder cartItemHolder = new CartItemHolder(v);
        return cartItemHolder;
    }

    @Override
    public void onBindViewHolder(final CartItemHolder holder, final int position) {
        final OrderTracker order = orderTrackerArrayList.get(position);
        holder.txtorderid.setText(order.getOrder_id());
        String[] date = order.getDate_added().split("\\s+");
        holder.txtorderdate.setText(date[0]);

        holder.carddetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new TrackerDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", "");
                bundle.putSerializable("model", order);
                fragment.setArguments(bundle);
                MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }
        });

        if (order.getStatus().equalsIgnoreCase("cancelled")) {
            holder.lyttracker.setVisibility(View.GONE);
        } else {
            if (order.getStatus().equals("returned")) {
                holder.l4.setVisibility(View.VISIBLE);
                holder.returnLyt.setVisibility(View.VISIBLE);
            }
            holder.lyttracker.setVisibility(View.VISIBLE);
            ApiConfig.setOrderTrackerLayout(activity, order, holder);
        }


        holder.recyclerView.setAdapter(new ItemsAdapter(activity, orderTrackerArrayList.get(position).itemsList));
        holder.recyclerView.setNestedScrollingEnabled(false);

    }

    @Override
    public int getItemCount() {

        return orderTrackerArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class CartItemHolder extends RecyclerView.ViewHolder {
        TextView txtorderid, txtorderdate;
        NetworkImageView imgorder;
        LinearLayout lyttracker, returnLyt;
        CardView carddetail;
        RecyclerView recyclerView;
        View l4;

        public CartItemHolder(View itemView) {
            super(itemView);
            txtorderid = itemView.findViewById(R.id.txtorderid);
            txtorderdate = itemView.findViewById(R.id.txtorderdate);
            imgorder = itemView.findViewById(R.id.imgorder);
            lyttracker = itemView.findViewById(R.id.lyttracker);
            l4 = itemView.findViewById(R.id.l4);
            returnLyt = itemView.findViewById(R.id.returnLyt);
            carddetail = itemView.findViewById(R.id.carddetail);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        }
    }

}
