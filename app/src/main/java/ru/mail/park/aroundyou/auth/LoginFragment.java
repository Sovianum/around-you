package ru.mail.park.aroundyou.auth;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.mail.park.aroundyou.datasource.Api;
import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.model.ServerResponse;
import ru.mail.park.aroundyou.model.User;

public class LoginFragment extends Fragment {
    private EditText loginText;
    private EditText passwordText;
    private Button loginButton;
    private ProgressBar progressBar;
    private TextView linkToRegister;
    private View.OnClickListener onClickListener;
    private Api.OnSmthGetListener<ServerResponse<String>> onDataGetListener;
    private ListenerHandler<Api.OnSmthGetListener<ServerResponse<String>>> handler;

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

    public void setOnClickListener(View.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public void setOnDataGetListener(Api.OnSmthGetListener<ServerResponse<String>> listener) {
        this.onDataGetListener = listener;
    }

    public void setHandler(ListenerHandler<Api.OnSmthGetListener<ServerResponse<String>>> handler) {
        this.handler = handler;
    }
}
