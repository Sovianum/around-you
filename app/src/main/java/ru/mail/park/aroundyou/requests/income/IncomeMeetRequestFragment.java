package ru.mail.park.aroundyou.requests.income;

import java.util.ArrayList;

import ru.mail.park.aroundyou.Api;
import ru.mail.park.aroundyou.requests.MeetRequestAdapter;
import ru.mail.park.aroundyou.requests.MeetRequestFragment;
import ru.mail.park.aroundyou.model.MeetRequest;

public class IncomeMeetRequestFragment extends MeetRequestFragment {
    @Override
    protected MeetRequestAdapter getAdapter() {
        return new IncomeMeetRequestAdapter(new ArrayList<MeetRequest>());
    }

    @Override
    protected void refreshItems() {
        Api.getInstance().getIncomePendingRequests(onRequestsGetListener);
    }
}
