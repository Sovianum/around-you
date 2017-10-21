package ru.mail.park.aroundyou;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView nav;
    private NeighbourFragment neighbourFragment;
    private SupportMapFragment mapFragment;

    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        neighbourFragment = new NeighbourFragment();
        mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

            }
        });

        neighbourFragment.loadItems(buildItemList());

        selectFragment(neighbourFragment);

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
        final int cardCnt = 100;
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
