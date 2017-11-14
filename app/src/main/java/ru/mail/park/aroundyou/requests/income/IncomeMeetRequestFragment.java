package ru.mail.park.aroundyou.requests.income;

import java.util.ArrayList;

import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.datasource.MemCache;
import ru.mail.park.aroundyou.requests.MeetRequestAdapter;
import ru.mail.park.aroundyou.requests.MeetRequestFragment;
import ru.mail.park.aroundyou.model.MeetRequest;

public class IncomeMeetRequestFragment extends MeetRequestFragment {
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
        Api.getInstance().getIncomePendingRequests(onRequestsGetListener);
    }
}
