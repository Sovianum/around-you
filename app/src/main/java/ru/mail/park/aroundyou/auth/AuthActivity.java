package ru.mail.park.aroundyou.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import ru.mail.park.aroundyou.Api;
import ru.mail.park.aroundyou.ListenerHandler;
import ru.mail.park.aroundyou.MainActivity;
import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.ReceivedData;

public class AuthActivity extends AppCompatActivity {

    private LoginFragment loginFragment;
    private RegistrationFragment registrationFragment;
    private Fragment activeFragment;

    private ListenerHandler<Api.OnSmthGetListener<ReceivedData>> loginHandler;
    private ListenerHandler<Api.OnSmthGetListener<ReceivedData>> registerHandler;

    private Api.OnSmthGetListener<ReceivedData> loginOnDataGetListener = new Api.OnSmthGetListener<ReceivedData>() {
        @Override
        public void onSuccess(ReceivedData response) {
            if (response.getData() != null) {
                onGettingToken(response.getData());
            }
        }

        @Override
        public void onError(Exception error) {
            Log.e(AuthActivity.class.getName(), error.toString());
        }
    };

    private Api.OnSmthGetListener<ReceivedData> registerOnDataGetListener = new Api.OnSmthGetListener<ReceivedData>() {
        @Override
        public void onSuccess(ReceivedData response) {
            if (response.getData() != null) {
                onGettingToken(response.getData());
            }
        }

        @Override
        public void onError(Exception error) {
            Log.e(AuthActivity.class.getName(), error.toString());
        }
    };

    private View.OnClickListener loginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectFragment(loginFragment);
        }
    };

    private View.OnClickListener registerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectFragment(registrationFragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        registrationFragment = new RegistrationFragment();
        registrationFragment.setOnClickListener(registerOnClickListener);
        registrationFragment.setOnDataGetListener(registerOnDataGetListener);
        registrationFragment.setHandler(registerHandler);

        loginFragment = new LoginFragment();
        loginFragment.setOnClickListener(loginOnClickListener);
        loginFragment.setOnDataGetListener(loginOnDataGetListener);
        loginFragment.setHandler(loginHandler);

        selectFragment(loginFragment);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (loginHandler != null) {
            loginHandler.unregister();
        }

        if (registerHandler != null) {
            registerHandler.unregister();
        }
    }

    private void onGettingToken(String token) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext()).edit();

        editor.putString("jwt", token);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
        fragmentTransaction.add(R.id.auth_fragment_container, activeFragment);
        fragmentTransaction.commit();
    }
}
