package wrteam.ekart.shop.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.model.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    public boolean isLoading;
    Activity activity;
    ArrayList<Notification> Notifications;
    String id = "0";


    public NotificationAdapter(Activity activity, ArrayList<Notification> Notifications) {
        this.activity = activity;
        this.Notifications = Notifications;
    }

    public void add(int position, Notification item) {
        Notifications.add(position, item);
        notifyItemInserted(position);
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.lyt_notification_list, parent, false);
            return new NotificationItemHolder(view);
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

        if (holderparent instanceof NotificationItemHolder) {
            final NotificationItemHolder holder = (NotificationItemHolder) holderparent;
            final Notification notification = Notifications.get(position);

            id = notification.getId();

            if (!notification.getImage().isEmpty()) {
                holder.image.setVisibility(View.VISIBLE);
            } else {
                holder.image.setVisibility(View.GONE);
            }

            if (!notification.getName().isEmpty()) {
                holder.tvTitle.setVisibility(View.VISIBLE);
            } else {
                holder.tvTitle.setVisibility(View.GONE);
            }

            if (!notification.getSubtitle().isEmpty()) {
                holder.tvMessage.setVisibility(View.VISIBLE);
            } else {
                holder.tvMessage.setVisibility(View.GONE);
            }

            holder.image.setImageUrl(notification.getImage(), Constant.imageLoader);
            holder.tvTitle.setText(Html.fromHtml(notification.getName()));
            holder.tvMessage.setText(Html.fromHtml(notification.getSubtitle()));

        } else if (holderparent instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderparent;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return Notifications.size();
    }

    @Override
    public int getItemViewType(int position) {
        return Notifications.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        Notification product = Notifications.get(position);
        if (product != null)
            return Integer.parseInt(product.getId());
        else
            return position;
    }

    static class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.itemProgressbar);
        }
    }

    class NotificationItemHolder extends RecyclerView.ViewHolder {

        NetworkImageView image;
        TextView tvTitle, tvMessage;


        public NotificationItemHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }
}
