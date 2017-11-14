package ru.mail.park.aroundyou.requests.outcome;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.mail.park.aroundyou.MainActivity;
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.datasource.MemCache;
import ru.mail.park.aroundyou.model.MeetRequest;
import ru.mail.park.aroundyou.requests.MeetRequestAdapter;
import ru.mail.park.aroundyou.requests.MeetRequestFragment;

public class OutcomeMeetRequestFragment extends MeetRequestFragment {
    ListenerHandler<Api.OnSmthGetListener<List<MeetRequest>>> handler;

    private Api.OnSmthGetListener<List<MeetRequest>> onRequestsGetListener = new Api.OnSmthGetListener<List<MeetRequest>>() {

        @Override
        public void onSuccess(List<MeetRequest> items) {
            MemCache.clearAndAddOutcomeRequests(items);
            loadItems(items);
            setRefreshing(false);
        }

        @Override
        public void onError(Exception error) {
            Log.e(MainActivity.class.getName(), error.toString());
            Toast.makeText(OutcomeMeetRequestFragment.this.getContext(), error.toString(), Toast.LENGTH_LONG).show();
            setRefreshing(false);
        }
    };

    @Override
    protected MeetRequestAdapter getAdapter() {
        return new OutcomeMeetRequestAdapter(new ArrayList<MeetRequest>());
    }

    @Override
    protected void refreshItems() {
        if (!MemCache.getInstance().isEmptyOutcome()) {
            this.loadItems(MemCache.getInstance().getOutcomeRequests());
            this.setRefreshing(false);
        }
        handler = Api.getInstance().getOutcomePendingRequests(onRequestsGetListener);
    }

    @Override
    public void onStop() {
        if (handler != null) {
            handler.unregister();
        }
        super.onStop();
    }
}
