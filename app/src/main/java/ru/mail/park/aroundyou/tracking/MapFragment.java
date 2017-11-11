package ru.mail.park.aroundyou.tracking;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ru.mail.park.aroundyou.Api;
import ru.mail.park.aroundyou.ListenerHandler;
import ru.mail.park.aroundyou.MainActivity;
import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.model.Position;

import static ru.mail.park.aroundyou.MainActivity.MY_PERMISSIONS_REQUEST_LOCATION;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {
    private LocationManager locationManager;
    private Api.OnSmthGetListener<Position> onDestPositionChangeListener;

    private LocationListener onSelfLocationChangeListener = new LocationListener() {
        ListenerHandler<Api.OnSmthGetListener<Integer>> positionHandler;
        Api.OnSmthGetListener<Integer> listener = new Api.OnSmthGetListener<Integer>() {
            @Override
            public void onSuccess(Integer payload) {
//                Toast.makeText(getContext(), String.format("Response code is %d", payload), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Exception error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        @Override
        public void onLocationChanged(final Location location) {
            Position position = new Position(location.getAltitude(), location.getLongitude());
            positionHandler = Api.getInstance().savePosition(position, listener);
            setMyMarker(new LatLng(location.getLatitude(), location.getLongitude()));
            fitCamera();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    private GoogleMap map;
    private Marker myMarker;
    private Marker destMarker;

    @Override
    public void onStart() {
        super.onStart();

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION
                );
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, onSelfLocationChangeListener);
        getMapAsync(this);
        startTracking(1);
    }

    @Override
    public void onStop() {
        super.onStop();
        locationManager.removeUpdates(onSelfLocationChangeListener);
        onDestPositionChangeListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        onSelfLocationChangeListener.onLocationChanged(getLastLocation());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "You will not be able to meet other users", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void setDestMarker(LatLng pos) {
        if (destMarker != null) {
            destMarker.remove();
        }
        destMarker = addMarker(pos, BitmapDescriptorFactory.HUE_BLUE);
    }

    private void setMyMarker(LatLng pos) {
        if (myMarker != null) {
            myMarker.remove();
        }
        myMarker = addMarker(pos, BitmapDescriptorFactory.HUE_RED);
        map.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }

    private void startTracking(final int id) {
        onDestPositionChangeListener = new Api.OnSmthGetListener<Position>() {
            private int userId = id;

            @Override
            public void onSuccess(Position pos) {
                if (pos == null || pos.getPoint() == null) {
                    return;
                }
                if (MapFragment.this.getActivity() == null) {
                    return;
                }

                setDestMarker(new LatLng(pos.getPoint().getY(), pos.getPoint().getX()));    // todo transpose coordinates
                fitCamera();
                Api.getInstance().getNeighbourPosition(userId, this);
            }

            @Override
            public void onError(Exception error) {
                Log.e(MainActivity.class.getName(), error.toString());
            }
        };
        Api.getInstance().getNeighbourPosition(id, onDestPositionChangeListener);
    }

    private void stopTracking() {
        onDestPositionChangeListener = null;
        if (destMarker != null) {
            destMarker.remove();
        }
    }

    @Nullable
    private Marker addMarker(LatLng pos, float color) {
        if (map != null) {
            return map.
                    addMarker(
                            new MarkerOptions().
                                    position(pos).
                                    title(getString(R.string.you_marker_str)).
                                    icon(BitmapDescriptorFactory.defaultMarker(color))
                    );
        }
        return null;
    }

    private void fitCamera() {
        if (map == null || myMarker == null || destMarker == null) {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(myMarker.getPosition());
        builder.include(destMarker.getPosition());

        LatLngBounds bounds = builder.build();

        CameraUpdate moveUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 300);
        map.moveCamera(moveUpdate);
    }

    private Location getLastLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION
                );
            }
        }
        return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }
}