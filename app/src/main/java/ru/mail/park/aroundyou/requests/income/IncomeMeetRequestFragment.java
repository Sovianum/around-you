package ru.mail.park.aroundyou.requests.income;

import android.util.Log;
import android.widget.Toast;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.datasource.MemCache;
import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.datasource.network.NetworkError;
import ru.mail.park.aroundyou.model.MeetRequest;
import ru.mail.park.aroundyou.model.MeetRequestUpdate;
import ru.mail.park.aroundyou.requests.MeetRequestAdapter;
import ru.mail.park.aroundyou.requests.MeetRequestFragment;
import ru.mail.park.aroundyou.tracking.Tracker;

import static ru.mail.park.aroundyou.model.MeetRequest.STATUS_ACCEPTED;
import static ru.mail.park.aroundyou.model.MeetRequest.STATUS_DECLINED;

public class IncomeMeetRequestFragment extends MeetRequestFragment {
    ListenerHandler<Api.OnSmthGetListener<List<MeetRequest>>> pendingRequestsHandler;
    private ListenerHandler<Api.OnSmthGetListener<MeetRequest>> acceptHandler;
    private ListenerHandler<Api.OnSmthGetListener<MeetRequest>> declineHandler;

    private Api.OnSmthGetListener<List<MeetRequest>> onRequestsGetListener = new Api.OnSmthGetListener<List<MeetRequest>>() {

        @Override
        public void onSuccess(List<MeetRequest> items) {
            MemCache.clearAndAddIncomeRequests(items);
            loadItems(items);
            setRefreshing(false);
        }

        @Override
        public void onError(Exception error) {
            defaultErrorHandler(error);
        }
    };

    private Api.OnSmthGetListener<MeetRequest> acceptListener = new Api.OnSmthGetListener<MeetRequest>() {
        @Override
        public void onSuccess(MeetRequest payload) {
            handleAcceptResponseCode(payload);
            setRefreshing(false);
        }

        @Override
        public void onError(Exception error) {
            defaultErrorHandler(error);
        }
    };

    private Api.OnSmthGetListener<MeetRequest> declineListener = new Api.OnSmthGetListener<MeetRequest>() {
        @Override
        public void onSuccess(MeetRequest payload) {
            handleDeclineResponseCode(payload);
            setRefreshing(false);
        }

        @Override
        public void onError(Exception error) {
            defaultErrorHandler(error);
        }
    };

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
        pendingRequestsHandler = Api.getInstance().getIncomePendingRequests(onRequestsGetListener);
    }

    @Override
    public void onStop() {
        if (pendingRequestsHandler != null) {
            pendingRequestsHandler.unregister();
        }
        if (acceptHandler != null) {
            acceptHandler.unregister();
        }
        if (declineHandler != null) {
            declineHandler.unregister();
        }
        super.onStop();
    }

    public void acceptRequest(int requestId) {
        if (Tracker.getInstance(getContext()).isTracking()) {
            Toast.makeText(getContext(), R.string.stop_tracking_str, Toast.LENGTH_SHORT).show();
            return;
        }
        setRefreshing(true);
        acceptHandler = updateMeetRequest(requestId, STATUS_ACCEPTED, acceptListener);
    }

    public void declineRequest(int requestId) {
        setRefreshing(true);
        declineHandler = updateMeetRequest(requestId, STATUS_DECLINED, declineListener);
    }

    private ListenerHandler<Api.OnSmthGetListener<MeetRequest>>
    updateMeetRequest(int id, String status, Api.OnSmthGetListener<MeetRequest> listener) {
        return Api.getInstance().updateMeetRequest(
                new MeetRequestUpdate(id, status),
                listener
        );
    }

    private void handleAcceptResponseCode(MeetRequest request) {
        String message = getString(R.string.request_accepted_str);
        Tracker.getInstance(getContext()).startTracking(request.getRequesterId());
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        setRefreshing(false);
    }

    private void handleDeclineResponseCode(MeetRequest request) {
        String message = getString(R.string.request_declined_str);
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        setRefreshing(false);
    }

    private void defaultErrorHandler(Exception error) {
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
}
