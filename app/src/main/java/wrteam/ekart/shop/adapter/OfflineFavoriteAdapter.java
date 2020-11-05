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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.fragment.FavoriteFragment;
import wrteam.ekart.shop.fragment.ProductDetailFragment;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.DatabaseHelper;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.model.PriceVariation;
import wrteam.ekart.shop.model.Product;

import static wrteam.ekart.shop.helper.ApiConfig.AddMultipleProductInCart;

public class OfflineFavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    private final Context context;
    private final Activity activity;
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    public boolean isLoading;
    public int resource;
    public ArrayList<Product> mDataset = new ArrayList<>();
    SpannableString spannableString;
    Session session;
    DatabaseHelper databaseHelper;


    public OfflineFavoriteAdapter(Context context, ArrayList<Product> myDataset, int resource) {
        this.context = context;
        this.activity = (Activity) context;
        this.mDataset = myDataset;
        this.resource = resource;
        this.session = new Session(activity);
        databaseHelper = new DatabaseHelper(activity);
    }

    public void add(int position, Product item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(resource, parent, false);
            return new ViewHolderRow(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_progressbar, parent, false);
            return new ViewHolderLoading(view);
        }

        return null;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderparent, final int position) {

        if (holderparent instanceof ViewHolderRow) {
            final ViewHolderRow holder = (ViewHolderRow) holderparent;
            holder.setIsRecyclable(false);
            final Product product = mDataset.get(position);

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
            holder.productName.setText(Html.fromHtml(product.getName()));
            holder.imgThumb.setDefaultImageResId(R.drawable.placeholder);
            holder.imgThumb.setErrorImageResId(R.drawable.placeholder);

            holder.imgThumb.setImageUrl(product.getImage(), Constant.imageLoader);

            CustomAdapter customAdapter = new CustomAdapter(context, priceVariations, holder, product);
            holder.spinner.setAdapter(customAdapter);

            holder.lytmain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Constant.CartValues.size() > 0) {
                        AddMultipleProductInCart(session, activity, Constant.CartValues);
                    }

                    AppCompatActivity activity1 = (AppCompatActivity) context;
                    Fragment fragment = new ProductDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("vpos", priceVariations.size() == 1 ? 0 : holder.spinner.getSelectedItemPosition());
                    bundle.putString("id", product.getId());
                    bundle.putInt("position", position);
                    bundle.putString("from", "fragment");

                    fragment.setArguments(bundle);

                    activity1.getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();

                }
            });


            holder.txtqty.setText(databaseHelper.CheckOrderExists(product.getPriceVariations().get(0).getId(), product.getId()));

            holder.imgFav.setImageResource(R.drawable.ic_is_favorite);

            holder.imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseHelper.AddOrRemoveFavorite(product.getId(), false);
                    mDataset.remove(product);
                    FavoriteFragment.offlineFavoriteAdapter.notifyDataSetChanged();
                    FavoriteFragment.recyclerView.setAdapter(FavoriteFragment.offlineFavoriteAdapter);
                }
            });

            SetSelectedData(holder, priceVariations.get(0));


        } else if (holderparent instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderparent;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        Product product = mDataset.get(position);
        if (product != null)
            return Integer.parseInt(product.getId());
        else
            return position;
    }

    public void setLoaded() {
        isLoading = false;
    }

    @SuppressLint("SetTextI18n")
    public void SetSelectedData(final ViewHolderRow holder, final PriceVariation extra) {

        holder.Measurement.setText(extra.getMeasurement() + extra.getMeasurement_unit_name());
        holder.productPrice.setText(activity.getResources().getString(R.string.offer_price) + Constant.SETTING_CURRENCY_SYMBOL + extra.getProductPrice());

        if (session.isUserLoggedIn()) {
            if (Constant.CartValues.containsKey(extra.getId())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.txtqty.setText("" + Constant.CartValues.get(extra.getId()));
                }
            }
        } else {
            if (session.getData(extra.getId()) != null) {
                holder.txtqty.setText(session.getData(extra.getId()));
            } else {
                holder.txtqty.setText(extra.getCart_count());
            }
        }

        holder.txtstatus.setText(extra.getServe_for());

        if (extra.getDiscounted_price().equals("0") || extra.getDiscounted_price().equals("")) {
            holder.originalPrice.setVisibility(View.INVISIBLE);
            holder.showDiscount.setVisibility(View.INVISIBLE);

            holder.productPrice.setText(activity.getResources().getString(R.string.mrp) + Constant.SETTING_CURRENCY_SYMBOL + extra.getProductPrice());
        } else {
            spannableString = new SpannableString(activity.getResources().getString(R.string.mrp) + Constant.SETTING_CURRENCY_SYMBOL + extra.getPrice());
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

        holder.txtqty.setText(databaseHelper.CheckOrderExists(extra.getId(), extra.getProduct_id()));

        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(holder.txtqty.getText().toString());
                if (count < Float.parseFloat(extra.getStock())) {
                    if (count < Constant.MAX_PRODUCT_LIMIT) {
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

    private class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.itemProgressbar);
        }
    }

    public class ViewHolderRow extends RecyclerView.ViewHolder {
        public ImageButton imgAdd, imgMinus;
        TextView productName, productPrice, txtqty, Measurement, showDiscount, originalPrice, txtstatus;
        NetworkImageView imgThumb;
        ImageView imgFav, imgIndicator;
        RelativeLayout lytmain;
        AppCompatSpinner spinner;
        LinearLayout qtyLyt;

        public ViewHolderRow(View itemView) {
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

        }

    }

    public class CustomAdapter extends BaseAdapter {
        Context context;
        ArrayList<PriceVariation> extraList;
        LayoutInflater inflter;
        ViewHolderRow holder;
        Product product;

        public CustomAdapter(Context applicationContext, ArrayList<PriceVariation> extraList, ViewHolderRow holder, Product product) {
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

        @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.lyt_spinner_item, null);
            TextView measurement = view.findViewById(R.id.txtmeasurement);
            TextView price = view.findViewById(R.id.txtprice);


            PriceVariation extra = extraList.get(i);
            measurement.setText(extra.getMeasurement() + " " + extra.getMeasurement_unit_name());
            price.setText(Constant.SETTING_CURRENCY_SYMBOL + extra.getProductPrice());

            if (extra.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
                measurement.setTextColor(context.getResources().getColor(R.color.red));
                price.setTextColor(context.getResources().getColor(R.color.red));
            } else {
                measurement.setTextColor(context.getResources().getColor(R.color.black));
                price.setTextColor(context.getResources().getColor(R.color.black));
            }

            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    PriceVariation priceVariation = extraList.get(i);
                    SetSelectedData(holder, priceVariation);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            return view;
        }
    }

}
