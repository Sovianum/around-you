package ru.mail.park.aroundyou.tracking;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import ru.mail.park.aroundyou.MainActivity;
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.model.Position;

public class Tracker {
    @SuppressLint("StaticFieldLeak")
    private static Tracker instance = new Tracker();
    private Context context;
    private ListenerHandler<Api.OnSmthGetListener<Position>> positionHandler;
    private PositionListener positionListener;
    private boolean tracking;
    private volatile int currId;

    public static Tracker getInstance(Context context) {
        if (instance.context == null) {
            instance.context = context.getApplicationContext();
        }
        return instance;
    }

    void subscribe(PositionListener listener) {
        positionListener = listener;
    }

    void unSubscribe() {
        positionListener = null;
    }

    public void startTracking(final int id) {
        currId = id;
        tracking = true;

        Api.OnSmthGetListener<Position> onPositionChangeListener = new Api.OnSmthGetListener<Position>() {
            @Override
            public void onSuccess(Position pos) {
                if (pos == null || pos.getPoint() == null) {
                    return;
                }

                if (positionListener != null) {
                    positionListener.onPositionChange(pos.getPoint());
                }
                if (positionHandler != null) {
                    positionHandler.unregister();
                }
                positionHandler = Api.getInstance().getNeighbourPosition(currId, this);
            }

            @Override
            public void onError(Exception error) {
                Log.e(MainActivity.class.getName(), error.toString());
                if (positionHandler != null) {
                    positionHandler.unregister();
                }
                positionHandler = Api.getInstance().getNeighbourPosition(currId, this);
            }
        };
        positionHandler = Api.getInstance().getNeighbourPosition(id, onPositionChangeListener);
    }

    public void stopTracking() {
        tracking = false;
        positionHandler.unregister();
    }

    public boolean isTracking() {
        return tracking;
    }

    private Tracker() {}

    public interface PositionListener {
        void onPositionChange(Position.Point point);
    }
}
