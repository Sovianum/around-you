package ru.mail.park.aroundyou.requests;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.model.MeetRequest;

public class Pusher {
    private static Pusher instance = new Pusher();
    private List<MeetRequest> newRequests;

    private PushListener pushListener;
    private ListenerHandler<Api.OnSmthGetListener<List<MeetRequest>>> requestHandler;
    private Api.OnSmthGetListener<List<MeetRequest>> requestListener = new Api.OnSmthGetListener<List<MeetRequest>>() {
        @Override
        public void onSuccess(List<MeetRequest> items) {
            List<MeetRequest> pushRequests = newRequests;
            newRequests = items;

            if (requestListener != null) {
                pushListener.onPush(pushRequests);
            }
            requestHandler = Api.getInstance().getNewRequests(requestListener);
        }

        @Override
        public void onError(Exception error) {
            Log.d(Pusher.class.getName(), error.toString());
            requestHandler = Api.getInstance().getNewRequests(requestListener);
        }
    };

    public static Pusher getInstance() {
        return instance;
    }

    public void subscribe(PushListener listener) {
        startListening();
        this.pushListener = listener;
    }

    public void unSubscribe() {
        requestHandler.unregister();
        pushListener = null;
    }

    private Pusher(){
        newRequests = new ArrayList<>();
    }

    private void startListening() {
        requestHandler = Api.getInstance().getNewRequests(requestListener);
    }

    public interface PushListener {
        void onPush(List<MeetRequest> newRequests);
    }
}
