package wrteam.ekart.shop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.adapter.TrackerAdapter;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.model.OrderTracker;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class OrderListFragment extends Fragment {

    RecyclerView recyclerView;
    TextView nodata;
    ProgressBar progressbar;
    Session session;
    int pos;
    View root;
    private ArrayList<OrderTracker> orderTrackerArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_order_list, container, false);

        pos = getArguments().getInt("pos");
        session = new Session(getActivity());
        progressbar = root.findViewById(R.id.progressbar);
        recyclerView = root.findViewById(R.id.recycleview);
        nodata = root.findViewById(R.id.nodata);
        setHasOptionsMenu(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        switch (pos) {
            case 0:
                orderTrackerArrayList = TrackOrderFragment.orderTrackerslist;
                break;
            case 1:
                orderTrackerArrayList = TrackOrderFragment.processedlist;
                break;
            case 2:
                orderTrackerArrayList = TrackOrderFragment.shippedlist;
                break;
            case 3:
                orderTrackerArrayList = TrackOrderFragment.deliveredlist;
                break;
            case 4:
                orderTrackerArrayList = TrackOrderFragment.cancelledlist;
                break;
            case 5:
                orderTrackerArrayList = TrackOrderFragment.returnedList;
                break;
        }

        if (orderTrackerArrayList.size() == 0)
            nodata.setVisibility(View.VISIBLE);

        recyclerView.setAdapter(new TrackerAdapter(getActivity(), orderTrackerArrayList));
        progressbar.setVisibility(View.GONE);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
