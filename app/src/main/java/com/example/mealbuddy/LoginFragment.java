package com.example.mealbuddy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealbuddy.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


/**
 * Class : LoginFragment
 *
 * Description :
 *
 * This fragment displays the visual representation for the login screen.
 * The purpose of this fragment is to display the login screen and pass the user's login
 * information to the Firebase server for authentication.
 * This fragment is called ...
 *
 */

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    // User information and Firebase user authentication
    private User mUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // View items
    private EditText mUsernameField;
    private EditText mPasswordField;
    private Button mLoginButton;
    private TextView mSignupText;
    private TextView mForgotPasswordText;
    private TextView mForgotUsernameText;

    // listener for login events
    private LoginListener mLoginListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser = new User();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        // Create Firebase authentication objects and connect listener for login event
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebase_user = firebaseAuth.getCurrentUser();

                if (firebase_user != null) {
                    Log.i(TAG, "User logged in: " + firebase_user.getEmail());

                    mLoginListener.OnUserLogin(firebase_user.getUid());
                }
            }
        };

        // Connect username text box
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

        // Connect password text box
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

        // Connect login button
        mLoginButton = (Button) v.findViewById(R.id.login_button);
        mLoginButton.setEnabled(false);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser.getAuth().signInWithEmailAndPassword(mUser.getUsername().trim(), mUser.getPassword().trim())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // User is logged in successfully
                                int messageToast = 0;

                                if (!task.isSuccessful()) {
                                    messageToast = R.string.login_fail_toast;
                                    Log.d(TAG, "Login is failed");
                                } else {
                                    messageToast = R.string.login_success_toast;
                                    Log.d(TAG, "Login is successful");
                                }

                                mUser.setPassword(null);
                                Toast.makeText(getActivity(), messageToast, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Display dialog that allows a user to create an account with an email and password
        mSignupText = (TextView) v.findViewById(R.id.login_signup);
        mSignupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                SignUpFragment dialog = new SignUpFragment();
                dialog.setSignUpListener(new SignUpFragment.DialogFragmentListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog, String email, String password) {
                        mUser.getAuth().createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        DatabaseReference db = FirebaseDatabase.getInstance()
                                                .getReference();

                                        Map<String, Object> user_values = new HashMap<>();
                                        user_values.put("name", "");
                                        user_values.put("email", mAuth.getCurrentUser().getEmail());
                                        db.child("users").child(mAuth.getCurrentUser().getUid())
                                                .setValue(user_values);
                                    }
                                });
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {}
                });
                dialog.show(manager, null);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mLoginListener = (LoginListener) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException(context.toString()
                    + " must implement LoginListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mUser.getAuthStateListener());
        }
    }

    /**
     * Interface for callback when user successfully logs in.
     */
    public interface LoginListener {
        void OnUserLogin(String uid);
    }
}
