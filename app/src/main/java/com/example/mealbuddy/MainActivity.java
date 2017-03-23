package com.example.mealbuddy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.mealbuddy.models.User;

public class MainActivity extends SingleFragmentActivity
        implements LoginFragment.LoginListener {

    private static final String TAG = "MainActivity";

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

    @Override
    public void OnUserLogin(String uid) {
        Log.d(TAG, "User login: " + uid);

        User user = new User(uid);
        Bundle bundle = new Bundle();
        bundle.putParcelable("User", user);

        Fragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
