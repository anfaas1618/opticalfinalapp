package wrteam.ekart.shop.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.MainActivity;
import wrteam.ekart.shop.fragment.TrackerDetailFragment;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.model.OrderTracker;

public class TrackerAdapter extends RecyclerView.Adapter<TrackerAdapter.CartItemHolder> {

    Activity activity;
    ArrayList<OrderTracker> orderTrackerArrayList;
    HashMap<String, String> hashMap;

    public TrackerAdapter(Activity activity, ArrayList<OrderTracker> orderTrackerArrayList) {
        this.activity = activity;
        this.orderTrackerArrayList = orderTrackerArrayList;
        hashMap = new HashMap<>();
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

        for (int i = 0; i < order.getItemsList().size(); i++) {
            hashMap.put(order.getItemsList().get(i).getProduct_variant_id(), order.getItemsList().get(i).getQuantity());
        }

        holder.reorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(activity)
                        .setTitle(activity.getString(R.string.re_order))
                        .setMessage(activity.getString(R.string.reorder_msg))
                        .setPositiveButton(activity.getString(R.string.proceed), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (activity != null) {
                                    ApiConfig.AddMultipleProductInCart(new Session(activity), activity, hashMap);
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
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
        TextView txtorderid, txtorderdate, carddetail, reorder;
        LinearLayout lyttracker, returnLyt;
        RecyclerView recyclerView;
        View l4;

        public CartItemHolder(View itemView) {
            super(itemView);
            txtorderid = itemView.findViewById(R.id.txtorderid);
            txtorderdate = itemView.findViewById(R.id.txtorderdate);
            lyttracker = itemView.findViewById(R.id.lyttracker);
            l4 = itemView.findViewById(R.id.l4);
            returnLyt = itemView.findViewById(R.id.returnLyt);
            carddetail = itemView.findViewById(R.id.carddetail);
            reorder = itemView.findViewById(R.id.reorder);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        }
    }

}
