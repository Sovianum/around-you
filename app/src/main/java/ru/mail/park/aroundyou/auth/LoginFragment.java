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

import ru.mail.park.aroundyou.Api;
import ru.mail.park.aroundyou.ListenerHandler;
import ru.mail.park.aroundyou.LoginUser;
import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.ReceivedData;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private EditText loginText;
    private EditText passwordText;
    private Button loginButton;
    private ProgressBar progressBar;
    private TextView linkToRegister;
    private View.OnClickListener setRegisterFragmentListener;
    private Api.OnSmthGetListener<ReceivedData> onLoginDataListener;
    private ListenerHandler<Api.OnSmthGetListener<ReceivedData>> handler;


    public LoginFragment() {
    }

    public void setListener(View.OnClickListener listener) {
        this.setRegisterFragmentListener = listener;
    }

    public void setListener(Api.OnSmthGetListener<ReceivedData> listener) {
        this.onLoginDataListener = listener;
    }

    public void setHandler(ListenerHandler<Api.OnSmthGetListener<ReceivedData>> handler) {
        this.handler = handler;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_login, container, false);
        loginText = view.findViewById(R.id.login_edit);
        passwordText = view.findViewById(R.id.password_edit);
        loginButton = view.findViewById(R.id.login_btn);
        progressBar = view.findViewById(R.id.progressBar);
        linkToRegister = view.findViewById(R.id.link_to_register);

        final LoginUser userStub = new LoginUser();
        userStub.setLogin("test3");
        userStub.setPassword("test3");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                handler = Api.getInstance().loginUser(onLoginDataListener, userStub);

            }
        });

        linkToRegister.setOnClickListener(setRegisterFragmentListener);


        return view;
    }

}
