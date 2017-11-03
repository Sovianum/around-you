package ru.mail.park.aroundyou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

import ru.mail.park.aroundyou.auth.AuthActivity;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView nav;
    private NeighbourFragment neighbourFragment;
    private SupportMapFragment mapFragment;

    private Fragment activeFragment;
    private String jwt = null;

    /*private ListenerHandler<OnNeighboursGetListener> neighboursHandler;

      private OnNeighboursGetListener neighboursListener = new OnNeighboursGetListener() {
        @Override
        public void onGettingSuccess(List<NeighbourItem> neighbourItems) {
            neighbourFragment.loadItems(neighbourItems);
        }

        @Override
        public void onGettingError(Exception error) {
            
        }

    };*/

    private ListenerHandler<Api.OnSmthGetListener<List<NeighbourItem>>> neighboursHandler;

    private Api.OnSmthGetListener<List<NeighbourItem>> neighboursListener = new Api.OnSmthGetListener<List<NeighbourItem>>() {
        @Override
        public void onGettingSuccess(List<NeighbourItem> neighbourItems) {
            neighbourFragment.loadItems(neighbourItems);
        }

        @Override
        public void onGettingError(Exception error) {

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        neighbourFragment = new NeighbourFragment();
        neighbourFragment.setListener(neighboursListener);
        mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

            }
        });

        //neighbourFragment.loadItems(buildItemList());

        if (neighboursHandler != null) {
            neighboursHandler.unregister();
        }
        neighboursHandler = Api.getInstance().getNeighbours(neighboursListener);

        selectFragment(neighbourFragment);

        nav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleNavigationItemSelected(item);
                return true;
            }
        });

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        //SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String jwt = prefs.getString("jwt", null);

        //jwt = null;

        if (jwt == null) {
            Intent intentAuth = new Intent(this, AuthActivity.class);
            startActivity(intentAuth);
        } else {
            Api.getInstance().setToken(jwt);
        }
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (neighboursHandler != null) {
            neighboursHandler.unregister();
        }
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

    private List<NeighbourItem> buildItemList() {
        final int cardCnt = 3;
        List<NeighbourItem> items = new ArrayList<>();

        for (int i = 0; i != cardCnt; i++) {
            items.add(NeighbourMock.getRandomNeighbourItem());
        }
        return items;
    }

    private void toastText(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
