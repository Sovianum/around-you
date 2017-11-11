package ru.mail.park.aroundyou.requests.outcome;

import java.util.ArrayList;

import ru.mail.park.aroundyou.requests.MeetRequestAdapter;
import ru.mail.park.aroundyou.requests.MeetRequestFragment;
import ru.mail.park.aroundyou.requests.MeetRequestItem;

public class OutcomeMeetRequestFragment extends MeetRequestFragment {
    @Override
    protected MeetRequestAdapter getAdapter() {
        return new OutcomeMeetRequestAdapter(new ArrayList<MeetRequestItem>());
    }
}
