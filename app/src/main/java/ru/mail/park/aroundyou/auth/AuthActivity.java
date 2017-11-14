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
import android.widget.Toast;

import ru.mail.park.aroundyou.common.PreferencesInfo;
import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.MainActivity;
import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.model.ServerResponse;

public class AuthActivity extends AppCompatActivity {

    private LoginFragment loginFragment;
    private RegistrationFragment registrationFragment;
    private Fragment activeFragment;

    private ListenerHandler<Api.OnSmthGetListener<ServerResponse<String>>> loginHandler;
    private ListenerHandler<Api.OnSmthGetListener<ServerResponse<String>>> registerHandler;

    private Api.OnSmthGetListener<ServerResponse<String>> loginOnDataGetListener = new Api.OnSmthGetListener<ServerResponse<String>>() {
        @Override
        public void onSuccess(ServerResponse<String> response) {
            if (response.getData() != null) {
                onGettingToken(response.getData());
            }
            loginFragment.setLoading(false);
        }

        @Override
        public void onError(Exception error) {
            Log.e(AuthActivity.class.getName(), error.toString());
            Toast.makeText(AuthActivity.this, R.string.failed_to_authorize_str, Toast.LENGTH_SHORT).show();
            loginFragment.setLoading(false);
        }
    };

    private Api.OnSmthGetListener<ServerResponse<String>> registerOnDataGetListener = new Api.OnSmthGetListener<ServerResponse<String>>() {
        @Override
        public void onSuccess(ServerResponse<String> response) {
            if (response.getData() != null) {
                onGettingToken(response.getData());
                loginFragment.setLoading(false);
            }
        }

        @Override
        public void onError(Exception error) {
            Log.e(AuthActivity.class.getName(), error.toString());
            loginFragment.setLoading(false);
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

        editor.putString(PreferencesInfo.JSON_WEB_TOKEN, token);
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
