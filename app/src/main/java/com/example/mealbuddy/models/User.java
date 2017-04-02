package com.example.mealbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Vector;

/**
 * Created by Rob Ford on 3/7/2017.
 */

/**
 * The User class provides a top-level object to store all the information for the data model of the
 * application.
 */
public class User implements Parcelable {

    private String mPassword;

    /**
     * User's username.
     */
    private String mUsername;

    /**
     * User's Firebase UID.
     */
    private String mUid;

    /**
     * Whether the user is logged in or not.
     */
    private boolean mUserLoggedIn;

    /**
     * Vector to store the list of the user's meal plans
     */
    private Vector<Plan> mPlans = new Vector<>();

    /**
     * Firebase authorization object.
     */
    private FirebaseAuth mAuth;

    /**
     * Listener for authentication changes.
     */
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    /**
     * Get the user's unique UID.
     * @return the user's UID
     */
    public String getUid() { return mUid; }

    /**
     * Get the user's password.
     * @return the user's password
     */
    public String getPassword() {
        return mPassword;
    }

    /**
     * Set the user's password.
     * @param password user's new password
     */
    public void setPassword(String password) {
        mPassword = password;
    }

    /**
     * Get the user's username used to login.
     * @return the username
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Set the user's username used to login.
     * @param username the new username
     */
    public void setUsername(String username) {
        mUsername = username;
    }

    /**
     * Indicates whether the user is logged in or not.
     * @return whether the user is logged in or not.
     */
    public boolean isUserLoggedIn() {
        return mUserLoggedIn;
    }

    /**
     * Get the Firebase authentication object.
     * @return the Firebase authentication.
     */
    public FirebaseAuth getAuth() {
        return mAuth;
    }

    /**
     * Get the Firebase authentication listener.
     * @return  the Firebase authentication listener.
     */
    public FirebaseAuth.AuthStateListener getAuthStateListener() {
        return mAuthStateListener;
    }

    /**
     * Get the list of the user's meal plans.
     * @return the list meal plans
     */
    public Vector<Plan> getPlans() {
        return mPlans;
    }

    /**
     * Add a new plan to the user's list of plans.
     * @param newPlan the new plan to add
     */
    public void addPlan(Plan newPlan) {
        mPlans.add(newPlan);
    }

    /**
     * Creates a new User object
     */
    public User() {
        //Firebase Login
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // Record whether the user is logged in or not
                if (user == null) {
                    mUserLoggedIn = false;
                } else {
                    mUserLoggedIn = true;
                }
            }
        };
    }

    /**
     * Creates a new User object with the given UID.
     * @param uid the UID for the new User
     */
    public User(String uid) {
        mUid = uid;
    }

    /**
     * Creator object required for the Parcelable interface.
     */
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
        dest.writeTypedList(mPlans);
    }

    /**
     * Private constructor required for Parcelable interface.
     * @param in Parcel to extract the object from
     */
    private User(Parcel in) {
        mUid = in.readString();
        in.readTypedList(mPlans, Plan.CREATOR);
    }
}
