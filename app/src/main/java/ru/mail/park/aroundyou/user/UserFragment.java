package ru.mail.park.aroundyou.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.auth.AuthActivity;

public class UserFragment extends Fragment {
    private Button logoutButton;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View userPageView = inflater.inflate(R.layout.fragment_user_page, container, false);
        logoutButton = userPageView.findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        return userPageView;
    }

    private void logout() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        prefs.edit().clear().apply();

        Intent intentAuth = new Intent(getActivity(), AuthActivity.class);
        startActivity(intentAuth);
    }
}
