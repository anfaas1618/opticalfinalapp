package wrteam.ekart.shop.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.model.WalletTransaction;

import static wrteam.ekart.shop.helper.ApiConfig.toTitleCase;


public class WalletTransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    public boolean isLoading;
    Activity activity;
    ArrayList<WalletTransaction> walletTransactions;
    String id = "0";


    public WalletTransactionAdapter(Activity activity, ArrayList<WalletTransaction> walletTransactions) {
        this.activity = activity;
        this.walletTransactions = walletTransactions;
    }

    public void add(int position, WalletTransaction item) {
        walletTransactions.add(position, item);
        notifyItemInserted(position);
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.lyt_wallet_transection_list, parent, false);
            return new TransactionHolderItems(view);
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

        if (holderparent instanceof TransactionHolderItems) {
            final TransactionHolderItems holder = (TransactionHolderItems) holderparent;
            final WalletTransaction walletTransaction = walletTransactions.get(position);
            id = walletTransaction.getId();


            holder.tvTxDateAndTime.setText(walletTransaction.getDate_created());
            holder.tvTxMessage.setText("#" + walletTransaction.getOrder_id() + " " + walletTransaction.getMessage());
            holder.tvTxAmount.setText("Amount : " + Float.parseFloat(walletTransaction.getAmount()));
            holder.tvTxNo.setText("#" + walletTransaction.getId());
            holder.tvTxStatus.setText(toTitleCase(walletTransaction.getStatus()));

            if (walletTransaction.getStatus().equalsIgnoreCase(Constant.CREDIT)) {
                holder.cardViewTxStatus.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.tx_success_bg));
            } else {
                holder.cardViewTxStatus.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.tx_fail_bg));
            }

        } else if (holderparent instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderparent;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return walletTransactions.size();
    }

    @Override
    public int getItemViewType(int position) {
        return walletTransactions.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        WalletTransaction product = walletTransactions.get(position);
        if (product != null)
            return Integer.parseInt(product.getId());
        else
            return position;
    }

    class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.itemProgressbar);
        }
    }

    public class TransactionHolderItems extends RecyclerView.ViewHolder {

        TextView tvTxNo, tvTxDateAndTime, tvTxMessage, tvTxAmount, tvTxStatus;
        CardView cardViewTxStatus;

        public TransactionHolderItems(@NonNull View itemView) {
            super(itemView);

            tvTxNo = itemView.findViewById(R.id.tvTxNo);
            tvTxDateAndTime = itemView.findViewById(R.id.tvTxDateAndTime);
            tvTxMessage = itemView.findViewById(R.id.tvTxMessage);
            tvTxAmount = itemView.findViewById(R.id.tvTxAmount);
            tvTxStatus = itemView.findViewById(R.id.tvTxStatus);


            cardViewTxStatus = itemView.findViewById(R.id.cardViewTxStatus);

        }
    }
}