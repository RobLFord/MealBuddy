package com.example.mealbuddy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Class : MyMealsFragment
 *
 * Description :
 *
 * This fragment displays the visual representation the Meals screen
 * The purpose of this fragment is display the meals that the user has.
 * This fragment is called when the Meal button is selected in the
 * bottom navigation bar.
 *
 */

public class MyMealsFragment extends Fragment {
    private static final String ARG_TEXT = "arg_text";

    private String mText;
    private TextView mTextView;

    public static Fragment newInstance(String text) {
        Fragment frag = new MyMealsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_meals, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // retrieve text and color from bundle or savedInstanceState
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            mText = args.getString(ARG_TEXT);
        } else {
            mText = savedInstanceState.getString(ARG_TEXT);
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_TEXT, mText);
        super.onSaveInstanceState(outState);
    }
}
