package wrteam.ekart.shop.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.model.Cart;

/**
 * Created by shree1 on 3/16/2017.
 */

public class CheckoutItemListAdapter extends RecyclerView.Adapter<CheckoutItemListAdapter.ItemHolder> {

    public ArrayList<Cart> carts;
    public Activity activity;

    public CheckoutItemListAdapter(Activity activity, ArrayList<Cart> carts) {
        this.activity = activity;
        this.carts = carts;
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        final Cart cart = carts.get(position);

        float price;
        if (cart.getItems().get(0).getDiscounted_price().equals("0")) {
            price = Float.parseFloat(cart.getItems().get(0).getPrice());
        } else {
            price = Float.parseFloat(cart.getItems().get(0).getDiscounted_price());
        }

        double itemTotal = price * (Integer.parseInt(cart.getQty()));


        holder.tvItemName.setText(cart.getItems().get(0).getName() + " (" + cart.getItems().get(0).getMeasurement() + " " + ApiConfig.toTitleCase(cart.getItems().get(0).getUnit()) + ")");
        holder.tvQty.setText("Qty : " + cart.getQty());
        holder.tvPrice.setText("Price : " + Constant.systemSettings.getCurrency() + Constant.formater.format(price));
        holder.tvTaxPercent.setText("Tax (" + cart.getItems().get(0).getTax_percentage() + "%)");
        holder.tvSubTotal.setText(Constant.systemSettings.getCurrency() + Constant.formater.format(itemTotal));
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

        public TextView tvItemName, tvQty, tvPrice, tvSubTotal, tvTaxPercent;

        public ItemHolder(View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSubTotal = itemView.findViewById(R.id.tvSubTotal);
            tvTaxPercent = itemView.findViewById(R.id.tvTaxPercent);
        }


    }
}

