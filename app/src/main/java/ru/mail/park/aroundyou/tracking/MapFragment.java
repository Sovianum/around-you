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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
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
            Position position = new Position(location.getLatitude(), location.getLongitude());
            positionHandler = Api.getInstance().savePosition(position, listener);
            setMyMarker(new LatLng(location.getLatitude(), location.getLongitude()));
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

    private Tracker.PositionListener positionListener = new Tracker.PositionListener() {
        @Override
        public void onPositionChange(Position.Point point) {
            setDestMarker(new LatLng(point.getX(), point.getY()));
        }
    };

    private Button stopTrackingBtn;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FrameLayout mapView = (FrameLayout) super.onCreateView(layoutInflater, viewGroup, bundle);
        prepareButtonLayout(layoutInflater, mapView);
        return mapView;
    }

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
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION
                );
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, onSelfLocationChangeListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, onSelfLocationChangeListener);
        getMapAsync(this);
        Tracker.getInstance(this.getContext()).subscribe(positionListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        locationManager.removeUpdates(onSelfLocationChangeListener);
        onDestPositionChangeListener = null;
        Tracker.getInstance(this.getContext()).unSubscribe();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), R.string.can_not_meet_str, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void removeDestMarker() {
        if (destMarker == null) {
            return;
        }
        destMarker.remove();
        destMarker = null;
    }

    private void setDestMarker(LatLng pos) {
        if (getActivity() == null) {
            return;
        }
        if (destMarker != null) {
            destMarker.remove();
        }
        destMarker = addMarker(pos, BitmapDescriptorFactory.HUE_BLUE, getString(R.string.dest_marker_str));
    }

    private void setMyMarker(LatLng pos) {
        if (getActivity() == null) {
            return;
        }
        if (myMarker != null) {
            myMarker.remove();
        }
        myMarker = addMarker(pos, BitmapDescriptorFactory.HUE_RED, getString(R.string.you_marker_str));
    }

    @Nullable
    private Marker addMarker(LatLng pos, float color, String name) {
        if (map != null) {
            return map.
                    addMarker(
                            new MarkerOptions().
                                    position(pos).
                                    title(name).
                                    icon(BitmapDescriptorFactory.defaultMarker(color))
                    );
        }
        return null;
    }

    @NonNull
    private void prepareButtonLayout(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        View relativeLayout1 = layoutInflater.inflate(
                R.layout.map_button_layout,
                viewGroup
        );
        stopTrackingBtn = relativeLayout1.findViewById(R.id.stop_tracking_btn);
        if (!Tracker.getInstance(getContext()).isTracking()) {
            setUntracking();
            return;
        }

        setTracking();
        stopTrackingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker.getInstance(MapFragment.this.getContext()).stopTracking();
                Toast.makeText(
                        MapFragment.this.getContext(),
                        getString(R.string.tracking_stopped_str),
                        Toast.LENGTH_SHORT
                ).show();
                setUntracking();
                removeDestMarker();
            }
        });
    }

    private void setTracking() {
        stopTrackingBtn.setAlpha(1f);
        stopTrackingBtn.setText(R.string.stop_tracking_command);
        stopTrackingBtn.setClickable(true);
    }

    private void setUntracking() {
        stopTrackingBtn.setAlpha(0.25f);
        stopTrackingBtn.setText(R.string.not_tracking_str);
        stopTrackingBtn.setClickable(false);
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
