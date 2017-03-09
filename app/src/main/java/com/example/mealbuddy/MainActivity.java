package com.example.mealbuddy;

import android.support.v4.app.Fragment;
import android.util.Log;

public class MainActivity extends SingleFragmentActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

}
