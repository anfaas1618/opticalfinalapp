package wrteam.ekart.shop.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.MainActivity;
import wrteam.ekart.shop.fragment.AddressAddUpdateFragment;
import wrteam.ekart.shop.fragment.AddressListFragment;
import wrteam.ekart.shop.helper.AppController;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.model.Address;

import static wrteam.ekart.shop.helper.ApiConfig.removeAddress;

public class AddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Activity activity;
    ArrayList<Address> addresses;
    String id = "0";


    public AddressAdapter(Activity activity, ArrayList<Address> addresses) {
        this.activity = activity;
        this.addresses = addresses;

    }

    public void add(int position, Address item) {
        addresses.add(position, item);
        notifyItemInserted(position);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.lyt_address_list, parent, false);
        return new AddressItemHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderparent, final int position) {
        final AddressItemHolder holder = (AddressItemHolder) holderparent;
        final Address address = addresses.get(position);
        id = address.getId();

        holder.setIsRecyclable(false);

        if (Constant.selectedAddressId.equals(id)) {
            AddressListFragment.selectedAddress = address.getAddress() + "," + address.getLandmark() + "," + address.getCity_name() + "," + address.getArea_name() + "," + address.getState() + "," + address.getCountry() + ",Pin Code : " + address.getPincode();

            holder.tvName.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));

            holder.tvAddressType.setBackground(activity.getResources().getDrawable(R.drawable.right_btn_bg));

            holder.tvDefaultAddress.setBackground(activity.getResources().getDrawable(R.drawable.right_btn_bg));

            holder.imgSelect.setImageResource(R.drawable.ic_check_circle);
            holder.lytMain.setBackgroundResource(R.drawable.selected_shadow);


        } else {

            holder.tvName.setTextColor(ContextCompat.getColor(activity, R.color.gray));

            holder.tvAddressType.setBackground(activity.getResources().getDrawable(R.drawable.left_btn_bg));

            holder.tvDefaultAddress.setBackground(activity.getResources().getDrawable(R.drawable.left_btn_bg));

            holder.imgSelect.setImageResource(R.drawable.ic_uncheck_circle);
            holder.lytMain.setBackgroundResource(R.drawable.address_card_shadow);
        }

        if (address.getIs_default().equals("1")) {
            holder.tvDefaultAddress.setVisibility(View.VISIBLE);
        }

        holder.lytMain.setPadding((int) activity.getResources().getDimension(R.dimen.dimen_15dp), (int) activity.getResources().getDimension(R.dimen.dimen_15dp), (int) activity.getResources().getDimension(R.dimen.dimen_15dp), (int) activity.getResources().getDimension(R.dimen.dimen_15dp));
        holder.tvName.setText(address.getName());
        if (!address.getType().equalsIgnoreCase("")) {
            holder.tvAddressType.setText(address.getType());
        }
        holder.tvAddress.setText(address.getAddress() + "," + address.getArea_name() + "," + address.getCity_name() + "," + address.getState() + "," + address.getCountry() + "," + address.getPincode());
        holder.tvMobile.setText(address.getMobile());

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getResources().getString(R.string.delete_address));
                builder.setIcon(android.R.drawable.ic_delete);
                builder.setMessage(activity.getResources().getString(R.string.delete_address_msg));

                builder.setCancelable(false);
                builder.setPositiveButton(activity.getResources().getString(R.string.remove), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        if (AppController.isConnected(activity)) {
                            addresses.remove(address);
                            notifyDataSetChanged();
                            removeAddress(activity, address.getId());
                        }
                        if (addresses.size() == 0) {
                            AddressListFragment.tvAlert.setVisibility(View.VISIBLE);
                        } else {
                            AddressListFragment.tvAlert.setVisibility(View.GONE);
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
        });


        holder.lytMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.selectedAddressId = address.getId();
                notifyDataSetChanged();

            }
        });

        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppController.isConnected(activity)) {
                    Fragment fragment = new AddressAddUpdateFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("model", address);
                    bundle.putString("for", "update");
                    bundle.putInt("position", position);
                    fragment.setArguments(bundle);
                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class AddressItemHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAddress, tvAddressType, tvMobile, tvDefaultAddress;
        ImageView imgEdit, imgDelete, imgSelect;
        LinearLayout lytMain;

        public AddressItemHolder(@NonNull View itemView) {
            super(itemView);
            lytMain = itemView.findViewById(R.id.lytMain);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvAddressType = itemView.findViewById(R.id.tvAddressType);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            tvDefaultAddress = itemView.findViewById(R.id.tvDefaultAddress);

            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgSelect = itemView.findViewById(R.id.imgSelect);
            imgDelete = itemView.findViewById(R.id.imgDelete);


        }
    }
}
