package ru.mail.park.aroundyou.requests.outcome;

import java.util.ArrayList;
import java.util.List;

import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.datasource.MemCache;
import ru.mail.park.aroundyou.model.MeetRequest;
import ru.mail.park.aroundyou.requests.MeetRequestAdapter;
import ru.mail.park.aroundyou.requests.MeetRequestFragment;

public class OutcomeMeetRequestFragment extends MeetRequestFragment {
    ListenerHandler<Api.OnSmthGetListener<List<MeetRequest>>> handler;

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
