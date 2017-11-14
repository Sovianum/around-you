package ru.mail.park.aroundyou.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.UnknownHostException;

import ru.mail.park.aroundyou.MainActivity;
import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.auth.AuthActivity;
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.common.PreferencesInfo;
import ru.mail.park.aroundyou.datasource.DBApi;
import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.datasource.network.NetworkError;
import ru.mail.park.aroundyou.model.User;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

public class UserFragment extends Fragment {
    private Button logoutButton;
    private TextView loginTxt;
    private TextView aboutTxt;
    private ProgressBar progressBar;

    private User user;

    private ListenerHandler<Api.OnSmthGetListener<User>> userSelfHandler;
    private ListenerHandler<DBApi.OnDBDataGetListener<User>> userSelfHandlerDB;

    private DBApi.OnDBDataGetListener<User> userSelfListenerDB = new DBApi.OnDBDataGetListener<User>() {
        @Override
        public void onSuccess(User user) {
            setUser(user);
            setRefreshing(false);
        }

        @Override
        public void onError(Exception error) {
            Log.e(MainActivity.class.getName(), error.toString());
            Toast.makeText(UserFragment.this.getContext(), error.toString(), Toast.LENGTH_LONG).show();
        }
    };

    private Api.OnSmthGetListener<User> userSelfListener = new Api.OnSmthGetListener<User>() {
        @Override
        public void onSuccess(User user) {
            setSelfUser(user);
            setRefreshing(false);
        }

        @Override
        public void onError(Exception error) {
            Log.e(AuthActivity.class.getName(), error.toString());
            if (getActivity() == null) {
                return;
            }

            if (error instanceof NetworkError) {
                NetworkError casted = (NetworkError) error;
                int code = casted.getResponseCode();
                switch (code) {
                    case HTTP_NOT_FOUND: {
                        Toast.makeText(getActivity(), R.string.user_not_found_str, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case HTTP_INTERNAL_ERROR: {
                        Toast.makeText(getActivity(), R.string.server_internal_error_str, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    default: {
                        Toast.makeText(getActivity(), ((NetworkError) error).getErrMsg(), Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            } else if (error instanceof UnknownHostException){
                Toast.makeText(getContext(), R.string.connection_lost_str, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            }
            setRefreshing(false);
        }
    };

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
        loginTxt = userPageView.findViewById(R.id.login_txt);
        aboutTxt = userPageView.findViewById(R.id.about_txt);
        progressBar = userPageView.findViewById(R.id.progressBar);
        return userPageView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (userSelfHandler != null) {
            userSelfHandler.unregister();
        }

        if (userSelfHandlerDB != null) {
            userSelfHandlerDB.unregister();
        }
    }

    private void setSelfUser(final User user) {
        if (getActivity() == null) {
            return;
        }

        DBApi.getInstance(getActivity()).insertUser(user);
        DBApi.getInstance(getActivity()).setUserId(user.getId());
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getActivity()).edit();

        editor.putInt(PreferencesInfo.USER_SELF_ID, user.getId());
        editor.apply();
        setUser(user);
    }

    private void loadUser() {
        if (getActivity() == null) {
            return;
        }
        setRefreshing(true);

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
        int userSelfId  = prefs.getInt(PreferencesInfo.USER_SELF_ID, 0);

        if (userSelfId == 0) {
            userSelfHandler = Api.getInstance().getSelfInfo(userSelfListener);
        } else {
            userSelfHandlerDB = DBApi.getInstance(getActivity().getApplicationContext())
                    .getUser(userSelfListenerDB, userSelfId);
        }

    }

    public void setUser(final User user) {
        this.user = user;

        if (getActivity() == null) {
            return;
        }
        loginTxt.setText(user.getLogin());
        aboutTxt.setText(user.getAbout());

    }

    private void logout() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        prefs.edit().clear().apply();

        Intent intentAuth = new Intent(getActivity(), AuthActivity.class);
        startActivity(intentAuth);
    }

    private void setRefreshing(boolean refreshing) {
        if (refreshing) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
