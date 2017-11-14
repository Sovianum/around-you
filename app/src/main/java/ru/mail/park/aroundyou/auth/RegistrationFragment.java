package ru.mail.park.aroundyou.auth;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.net.UnknownHostException;

import ru.mail.park.aroundyou.datasource.network.Api;
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.datasource.network.NetworkError;
import ru.mail.park.aroundyou.model.ServerResponse;
import ru.mail.park.aroundyou.model.User;

import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;


public class RegistrationFragment extends AuthFragment {

    private EditText loginText;
    private EditText passwordText;
    private EditText aboutText;
    private EditText ageText;
    private Spinner spinner;
    private Button registerButton;
    private ProgressBar progressBar;
    private TextView linkToLogin;
    private View.OnClickListener onClickListener;
    private ListenerHandler<Api.OnSmthGetListener<ServerResponse<String>>> handler;

    private Api.OnSmthGetListener<ServerResponse<String>> onDataGetListener = new Api.OnSmthGetListener<ServerResponse<String>>() {
        @Override
        public void onSuccess(ServerResponse<String> response) {
            if (response.getData() != null) {
                onGettingToken(response.getData());
                setLoading(false);
            }
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
                    case HTTP_CONFLICT: {
                        Toast.makeText(getActivity(), R.string.duplicate_user_str, Toast.LENGTH_SHORT).show();
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
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_registration, container, false);
        loginText = view.findViewById(R.id.login_edit);
        passwordText = view.findViewById(R.id.password_edit);
        aboutText = view.findViewById(R.id.about_edit);
        ageText = view.findViewById(R.id.age_edit);
        spinner = view.findViewById(R.id.spinner_sex);
        registerButton = view.findViewById(R.id.register_btn);
        progressBar = view.findViewById(R.id.progressBar);
        linkToLogin = view.findViewById(R.id.link_to_login);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final User userStub = new User();
                userStub.setLogin(loginText.getText().toString());
                userStub.setPassword(passwordText.getText().toString());
                userStub.setAbout(aboutText.getText().toString());
                userStub.setSex(((TextView)spinner.getSelectedView()).getText().toString());

                int age = Integer.parseInt(ageText.getText().toString());
                userStub.setAge(age);

                setLoading(true);
                handler = Api.getInstance().registerUser(onDataGetListener, userStub);
            }
        });

        linkToLogin.setOnClickListener(onClickListener);
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
