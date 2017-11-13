package ru.mail.park.aroundyou.tracking;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import ru.mail.park.aroundyou.datasource.Api;
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.MainActivity;
import ru.mail.park.aroundyou.model.Position;

public class Tracker {
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

    public void subscribe(PositionListener listener) {
        positionListener = listener;
    }

    public void unSubscribe() {
        positionListener = null;
    }

    public int getCurrId() {
        return currId;
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
                Api.getInstance().getNeighbourPosition(currId, this);
            }

            @Override
            public void onError(Exception error) {
                Log.e(MainActivity.class.getName(), error.toString());
                Api.getInstance().getNeighbourPosition(currId, this);
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

    private boolean checkPermissions() {
        boolean permissionsGranted = isPermissionGranted(
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        );
        permissionsGranted &= isPermissionGranted(
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        );
        return permissionsGranted;
    }

    private boolean isPermissionGranted(int permission) {
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    public interface PositionListener {
        void onPositionChange(Position.Point point);
    }
}
