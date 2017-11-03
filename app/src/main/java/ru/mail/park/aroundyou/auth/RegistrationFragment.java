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
import ru.mail.park.aroundyou.RecievedData;
import ru.mail.park.aroundyou.RegisterUser;

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
    private View.OnClickListener setLoginFragmentListener;
    private Api.OnSmthGetListener<RecievedData> onRegisterDataListener;
    private ListenerHandler<Api.OnSmthGetListener<RecievedData>> handler;

    public RegistrationFragment() {
    }

    public void setListener(View.OnClickListener listener) {
        this.setLoginFragmentListener = listener;
    }


    public void setListener(Api.OnSmthGetListener<RecievedData> listener) {
        this.onRegisterDataListener = listener;
    }

    public void setHandler(ListenerHandler<Api.OnSmthGetListener<RecievedData>> handler) {
        this.handler = handler;
    }


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

        final RegisterUser userStub = new RegisterUser();
        userStub.setLogin("test3");
        userStub.setPassword("test3");
        userStub.setAbout("about");
        userStub.setAge(22);
        userStub.setSex("M");


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                handler = Api.getInstance().registerUser(onRegisterDataListener, userStub);

            }
        });

        linkToLogin.setOnClickListener(setLoginFragmentListener);

        return view;
    }

}
