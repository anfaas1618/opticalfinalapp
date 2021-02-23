package wrteam.ekart.shop.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.fragment.CartFragment;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.DatabaseHelper;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.model.OfflineCart;


public class OfflineCartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    final Activity activity;
    final ArrayList<OfflineCart> items;
    final DatabaseHelper databaseHelper;
    final Session session;
    final Context context;
    public boolean isLoading;


    public OfflineCartAdapter(Context context, Activity activity, ArrayList<OfflineCart> items) {
        this.activity = activity;
        this.context = context;
        this.items = items;
        databaseHelper = new DatabaseHelper(activity);
        session = new Session(context);
    }

    public void add(int position, OfflineCart item) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.lyt_cartlist, parent, false);
            return new ProductHolderItems(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_progressbar, parent, false);
            return new ViewHolderLoading(view);
        }

        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderparent, final int position) {

        if (holderparent instanceof ProductHolderItems) {
            final ProductHolderItems holder = (ProductHolderItems) holderparent;
            final OfflineCart cart = items.get(position);

            double price = Double.parseDouble(cart.getItem().get(0).getDiscounted_price());

            Picasso.get()
                    .load(cart.getItem().get(0).getImage())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imgproduct);

            holder.txtproductname.setText(cart.getItem().get(0).getName());
            holder.txtmeasurement.setText(cart.getItem().get(0).getMeasurement() + "\u0020" + cart.getItem().get(0).getUnit());
            holder.txtprice.setText(session.getData(Constant.currency) + Constant.formater.format(Double.parseDouble(cart.getItem().get(0).getDiscounted_price())));


            if (cart.getItem().get(0).getDiscounted_price().equals("0")) {
                holder.txtprice.setText(session.getData(Constant.currency) + Constant.formater.format(Double.parseDouble(cart.getItem().get(0).getPrice())));
                price = Double.parseDouble(cart.getItem().get(0).getPrice());
            } else if (!cart.getItem().get(0).getDiscounted_price().equalsIgnoreCase(cart.getItem().get(0).getPrice())) {
                holder.txtoriginalprice.setPaintFlags(holder.txtoriginalprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.txtoriginalprice.setText(session.getData(Constant.currency) + Constant.formater.format(Double.parseDouble(cart.getItem().get(0).getPrice())));
            }

            holder.txtQuantity.setText(databaseHelper.CheckOrderExists(cart.getId(), cart.getProduct_id()));

            holder.txttotalprice.setText(session.getData(Constant.currency) + Constant.formater.format(price * Integer.parseInt(databaseHelper.CheckOrderExists(cart.getId(), cart.getProduct_id()))));

            Constant.FLOAT_TOTAL_AMOUNT = Constant.FLOAT_TOTAL_AMOUNT + (price * Integer.parseInt(databaseHelper.CheckOrderExists(cart.getId(), cart.getProduct_id())));
            CartFragment.SetData();

            final double finalPrice = price;
            holder.btnaddqty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (ApiConfig.isConnected(activity)) {
                        if (!(Integer.parseInt(holder.txtQuantity.getText().toString()) >= Float.parseFloat(cart.getItem().get(0).getStock()))) {
                            if (!(Integer.parseInt(holder.txtQuantity.getText().toString()) + 1 > Integer.parseInt(session.getData(Constant.max_cart_items_count)))) {
                                int count = Integer.parseInt(holder.txtQuantity.getText().toString());
                                count++;
                                holder.txtQuantity.setText("" + count);
                                holder.txttotalprice.setText(session.getData(Constant.currency) + Constant.formater.format(finalPrice * count));
                                Constant.FLOAT_TOTAL_AMOUNT = Constant.FLOAT_TOTAL_AMOUNT + finalPrice;
                                databaseHelper.AddOrderData(cart.getId(), cart.getProduct_id(), "" + count);
                                CartFragment.SetData();
                            } else {
                                Toast.makeText(activity, activity.getString(R.string.limit_alert), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });

            holder.btnminusqty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ApiConfig.isConnected(activity)) {
                        if (Integer.parseInt(holder.txtQuantity.getText().toString()) > 1) {
                            int count = Integer.parseInt(holder.txtQuantity.getText().toString());
                            count--;
                            holder.txtQuantity.setText("" + count);
                            holder.txttotalprice.setText(session.getData(Constant.currency) + Constant.formater.format(finalPrice * count));
                            Constant.FLOAT_TOTAL_AMOUNT = Constant.FLOAT_TOTAL_AMOUNT - finalPrice;
                            databaseHelper.AddOrderData(cart.getId(), cart.getProduct_id(), "" + count);
                            CartFragment.SetData();
                        }
                    }
                }
            });

            holder.imgdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ApiConfig.isConnected(activity)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(activity.getResources().getString(R.string.deleteproducttitle));
                        builder.setIcon(android.R.drawable.ic_delete);
                        builder.setMessage(activity.getResources().getString(R.string.deleteproductmsg));

                        builder.setCancelable(false);
                        builder.setPositiveButton(activity.getResources().getString(R.string.remove), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                CartFragment.offlineCarts.remove(cart);
                                notifyItemRemoved(position);
                                databaseHelper.DeleteOrderData(cart.getId(), cart.getProduct_id());
                                Constant.FLOAT_TOTAL_AMOUNT = Double.parseDouble(Constant.formater.format(Constant.FLOAT_TOTAL_AMOUNT - (finalPrice * Integer.parseInt(databaseHelper.CheckOrderExists(cart.getId(), cart.getProduct_id())))));
                                CartFragment.SetData();

                                items.remove(cart);
                                Constant.FLOAT_TOTAL_AMOUNT = 0.00;
                                notifyDataSetChanged();
                                databaseHelper.getTotalItemOfCart(activity);
                                activity.invalidateOptionsMenu();
                                if (getItemCount() == 0) {
                                    CartFragment.lytempty.setVisibility(View.VISIBLE);
                                    CartFragment.lytTotal.setVisibility(View.GONE);
                                }
                            }
                        });

                        builder.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                }
            });

        } else if (holderparent instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderparent;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        OfflineCart cart = items.get(position);
        if (cart != null)
            return Integer.parseInt(cart.getId());
        else
            return position;
    }

    static class ViewHolderLoading extends RecyclerView.ViewHolder {
        public final ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.itemProgressbar);
        }
    }

    public static class ProductHolderItems extends RecyclerView.ViewHolder {
        final ImageView imgproduct;
        final ImageView imgdelete;
        final ImageView btnminusqty;
        final ImageView btnaddqty;
        final TextView txtproductname;
        final TextView txtmeasurement;
        final TextView txtprice;
        final TextView txtoriginalprice;
        final TextView txtQuantity;
        final TextView txttotalprice;

        public ProductHolderItems(@NonNull View itemView) {
            super(itemView);
            imgproduct = itemView.findViewById(R.id.imgproduct);

            imgdelete = itemView.findViewById(R.id.imgdelete);
            btnminusqty = itemView.findViewById(R.id.btnminusqty);
            btnaddqty = itemView.findViewById(R.id.btnaddqty);

            txtproductname = itemView.findViewById(R.id.txtproductname);
            txtmeasurement = itemView.findViewById(R.id.txtmeasurement);
            txtprice = itemView.findViewById(R.id.txtprice);
            txtoriginalprice = itemView.findViewById(R.id.txtoriginalprice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txttotalprice = itemView.findViewById(R.id.txttotalprice);
        }
    }
}