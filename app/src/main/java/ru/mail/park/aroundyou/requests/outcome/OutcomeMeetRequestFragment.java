package ru.mail.park.aroundyou.requests.outcome;

import java.util.ArrayList;

import ru.mail.park.aroundyou.Api;
import ru.mail.park.aroundyou.requests.MeetRequestAdapter;
import ru.mail.park.aroundyou.requests.MeetRequestFragment;
import ru.mail.park.aroundyou.model.MeetRequest;

public class OutcomeMeetRequestFragment extends MeetRequestFragment {
    @Override
    protected MeetRequestAdapter getAdapter() {
        return new OutcomeMeetRequestAdapter(new ArrayList<MeetRequest>());
    }

    @Override
    protected void refreshItems() {
        Api.getInstance().getOutcomePendingRequests(onRequestsGetListener);
    }
}
