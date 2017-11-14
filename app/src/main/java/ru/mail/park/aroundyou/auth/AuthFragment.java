package ru.mail.park.aroundyou.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import ru.mail.park.aroundyou.MainActivity;

public class AuthFragment extends Fragment {
    protected void onGettingToken(String token) {
        if (getActivity() == null) {
            return;
        }

        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getActivity()).edit();

        editor.putString("jwt", token);
        editor.apply();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}
