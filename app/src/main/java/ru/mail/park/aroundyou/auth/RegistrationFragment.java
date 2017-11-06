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
import android.widget.Spinner;
import android.widget.TextView;

import ru.mail.park.aroundyou.Api;
import ru.mail.park.aroundyou.ListenerHandler;
import ru.mail.park.aroundyou.R;
import ru.mail.park.aroundyou.ReceivedData;
import ru.mail.park.aroundyou.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {

    private EditText loginText;
    private EditText passwordText;
    private EditText aboutText;
    private EditText ageText;
    private EditText sexText;
    private Spinner spinner;
    private Button loginButton;
    private ProgressBar progressBar;
    private TextView linkToLogin;
    private View.OnClickListener onClickListener;
    private Api.OnSmthGetListener<ReceivedData> onDataGetListener;
    private ListenerHandler<Api.OnSmthGetListener<ReceivedData>> handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_registration, container, false);
        loginText = view.findViewById(R.id.login_edit);
        passwordText = view.findViewById(R.id.password_edit);
        aboutText = view.findViewById(R.id.about_edit);
        ageText = view.findViewById(R.id.age_edit);
        spinner = view.findViewById(R.id.spinner_sex);
        loginButton = view.findViewById(R.id.register_btn);
        progressBar = view.findViewById(R.id.progressBar);
        linkToLogin = view.findViewById(R.id.link_to_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final User userStub = new User();
                userStub.setLogin(loginText.getText().toString());
                userStub.setPassword(passwordText.getText().toString());
                userStub.setAbout(aboutText.getText().toString());
                userStub.setSex(sexText.getText().toString());

                int age = Integer.parseInt(ageText.getText().toString());   // TODO: 11/6/17 add handlers in case of failed parse
                userStub.setAge(age);

                progressBar.setVisibility(View.VISIBLE);
                handler = Api.getInstance().registerUser(onDataGetListener, userStub);
            }
        });

        linkToLogin.setOnClickListener(onClickListener);
        return view;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public void setOnDataGetListener(Api.OnSmthGetListener<ReceivedData> listener) {
        this.onDataGetListener = listener;
    }

    public void setHandler(ListenerHandler<Api.OnSmthGetListener<ReceivedData>> handler) {
        this.handler = handler;
    }
}
