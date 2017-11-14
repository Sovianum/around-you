package ru.mail.park.aroundyou.requests;


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

import java.util.List;

import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.model.MeetRequest;

abstract public class MeetRequestFragment extends Fragment {
    protected MeetRequestAdapter adapter;
    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_requests, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        adapter = getAdapter();
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

    public void loadItems(List<MeetRequest> items) {
        if (adapter != null) {
            adapter.setItems(items);
        }
    }

    abstract protected MeetRequestAdapter getAdapter();

    abstract protected void refreshItems();

    private void onItemsLoadComplete() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
