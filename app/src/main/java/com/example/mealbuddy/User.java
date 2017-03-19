package com.example.mealbuddy;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Rob Ford on 3/7/2017.
 */

public class User implements Parcelable {

    private String mPassword;
    private String mUsername;
    private String mUid;
    private boolean mUserLoggedIn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public String getUid() { return mUid; }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public boolean isUserLoggedIn() {
        return mUserLoggedIn;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public FirebaseAuth.AuthStateListener getAuthStateListener() {
        return mAuthStateListener;
    }

    public User() {
        //Firebase Login
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {
                    mUserLoggedIn = false;
                } else {
                    mUserLoggedIn = true;
                }
            }
        };
    }

    public User(String uid) {
        mUid = uid;
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUid);
    }

    private User(Parcel in) {
        mUid = in.readString();
    }
}
