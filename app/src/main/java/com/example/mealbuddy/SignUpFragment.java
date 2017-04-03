package com.example.mealbuddy;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Rob Ford on 3/7/2017.
 */

/**
 * Fragment that presents a dialog for creating an account by entering a username and password.
 */
public class SignUpFragment extends DialogFragment {

    private EditText mEmailText;
    private EditText mPasswordText;

    /**
     * Defines a listener interface to provide notification of the selected action
     */
    public interface DialogFragmentListener {
        void onDialogPositiveClick(DialogFragment dialog, String email, String password);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    private DialogFragmentListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Display the dialog to the user
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_signup, null);

        mEmailText = (EditText) v.findViewById(R.id.email_dialog);
        mPasswordText = (EditText) v.findViewById(R.id.password_dialog);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.signup_dialog_text)
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = mEmailText.getText().toString();
                        String password = mPasswordText.getText().toString();
                        mListener.onDialogPositiveClick(SignUpFragment.this, email, password);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick(SignUpFragment.this);
                    }
                })
                .create();
    }

    public void setSignUpListener(DialogFragmentListener listener) {
        mListener = listener;
    }
}
