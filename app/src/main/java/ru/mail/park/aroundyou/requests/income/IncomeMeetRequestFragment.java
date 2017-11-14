package ru.mail.park.aroundyou.requests.income;

import java.util.ArrayList;
import java.util.List;

import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.datasource.MemCache;
import ru.mail.park.aroundyou.requests.MeetRequestAdapter;
import ru.mail.park.aroundyou.requests.MeetRequestFragment;
import ru.mail.park.aroundyou.model.MeetRequest;

public class IncomeMeetRequestFragment extends MeetRequestFragment {
    ListenerHandler<Api.OnSmthGetListener<List<MeetRequest>>> handler;

    @Override
    protected MeetRequestAdapter getAdapter() {
        return new IncomeMeetRequestAdapter(this, new ArrayList<MeetRequest>());
    }

    @Override
    protected void refreshItems() {
        if (!MemCache.getInstance().isEmptyIncome()) {
            this.loadItems(MemCache.getInstance().getIncomeRequests());
            this.setRefreshing(false);
        }
        handler = Api.getInstance().getIncomePendingRequests(onRequestsGetListener);
    }

    @Override
    public void onStop() {
        if (handler != null) {
            handler.unregister();
        }
        super.onStop();
    }
}
