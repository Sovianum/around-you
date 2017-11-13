package ru.mail.park.aroundyou.requests.outcome;

import java.util.ArrayList;

import ru.mail.park.aroundyou.datasource.Api;
import ru.mail.park.aroundyou.datasource.MemCache;
import ru.mail.park.aroundyou.model.MeetRequest;
import ru.mail.park.aroundyou.requests.MeetRequestAdapter;
import ru.mail.park.aroundyou.requests.MeetRequestFragment;

public class OutcomeMeetRequestFragment extends MeetRequestFragment {
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
        Api.getInstance().getOutcomePendingRequests(onRequestsGetListener);
    }
}
