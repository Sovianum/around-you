package ru.mail.park.aroundyou;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.net.UnknownHostException;
import java.util.List;

import ru.mail.park.aroundyou.auth.AuthActivity;
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.common.PreferencesInfo;
import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.model.MeetRequest;
import ru.mail.park.aroundyou.model.Position;
import ru.mail.park.aroundyou.neighbours.NeighbourFragment;
import ru.mail.park.aroundyou.requests.MeetRequestFragment;
import ru.mail.park.aroundyou.requests.Pusher;
import ru.mail.park.aroundyou.requests.income.IncomeMeetRequestFragment;
import ru.mail.park.aroundyou.requests.outcome.OutcomeMeetRequestFragment;
import ru.mail.park.aroundyou.tracking.MapFragment;
import ru.mail.park.aroundyou.tracking.Tracker;
import ru.mail.park.aroundyou.user.UserFragment;

import static ru.mail.park.aroundyou.model.MeetRequest.STATUS_ACCEPTED;
import static ru.mail.park.aroundyou.model.MeetRequest.STATUS_INTERRUPTED;
import static ru.mail.park.aroundyou.model.MeetRequest.STATUS_PENDING;

public class MainActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    private BottomNavigationView nav;

    private NeighbourFragment neighbourFragment;
    private MapFragment mapFragment;
    private MeetRequestFragment outcomeRequestsFragment;
    private MeetRequestFragment incomeRequestsFragment;
    private UserFragment userFragment;

    private Fragment activeFragment;

    private Pusher.PushListener pushListener = new Pusher.PushListener() {
        @Override
        public void onPush(List<MeetRequest> newRequests) {
            int incomeCnt = 0;
            MeetRequest acceptedRequest = null;
            MeetRequest interruptedRequest = null;

            for (int i = 0; i != newRequests.size(); i++) {
                MeetRequest request = newRequests.get(i);
                switch (request.getStatus()) {
                    case STATUS_PENDING: {
                        incomeCnt++;
                        break;
                    }
                    case STATUS_ACCEPTED: {
                        acceptedRequest = request;
                        break;
                    }
                    case STATUS_INTERRUPTED: {
                        interruptedRequest = request;
                        break;
                    }
                }
            }

            // the order of handling interrupted and accepted requests matters
            String msg = "";
            if (incomeCnt > 0) {
                msg += String.format(getString(R.string.you_got_requests_template), incomeCnt);
            }
            if (interruptedRequest != null) {
                Tracker.getInstance(MainActivity.this).stopTracking();
                msg += getString(R.string.interrupted_str);
            }
            if (acceptedRequest != null) {
                Tracker.getInstance(MainActivity.this).startTracking(acceptedRequest.getRequestedId());
                msg += String.format(getString(R.string.request_accepted_template), acceptedRequest.getRequestedLogin());
            }
            if (msg.length() > 0) {
                Toast.makeText(
                        MainActivity.this,
                        msg,
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    };

    private LocationListener onSelfLocationChangeListener = new LocationListener() {
        ListenerHandler<Api.OnSmthGetListener<Integer>> positionHandler;
        Api.OnSmthGetListener<Integer> savePositionListener = new Api.OnSmthGetListener<Integer>() {
            @Override
            public void onSuccess(Integer payload) {
                Log.d(MapFragment.class.getName(), "position successfully saved");
            }

            @Override
            public void onError(Exception error) {
                if (error instanceof UnknownHostException) {
                    Toast.makeText(MainActivity.this, R.string.connection_lost_str, Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        @Override
        public void onLocationChanged(final Location location) {
            Position position = new Position(location.getLatitude(), location.getLongitude());
            positionHandler = Api.getInstance().savePosition(position, savePositionListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_main);

        checkAuthorization();
        startPositionTracking();

        neighbourFragment = new NeighbourFragment();

        Pusher.getInstance().subscribe(pushListener);

        mapFragment = getPreparedMapFragment();
        outcomeRequestsFragment = new OutcomeMeetRequestFragment();
        incomeRequestsFragment = new IncomeMeetRequestFragment();
        userFragment = new UserFragment();
        selectFragment(incomeRequestsFragment);

        nav = findViewById(R.id.bottom_navigation);
        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleNavigationItemSelected(item);
                return true;
            }
        });
        nav.setSelectedItemId(R.id.action_profile);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.can_not_meet_str, Toast.LENGTH_LONG).show();
                    selectFragment(userFragment);
                }
            }
        }
    }

    private void handleNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                selectFragment(userFragment);
                break;
            case R.id.action_outcome_requests:
                selectFragment(outcomeRequestsFragment);
                break;
            case R.id.action_income_requests:
                selectFragment(incomeRequestsFragment);
                break;
            case R.id.action_neighbours:
                selectFragment(neighbourFragment);
                break;
            case R.id.action_map:
                selectFragment(mapFragment);
                break;
        }
    }

    private void checkAuthorization() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String jwt = prefs.getString(PreferencesInfo.JSON_WEB_TOKEN, null);

        if (jwt == null) {
            Intent intentAuth = new Intent(this, AuthActivity.class);
            startActivity(intentAuth);
        } else {
            Api.getInstance().setToken(jwt);
        }
    }

    private MapFragment getPreparedMapFragment() {
        if (mapFragment != null) {
            return mapFragment;
        }
        MapFragment fragment = new MapFragment();
        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(MainActivity.class.getName(), "Map loaded");
            }
        });
        return fragment;
    }

    private void selectFragment(Fragment fragment) {
        if (fragment == activeFragment) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (activeFragment != null) {
            fragmentTransaction.remove(activeFragment);
        }
        activeFragment = fragment;
        fragmentTransaction.add(R.id.fragment_container, activeFragment);
        fragmentTransaction.commit();
    }

    private void startPositionTracking() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION
                );
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION
                );
            }
        }
        if (locationManager == null) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, onSelfLocationChangeListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, onSelfLocationChangeListener);
    }
}
