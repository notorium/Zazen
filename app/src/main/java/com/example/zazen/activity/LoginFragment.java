package com.example.zazen.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zazen.R;
import com.example.zazen.async.HttpRequest_POST_Login;

import static com.example.zazen.activity.StartActivity.loginStatus;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    //    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public LoginFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment LoginFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static LoginFragment newInstance(String param1, String param2) {
//        LoginFragment fragment = new LoginFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    static TextView errorText;
    static EditText useridInput, passwordInput;
    static View loginScreen;
    static Button loginButton;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        loginScreen = getActivity().findViewById(R.id.loginScreen);
        loginButton = getActivity().findViewById(R.id.loginButton);
        errorText = getActivity().findViewById(R.id.errorText);
        useridInput = getActivity().findViewById(R.id.userid_editText);
        passwordInput = getActivity().findViewById(R.id.password_editText);

        loginButton.setOnClickListener(v -> loginButton());
        loginScreen.setOnClickListener(v -> {
            useridInput.clearFocus();
            passwordInput.clearFocus();
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public void loginButton() {
        loginButton.setEnabled(false);
        useridInput.clearFocus();
        passwordInput.clearFocus();

        String postStr = "{\"id\":\"" + useridInput.getText().toString() +
                "\",\"pass\":\"" + passwordInput.getText().toString() +
                "\"}";
        HttpRequest_POST_Login login = new HttpRequest_POST_Login(this.getActivity(), postStr);
        login.execute("http://fukuiohr2.sakura.ne.jp/2021/Zazen/login.php");
    }
}