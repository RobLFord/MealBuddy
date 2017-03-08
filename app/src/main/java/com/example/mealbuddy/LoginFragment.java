package com.example.mealbuddy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


/**
 * Created by rf122 on 3/6/2017.
 */

public class LoginFragment extends Fragment {

    private User mUser;

    private EditText mUsernameField;
    private EditText mPasswordField;
    private Button mLoginButton;
    private TextView mSignupText;
    private TextView mForgotPasswordText;
    private TextView mForgotUsernameText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser = new User();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        mUsernameField = (EditText) v.findViewById(R.id.login_username);
        mUsernameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //This space is intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mUser.setUsername(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //This space is intentionally left blank
            }
        });

        mPasswordField = (EditText) v.findViewById(R.id.login_password);
        mPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //This space is intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mUser.setPassword(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    mLoginButton.setEnabled(false);
                } else {
                    mLoginButton.setEnabled(true);
                }
            }
        });

        mLoginButton = (Button) v.findViewById(R.id.login_button);
        mLoginButton.setEnabled(false);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser.getAuth().signInWithEmailAndPassword(mUser.getUsername().trim(), mUser.getPassword().trim())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                int messageToast = 0;

                                if (!task.isSuccessful()) {
                                    messageToast = R.string.login_fail_toast;
                                } else {
                                    messageToast = R.string.login_success_toast;
                                }

                                mUser.setPassword(null);
                                Toast.makeText(getActivity(), messageToast, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        mSignupText = (TextView) v.findViewById(R.id.login_signup);
        mSignupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                SignUpFragment dialog = new SignUpFragment();
                dialog.show(manager, null);
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mUser.getAuth().addAuthStateListener(mUser.getAuthStateListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mUser.getAuthStateListener() != null) {
            mUser.getAuth().removeAuthStateListener(mUser.getAuthStateListener());
        }
    }
}
