package ru.mail.park.aroundyou.neighbours;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ru.mail.park.aroundyou.Api;
import ru.mail.park.aroundyou.R;

public class NeighbourFragment extends Fragment {
    private NeighbourAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Api.OnSmthGetListener<List<NeighbourItem>> onNeighboursGetListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_neighbour, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        adapter = new NeighbourAdapter(this, new ArrayList<NeighbourItem>());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        setRefreshing(true);
        refreshItems();

        return view;
    }

    public void setRefreshing(boolean refreshing) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    public void loadItems(List<NeighbourItem> items) {
        if (adapter != null) {
            adapter.setItems(items);
        }
    }

    public void setListener(Api.OnSmthGetListener<List<NeighbourItem>> onNeighboursGetListener) {
        this.onNeighboursGetListener = onNeighboursGetListener;
    }

    private void refreshItems() {
        Api.getInstance().getNeighbours(onNeighboursGetListener);
    }

    private void onItemsLoadComplete() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}