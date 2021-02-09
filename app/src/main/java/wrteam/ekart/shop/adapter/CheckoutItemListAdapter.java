package wrteam.ekart.shop.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.model.Cart;

/**
 * Created by shree1 on 3/16/2017.
 */

public class CheckoutItemListAdapter extends RecyclerView.Adapter<CheckoutItemListAdapter.ItemHolder> {

    public ArrayList<Cart> carts;
    public Activity activity;
    Context context;
    Session session;

    public CheckoutItemListAdapter(Context context, Activity activity, ArrayList<Cart> carts) {
        try {
            this.context = context;
            this.activity = activity;
            this.carts = carts;
            session = new Session(context);
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        try {

            final Cart cart = carts.get(position);

            float price;
            if (cart.getItems().get(0).getDiscounted_price().equals("0")) {
                price = Float.parseFloat(cart.getItems().get(0).getPrice());
            } else {
                price = Float.parseFloat(cart.getItems().get(0).getDiscounted_price());
            }

            String taxPercentage = cart.getItems().get(0).getTax_percentage();

            holder.tvItemName.setText(cart.getItems().get(0).getName() + " (" + cart.getItems().get(0).getMeasurement() + " " + ApiConfig.toTitleCase(cart.getItems().get(0).getUnit()) + ")");
            holder.tvQty.setText(activity.getString(R.string.qty_1) + cart.getQty());
            holder.tvPrice.setText(activity.getString(R.string.mrp) + session.getData(Constant.currency) + Constant.formater.format(price));

            if (cart.getItems().get(0).getDiscounted_price().equals("0") || cart.getItems().get(0).getDiscounted_price().equals("")) {
                holder.tvTaxTitle.setText(cart.getItems().get(0).getTax_title());
                holder.tvTaxAmount.setText(session.getData(Constant.currency) + (Integer.parseInt(cart.getQty()) * ((Float.parseFloat(cart.getItems().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
                holder.tvTaxPercent.setText("(" + cart.getItems().get(0).getTax_percentage() + "%)");
            } else {
                holder.tvTaxTitle.setText(cart.getItems().get(0).getTax_title());
                holder.tvTaxAmount.setText(session.getData(Constant.currency) + (Integer.parseInt(cart.getQty()) * ((Float.parseFloat(cart.getItems().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
                holder.tvTaxPercent.setText("(" + cart.getItems().get(0).getTax_percentage() + "%)");
            }

            if (cart.getItems().get(0).getDiscounted_price().equals("0") || cart.getItems().get(0).getDiscounted_price().equals("")) {
                holder.tvSubTotal.setText(session.getData(Constant.currency) + (Integer.parseInt(cart.getQty()) * (Float.parseFloat(cart.getItems().get(0).getPrice()) + ((Float.parseFloat(cart.getItems().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100))));
            } else {
                holder.tvSubTotal.setText(session.getData(Constant.currency) + (Integer.parseInt(cart.getQty()) * (Float.parseFloat(cart.getItems().get(0).getDiscounted_price()) + ((Float.parseFloat(cart.getItems().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100))));
            }
        } catch (Exception e) {

        }
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_checkout_item_list, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        public TextView tvItemName, tvQty, tvPrice, tvSubTotal, tvTaxPercent, tvTaxTitle, tvTaxAmount;

        public ItemHolder(View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSubTotal = itemView.findViewById(R.id.tvSubTotal);
            tvTaxPercent = itemView.findViewById(R.id.tvTaxPercent);
            tvTaxTitle = itemView.findViewById(R.id.tvTaxTitle);
            tvTaxAmount = itemView.findViewById(R.id.tvTaxAmount);
        }


    }
}

