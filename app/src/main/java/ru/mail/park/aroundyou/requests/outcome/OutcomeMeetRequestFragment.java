package ru.mail.park.aroundyou.requests.outcome;

import android.util.Log;
import android.widget.Toast;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ru.mail.park.aroundyou.MainActivity;
import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.datasource.MemCache;
import ru.mail.park.aroundyou.datasource.network.NetworkError;
import ru.mail.park.aroundyou.model.MeetRequest;
import ru.mail.park.aroundyou.requests.MeetRequestAdapter;
import ru.mail.park.aroundyou.requests.MeetRequestFragment;
import ru.mail.park.aroundyou.requests.income.IncomeMeetRequestFragment;

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
            Log.e(IncomeMeetRequestFragment.class.getName(), error.toString());

            if (getActivity() == null) {
                return;
            }

            if (error instanceof NetworkError) {
                Toast.makeText(getActivity(), ((NetworkError) error).getErrMsg(), Toast.LENGTH_LONG).show();
            } else if (error instanceof UnknownHostException){
                Toast.makeText(getContext(), R.string.connection_lost_str, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            }
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
