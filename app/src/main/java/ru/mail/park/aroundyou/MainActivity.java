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
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.datasource.Api;
import ru.mail.park.aroundyou.datasource.DBApi;
import ru.mail.park.aroundyou.datasource.MemCache;
import ru.mail.park.aroundyou.neighbours.NeighbourFragment;
import ru.mail.park.aroundyou.model.User;
import ru.mail.park.aroundyou.requests.MeetRequestFragment;
import ru.mail.park.aroundyou.model.MeetRequest;
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
    private ListenerHandler<Api.OnSmthGetListener<List<User>>> neighboursHandler;
    private ListenerHandler<DBApi.OnDBDataGetListener<List<User>>> neighboursHandlerDB;
    private ListenerHandler<DBApi.OnDBDataGetListener<User>> userHandler;

    private Api.OnSmthGetListener<List<User>> neighboursListener = new Api.OnSmthGetListener<List<User>>() {
        @Override
        public void onSuccess(List<User> neighbourItems) {
            neighbourFragment.loadItems(neighbourItems);
            cacheNeighbours(neighbourItems);
            cacheUsers(neighbourItems);
            neighbourFragment.setRefreshing(false);
        }

        @Override
        public void onError(Exception error) {
            Log.e(MainActivity.class.getName(), error.toString());
            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            neighbourFragment.setRefreshing(false);
        }
    };

    private Api.OnSmthGetListener<List<MeetRequest>> onGetOutcomeRequestsListener = new Api.OnSmthGetListener<List<MeetRequest>>() {

        @Override
        public void onSuccess(List<MeetRequest> items) {
            MemCache.clearAndAddOutcomeRequests(items);
            outcomeRequestsFragment.loadItems(items);
            outcomeRequestsFragment.setRefreshing(false);
        }

        @Override
        public void onError(Exception error) {
            Log.e(MainActivity.class.getName(), error.toString());
            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            outcomeRequestsFragment.setRefreshing(false);
        }
    };

    private Api.OnSmthGetListener<List<MeetRequest>> onGetIncomeRequestsListener = new Api.OnSmthGetListener<List<MeetRequest>>() {

        @Override
        public void onSuccess(List<MeetRequest> items) {
            MemCache.clearAndAddIncomeRequests(items);
            incomeRequestsFragment.loadItems(items);
            incomeRequestsFragment.setRefreshing(false);
        }

        @Override
        public void onError(Exception error) {
            Log.e(MainActivity.class.getName(), error.toString());
            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            incomeRequestsFragment.setRefreshing(false);
        }
    };

    private DBApi.OnDBDataGetListener<List<User>> neighboursListenerDB = new DBApi.OnDBDataGetListener<List<User>>() {
        @Override
        public void onSuccess(List<User> items) {
            neighbourFragment.loadItems(items);
            neighbourFragment.setRefreshing(false);
        }

        @Override
        public void onError(Exception error) {
            Log.e(MainActivity.class.getName(), error.toString());
            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            neighbourFragment.setRefreshing(false);
        }
    };

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

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_main);

        checkAuthorization();
        //getApplicationContext().deleteDatabase("AroundYouDB.db");

        neighbourFragment = getPreparedNeighbourFragment();
        if (neighboursHandler != null) {
            neighboursHandler.unregister();
        };

        if (neighboursHandlerDB != null) {
            neighboursHandlerDB.unregister();
        }

        neighboursHandlerDB = DBApi.getInstance(this).getNeighbours(neighboursListenerDB);

        Pusher.getInstance().subscribe(pushListener);

        mapFragment = getPreparedMapFragment();
        outcomeRequestsFragment = getPreparedOutcomeRequestsFragment();
        incomeRequestsFragment = getPreparedIncomeRequestsFragment();
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
        nav.setSelectedItemId(R.id.action_neighbours);
        //nav.setSelectedItemId(R.id.action_income_requests);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (neighboursHandler != null) {
            neighboursHandler.unregister();
        }

        if (neighboursHandlerDB != null) {
            neighboursHandlerDB.unregister();
        }

        Pusher.getInstance().unSubscribe();
        if (userHandler != null) {
            userHandler.unregister();
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
        String jwt = prefs.getString("jwt", null);

        if (jwt == null) {
            Intent intentAuth = new Intent(this, AuthActivity.class);
            startActivity(intentAuth);
        } else {
            Api.getInstance().setToken(jwt);
        }
    }

    private MeetRequestFragment getPreparedIncomeRequestsFragment() {
        if (incomeRequestsFragment != null) {
            return incomeRequestsFragment;
        }
        MeetRequestFragment fragment = new IncomeMeetRequestFragment();
        fragment.setListener(onGetIncomeRequestsListener);
        return fragment;
    }

    private MeetRequestFragment getPreparedOutcomeRequestsFragment() {
        if (outcomeRequestsFragment != null) {
            return outcomeRequestsFragment;
        }

        MeetRequestFragment fragment = new OutcomeMeetRequestFragment();
        fragment.setListener(onGetOutcomeRequestsListener);
        return fragment;
    }

    private NeighbourFragment getPreparedNeighbourFragment() {
        if (neighbourFragment != null) {
            return neighbourFragment;
        }
        NeighbourFragment fragment = new NeighbourFragment();
        fragment.setListener(neighboursListener);
        fragment.setListenerDB(neighboursListenerDB);
        fragment.setHandler(neighboursHandler);
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

    public void cacheNeighbours(List<User> neighbourItems) {
        DBApi.getInstance(this).insertNeighbours(neighbourItems);
    }

    public void cacheUsers(List<User> users) {
        DBApi.getInstance(this).insertUsers(users);
    }
}
