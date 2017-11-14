package ru.mail.park.aroundyou.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import ru.mail.park.aroundyou.MainActivity;
import ru.mail.park.aroundyou.common.PreferencesInfo;

public class AuthFragment extends Fragment {
    protected void onGettingToken(String token) {
        if (getActivity() == null) {
            return;
        }

        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getActivity()).edit();

        editor.putString(PreferencesInfo.JSON_WEB_TOKEN, token);
        editor.apply();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}
