package wrteam.ekart.shop.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.fragment.ProductDetailFragment;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.DatabaseHelper;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.model.PriceVariation;
import wrteam.ekart.shop.model.Product;

import static wrteam.ekart.shop.helper.ApiConfig.AddOrRemoveFavorite;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {
    public ArrayList<Product> productList;
    public Activity activity;
    public int resource;
    SpannableString spannableString;
    boolean isFavorite = false;
    Session session;
    DatabaseHelper databaseHelper;

    public ProductAdapter(ArrayList<Product> productList, int resource, Activity activity) {
        this.productList = productList;
        this.resource = resource;
        this.activity = activity;
        session = new Session(activity);
        databaseHelper = new DatabaseHelper(activity);
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(resource, null);
        ProductHolder productHolder = new ProductHolder(v);
        return productHolder;
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, final int position) {
        final Product product = productList.get(position);
        holder.setIsRecyclable(false);
        final ArrayList<PriceVariation> priceVariations = product.getPriceVariations();
        if (priceVariations.size() == 1) {
            holder.spinner.setVisibility(View.GONE);
        }

        if (!product.getIndicator().equals("0")) {
            holder.imgIndicator.setVisibility(View.VISIBLE);
            if (product.getIndicator().equals("1"))
                holder.imgIndicator.setImageResource(R.drawable.ic_veg_icon);
            else if (product.getIndicator().equals("2"))
                holder.imgIndicator.setImageResource(R.drawable.ic_non_veg_icon);
        }
        holder.productName.setText(Html.fromHtml("<font color='#000000'><b>" + product.getName() + "</b></font> - <small>" + product.getDescription().replaceFirst("<p>", "").replaceFirst("</p>", "") + "</small>"));

        Picasso.get().
                load(product.getImage())
                .fit()
                .centerInside()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.imgThumb);

        CustomAdapter customAdapter = new CustomAdapter(activity, priceVariations, holder, product);
        holder.spinner.setAdapter(customAdapter);

        SetSelectedData(holder, priceVariations.get(0));

        if (session.isUserLoggedIn()) {

            holder.txtqty.setText(priceVariations.get(0).getCart_count());

            if (product.isIs_favorite()) {
                holder.imgFav.setImageResource(R.drawable.ic_is_favorite);
            } else {
                holder.imgFav.setImageResource(R.drawable.ic_is_not_favorite);
            }
            final Session session = new Session(activity);

            holder.imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFavorite = product.isIs_favorite();

                    if (isFavorite) {
                        isFavorite = false;
                        holder.imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                    } else {
                        isFavorite = true;
                        holder.imgFav.setImageResource(R.drawable.ic_is_favorite);
                    }
                    AddOrRemoveFavorite(activity, session, product.getId(), isFavorite);
                }
            });
        } else {

            holder.txtqty.setText(databaseHelper.CheckOrderExists(product.getPriceVariations().get(0).getId(), product.getId()));

            if (databaseHelper.getFavouriteById(product.getId())) {
                holder.imgFav.setImageResource(R.drawable.ic_is_favorite);
            } else {
                holder.imgFav.setImageResource(R.drawable.ic_is_not_favorite);
            }

            holder.imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFavorite = databaseHelper.getFavouriteById(product.getId());

                    if (isFavorite) {
                        isFavorite = false;
                        holder.imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                    } else {
                        isFavorite = true;
                        holder.imgFav.setImageResource(R.drawable.ic_is_favorite);
                    }
                    databaseHelper.AddOrRemoveFavorite(product.getId(), isFavorite);
                }
            });
        }

        holder.lytmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constant.CartValues.size() > 0) {
                    Session session = new Session(activity);
                    ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                }

                Session session = new Session(activity);
                session.setData(Constant.PRODUCT_ID, product.getId());

                AppCompatActivity activity1 = (AppCompatActivity) activity;
                Fragment fragment = new ProductDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("vpos", priceVariations.size() == 1 ? 0 : holder.spinner.getSelectedItemPosition());
                bundle.putString("id", product.getId());
                bundle.putInt("position", position);
                bundle.putString(Constant.FROM, "search");

                fragment.setArguments(bundle);

                activity1.getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }
        });

    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void SetSelectedData(final ProductHolder holder, final PriceVariation extra) {

        holder.Measurement.setText(extra.getMeasurement() + extra.getMeasurement_unit_name());
        holder.productPrice.setText(activity.getResources().getString(R.string.offer_price) + Constant.systemSettings.getCurrency() + extra.getProductPrice());
        holder.txtstatus.setText(extra.getServe_for());

        if (extra.getDiscounted_price().equals("0") || extra.getDiscounted_price().equals("")) {
            holder.originalPrice.setText("");
            holder.showDiscount.setText("");
            holder.lytDiscount.setVisibility(View.GONE);
        } else {
            spannableString = new SpannableString(activity.getResources().getString(R.string.mrp) + Constant.systemSettings.getCurrency() + extra.getPrice());
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.originalPrice.setText(spannableString);

            holder.showDiscount.setText(extra.getDiscountpercent().replace("(", "").replace(")", ""));

        }


        if (extra.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
            holder.txtstatus.setVisibility(View.VISIBLE);
            holder.txtstatus.setTextColor(Color.RED);
            holder.qtyLyt.setVisibility(View.GONE);
        } else {
            holder.txtstatus.setVisibility(View.GONE);
            holder.qtyLyt.setVisibility(View.VISIBLE);
        }

        if (session.isUserLoggedIn()) {
            if (Constant.CartValues.containsKey(extra.getId())) {
                holder.txtqty.setText(Constant.CartValues.get(extra.getId()));
            } else {
                holder.txtqty.setText(extra.getCart_count());
            }

            holder.imgAdd.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
                    int count = Integer.parseInt(holder.txtqty.getText().toString());
                    if (count < Float.parseFloat(extra.getStock())) {
                        if (count < Integer.parseInt(Constant.systemSettings.getMax_cart_items_count())) {
                            count++;
                            holder.txtqty.setText("" + count);
                            if (Constant.CartValues.containsKey(extra.getId())) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Constant.CartValues.replace(extra.getId(), "" + count);
                                }
                            } else {
                                Constant.CartValues.put(extra.getId(), "" + count);
                            }
                            ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.limit_alert), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.imgMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int count = Integer.parseInt(holder.txtqty.getText().toString());
                    if (!(count <= 0)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            if (count != 0) {
                                count--;
                                holder.txtqty.setText("" + count);
                            }
                            if (Constant.CartValues.containsKey(extra.getId())) {
                                Constant.CartValues.replace(extra.getId(), "" + count);
                            } else {
                                Constant.CartValues.put(extra.getId(), "" + count);
                            }
                        }
                        ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                    }
                }
            });
        } else {
            holder.txtqty.setText(databaseHelper.CheckOrderExists(extra.getId(), extra.getProduct_id()));

            holder.imgAdd.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
                    int count = Integer.parseInt(holder.txtqty.getText().toString());
                    if (count < Float.parseFloat(extra.getStock())) {
                        if (count < Integer.parseInt(Constant.systemSettings.getMax_cart_items_count())) {
                            count++;
                            holder.txtqty.setText("" + count);
                            databaseHelper.AddOrderData(extra.getId(), extra.getProduct_id(), "" + count);
                            databaseHelper.getTotalItemOfCart(activity);
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.limit_alert), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.imgMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int count = Integer.parseInt(holder.txtqty.getText().toString());
                    if (!(count <= 0)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            if (count != 0) {
                                count--;
                                holder.txtqty.setText("" + count);
                                databaseHelper.AddOrderData(extra.getId(), extra.getProduct_id(), "" + count);
                                databaseHelper.getTotalItemOfCart(activity);
                            }
                        }
                    }
                }
            });
        }

    }

    public class CustomAdapter extends BaseAdapter {
        Context context;
        ArrayList<PriceVariation> extraList;
        LayoutInflater inflter;
        ProductHolder holder;
        Product product;

        public CustomAdapter(Context applicationContext, ArrayList<PriceVariation> extraList, ProductHolder holder, Product product) {
            this.context = applicationContext;
            this.extraList = extraList;
            this.holder = holder;
            this.product = product;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return extraList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.lyt_spinner_item, null);
            TextView measurement = view.findViewById(R.id.txtmeasurement);
//            TextView price = view.findViewById(R.id.txtprice);

            PriceVariation extra = extraList.get(i);
            measurement.setText(extra.getMeasurement() + " " + extra.getMeasurement_unit_name());
//            price.setText(Constant.systemSettings.getCurrency() + extra.getProductPrice());

            if (extra.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
                measurement.setTextColor(context.getResources().getColor(R.color.red));
//                price.setTextColor(context.getResources().getColor(R.color.red));
            } else {
                measurement.setTextColor(context.getResources().getColor(R.color.black));
//                price.setTextColor(context.getResources().getColor(R.color.black));
            }

            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    PriceVariation extra = extraList.get(i);
                    SetSelectedData(holder, extra);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            return view;
        }
    }

    public class ProductHolder extends RecyclerView.ViewHolder {
        public ImageButton imgAdd, imgMinus;
        TextView productName, productPrice, txtqty, Measurement, showDiscount, originalPrice, txtstatus;
        ImageView imgThumb;
        ImageView imgFav, imgIndicator;
        RelativeLayout lytmain, lytDiscount;
        AppCompatSpinner spinner;
        LinearLayout qtyLyt;

        public ProductHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.txtprice);
            showDiscount = itemView.findViewById(R.id.showDiscount);
            originalPrice = itemView.findViewById(R.id.txtoriginalprice);
            Measurement = itemView.findViewById(R.id.txtmeasurement);
            txtstatus = itemView.findViewById(R.id.txtstatus);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            imgIndicator = itemView.findViewById(R.id.imgIndicator);
            imgAdd = itemView.findViewById(R.id.btnaddqty);
            imgMinus = itemView.findViewById(R.id.btnminusqty);
            txtqty = itemView.findViewById(R.id.txtqty);
            qtyLyt = itemView.findViewById(R.id.qtyLyt);
            imgFav = itemView.findViewById(R.id.imgFav);
            lytmain = itemView.findViewById(R.id.lytmain);
            spinner = itemView.findViewById(R.id.spinner);
            lytDiscount = itemView.findViewById(R.id.lytDiscount);
        }
    }
}

