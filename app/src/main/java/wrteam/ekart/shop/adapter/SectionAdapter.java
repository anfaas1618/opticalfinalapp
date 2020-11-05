package wrteam.ekart.shop.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.MainActivity;
import wrteam.ekart.shop.fragment.ProductListFragment;
import wrteam.ekart.shop.model.Category;


public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionHolder> {

    public ArrayList<Category> sectionList;
    public Activity activity;
    Context context;

    public SectionAdapter(Context context, Activity activity, ArrayList<Category> sectionList) {
        this.context = context;
        this.activity = activity;
        this.sectionList = sectionList;
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    @Override
    public void onBindViewHolder(SectionHolder holder1, final int position) {
        final Category section;
        section = sectionList.get(position);
        SectionHolder holder = holder1;
        holder.tvTitle.setText(section.getName());
        holder.tvSubTitle.setText(section.getSubtitle());

        switch (section.getStyle()) {
            case "style_1":
                holder.recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                AdapterStyle1 adapter = new AdapterStyle1(context, activity, section.getProductList(), R.layout.offer_layout);
                holder.recyclerView.setAdapter(adapter);
                break;
            case "style_2":
                holder.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                AdapterStyle2 adapterStyle2 = new AdapterStyle2(context, activity, section.getProductList());
                holder.recyclerView.setAdapter(adapterStyle2);
                break;
            case "style_3":
                holder.recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
                AdapterStyle1 adapter3 = new AdapterStyle1(context, activity, section.getProductList(), R.layout.layout_style_3);
                holder.recyclerView.setAdapter(adapter3);
                break;
        }

        holder.tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new ProductListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("from", "section");
                bundle.putString("name", section.getName());
                bundle.putInt("position", position);
                fragment.setArguments(bundle);

                MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public SectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_layout, parent, false);
        return new SectionHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class SectionHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubTitle, tvMore;
        RecyclerView recyclerView;
        RelativeLayout relativeLayout;

        public SectionHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubTitle = itemView.findViewById(R.id.tvSubTitle);
            tvMore = itemView.findViewById(R.id.tvMore);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);

        }
    }


}
