package ru.mail.park.aroundyou.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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

    private ListenerHandler<Api.OnSmthGetListener<ReceivedData>> userLoginHandler;
    private ListenerHandler<Api.OnSmthGetListener<ReceivedData>> userRegisterHandler;

    private Api.OnSmthGetListener<ReceivedData> userLoginListener = new Api.OnSmthGetListener<ReceivedData>() {
        @Override
        public void onGettingSuccess(ReceivedData items) {
            if (items.getData() != null) {
                onGettingToken(items.getData());
            }

        }

        @Override
        public void onGettingError(Exception error) {

        }

    };

    private Api.OnSmthGetListener<ReceivedData> userRegisterListener = new Api.OnSmthGetListener<ReceivedData>() {
        @Override
        public void onGettingSuccess(ReceivedData items) {
            if (items.getData() != null) {
                onGettingToken(items.getData());
            }

        }

        @Override
        public void onGettingError(Exception error) {

        }

    };

    //private ListenerHandler<View.OnClickListener> loginFragmentHandler;
    //private ListenerHandler<View.OnClickListener> registerFragmentHandler;

    private View.OnClickListener setLoginFragmentListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectFragment(loginFragment);
        }
    };

    private View.OnClickListener setRegisterFragmentListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectFragment(registrationFragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        loginFragment = new LoginFragment();
        loginFragment.setListener(setRegisterFragmentListener);
        registrationFragment = new RegistrationFragment();
        registrationFragment.setListener(setLoginFragmentListener);
        selectFragment(loginFragment);

        if (userLoginHandler != null) {
            userLoginHandler.unregister();
        }

        loginFragment.setListener(userLoginListener);
        loginFragment.setHandler(userLoginHandler);

        if (userRegisterHandler != null) {
            userRegisterHandler.unregister();
        }

        loginFragment.setListener(userRegisterListener);
        loginFragment.setHandler(userRegisterHandler);

        /*if (loginFragmentHandler != null) {
            loginFragmentHandler.unregister();
        }

        if (registerFragmentHandler != null) {
            registerFragmentHandler.unregister();
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (userLoginHandler != null) {
            userLoginHandler.unregister();
        }

        if (userRegisterHandler != null) {
            userRegisterHandler.unregister();
        }
        /*if (loginFragmentHandler != null) {
            loginFragmentHandler.unregister();
        }

        if (registerFragmentHandler != null) {
            registerFragmentHandler.unregister();
        }*/
    }

    private void onGettingToken(String token) {
        //SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext()).edit();

        editor.putString("jwt", token);
        editor.apply();


        //SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        //String jwt = prefs.getString("jwt", null);

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
