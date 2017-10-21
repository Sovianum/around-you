package ru.mail.park.aroundyou;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class NeighbourFragment extends Fragment {
    private NeighbourAdapter adapter;
    private List<NeighbourItem> items;
    private RecyclerView recyclerView;

    public void loadItems(List<NeighbourItem> items) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.addAll(items);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_neighbour, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        adapter = new NeighbourAdapter(items);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
