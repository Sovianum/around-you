package ru.mail.park.aroundyou.auth;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.UnknownHostException;
import java.util.Locale;

import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.datasource.network.NetworkError;
import ru.mail.park.aroundyou.model.ServerResponse;
import ru.mail.park.aroundyou.model.User;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

public class LoginFragment extends AuthFragment {
    private EditText loginText;
    private EditText passwordText;
    private Button loginButton;
    private ProgressBar progressBar;
    private TextView linkToRegister;
    private View.OnClickListener onClickListener;
    private ListenerHandler<Api.OnSmthGetListener<ServerResponse<String>>> handler;

    private Api.OnSmthGetListener<ServerResponse<String>> onDataGetListener = new Api.OnSmthGetListener<ServerResponse<String>>() {
        @Override
        public void onSuccess(ServerResponse<String> response) {
            if (response.getData() != null) {
                onGettingToken(response.getData());
            }
            setLoading(false);
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
                        Toast.makeText(getActivity(), R.string.failed_to_authorize_str, Toast.LENGTH_SHORT).show();
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
            setLoading(false);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_login, container, false);
        loginText = view.findViewById(R.id.login_edit);
        passwordText = view.findViewById(R.id.password_edit);
        loginButton = view.findViewById(R.id.login_btn);
        progressBar = view.findViewById(R.id.progressBar);
        linkToRegister = view.findViewById(R.id.link_to_register);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final User userStub = new User();
                userStub.setLogin(loginText.getText().toString());
                userStub.setPassword(passwordText.getText().toString());

                progressBar.setVisibility(View.VISIBLE);
                handler = Api.getInstance().loginUser(onDataGetListener, userStub);
            }
        });

        linkToRegister.setOnClickListener(onClickListener);
        return view;
    }

    @Override
    public void onStop() {
        if (handler != null) {
            handler.unregister();
        }
        super.onStop();
    }

    public void setLoading(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.onClickListener = listener;
    }
}
