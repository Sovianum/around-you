package ru.mail.park.aroundyou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;

import ru.mail.park.aroundyou.auth.AuthActivity;
import ru.mail.park.aroundyou.neighbours.NeighbourFragment;
import ru.mail.park.aroundyou.neighbours.NeighbourItem;
import ru.mail.park.aroundyou.tracking.MapFragment;

public class MainActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    private BottomNavigationView nav;
    private NeighbourFragment neighbourFragment;
    private MapFragment mapFragment;
    private Fragment activeFragment;
    private ListenerHandler<Api.OnSmthGetListener<List<NeighbourItem>>> neighboursHandler;

    private Api.OnSmthGetListener<List<NeighbourItem>> neighboursListener = new Api.OnSmthGetListener<List<NeighbourItem>>() {
        @Override
        public void onSuccess(List<NeighbourItem> neighbourItems) {
            neighbourFragment.loadItems(neighbourItems);
            neighbourFragment.setRefreshing(false);
        }

        @Override
        public void onError(Exception error) {
            Log.e(MainActivity.class.getName(), error.toString());
            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_main);

        checkAuthorization();
        if (neighboursHandler != null) {
            neighboursHandler.unregister();
        }
        neighboursHandler = Api.getInstance().getNeighbours(neighboursListener);

        neighbourFragment = getPreparedNeighbourFragment();
        mapFragment = getPreparedMapFragment();
        selectFragment(mapFragment);

        nav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleNavigationItemSelected(item);
                return true;
            }
        });
    }

    private void handleNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                break;
            case R.id.action_requests:
                break;
            case R.id.action_neighbours:
                selectFragment(neighbourFragment);
                break;
            case R.id.action_map:
                selectFragment(mapFragment);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (neighboursHandler != null) {
            neighboursHandler.unregister();
        }
    }

    private void checkAuthorization() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String jwt = prefs.getString("jwt", null);

        if (jwt == null) {
            Intent intentAuth = new Intent(this, AuthActivity.class);
            startActivity(intentAuth);
        } else {
            Api.getInstance().setToken(jwt);
        }
    }

    private NeighbourFragment getPreparedNeighbourFragment() {
        if (neighbourFragment != null) {
            return neighbourFragment;
        }
        NeighbourFragment fragment = new NeighbourFragment();
        fragment.setListener(neighboursListener);
        return fragment;
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
}
