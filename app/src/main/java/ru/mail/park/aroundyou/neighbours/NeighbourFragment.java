package ru.mail.park.aroundyou.neighbours;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.mail.park.aroundyou.MainActivity;
import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.datasource.DBApi;
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.datasource.network.NetworkError;
import ru.mail.park.aroundyou.model.User;

import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_PRECON_FAILED;
import static ru.mail.park.aroundyou.common.ServerInfo.HTTP_FAILED_DEPENDENCY;

public class NeighbourFragment extends Fragment {
    private NeighbourAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListenerHandler<Api.OnSmthGetListener<List<User>>> neighboursHandler;
    private ListenerHandler<Api.OnSmthGetListener<Integer>> requestCreationHandler;
    private ListenerHandler<DBApi.OnDBDataGetListener<List<User>>> neighboursHandlerDB;

    private DBApi.OnDBDataGetListener<List<User>> onNeighboursGetListenerDB = new DBApi.OnDBDataGetListener<List<User>>() {
        @Override
        public void onSuccess(List<User> items) {
            loadItems(items);
            setRefreshing(false);
        }

        @Override
        public void onError(Exception error) {
            Log.e(MainActivity.class.getName(), error.toString());
            Toast.makeText(NeighbourFragment.this.getContext(), error.toString(), Toast.LENGTH_LONG).show();
            setRefreshing(false);
        }
    };

    private Api.OnSmthGetListener<List<User>> onNeighboursGetListener = new Api.OnSmthGetListener<List<User>>() {
        @Override
        public void onSuccess(List<User> neighbourItems) {
            loadItems(neighbourItems);
            cacheNeighbours(neighbourItems);
            cacheUsers(neighbourItems);
            setRefreshing(false);
        }

        @Override
        public void onError(Exception error) {
            Log.e(MainActivity.class.getName(), error.toString());
            if (error instanceof NetworkError) {
                Toast.makeText(getContext(), ((NetworkError) error).getErrMsg(), Toast.LENGTH_LONG).show();
            } else if (error instanceof UnknownHostException) {
                Toast.makeText(getContext(), R.string.connection_lost_str, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
            setRefreshing(false);
        }
    };

    private Api.OnSmthGetListener<Integer> requestCreationListener = new Api.OnSmthGetListener<Integer>() {
        @Override
        public void onSuccess(Integer code) {
            String message;
            switch (code) {
                case HTTP_OK:
                    message = getString(R.string.request_created_str);
                    break;
                case HTTP_CONFLICT:
                    message = getString(R.string.request_exists_str);
                    break;
                case HTTP_FAILED_DEPENDENCY:
                    message = getString(R.string.out_of_range_str);
                    break;
                case HTTP_FORBIDDEN:
                    message = getString(R.string.request_exists_str);
                    break;
                case HTTP_INTERNAL_ERROR:
                    message = getString(R.string.server_internal_error_str);
                    break;
                default:
                    message = String.format(Locale.ENGLISH, getString(R.string.response_code_template), code);
                    break;
            }
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            setRefreshing(false);
        }

        @Override
        public void onError(Exception error) {
            if (error instanceof UnknownHostException) {
                Toast.makeText(getContext(), R.string.connection_lost_str, Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
            setRefreshing(false);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_neighbour, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        adapter = new NeighbourAdapter(this, new ArrayList<User>());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        neighboursHandlerDB = DBApi.getInstance(getContext()).getNeighbours(onNeighboursGetListenerDB);
        setRefreshing(true);
        refreshItems();
    }

    @Override
    public void onStop() {
        if (neighboursHandler != null) {
            neighboursHandler.unregister();
        }
        if (requestCreationHandler != null) {
            requestCreationHandler.unregister();
        }
        if (neighboursHandlerDB != null) {
            neighboursHandlerDB.unregister();
        }
        super.onStop();
    }

    public void setRefreshing(boolean refreshing) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    public void loadItems(List<User> items) {
        if (adapter != null) {
            adapter.setItems(items);
        }
    }

    public void createRequest(int userId) {
        setRefreshing(true);
        requestCreationHandler = Api.getInstance().createMeetRequest(userId, requestCreationListener);
    }

    private void refreshItems() {
        DBApi.getInstance(getActivity().getApplicationContext()).getNeighbours(onNeighboursGetListenerDB);
        neighboursHandler = Api.getInstance().getNeighbours(onNeighboursGetListener);
    }

    private void refreshCachedItems() {
        DBApi.getInstance(getActivity().getApplicationContext()).getNeighbours(onNeighboursGetListenerDB);
    }

    private void onItemsLoadComplete() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void cacheNeighbours(List<User> neighbourItems) {
        DBApi.getInstance(getContext()).insertNeighbours(neighbourItems);
    }

    private void cacheUsers(List<User> users) {
        DBApi.getInstance(getContext()).insertUsers(users);
    }
}
