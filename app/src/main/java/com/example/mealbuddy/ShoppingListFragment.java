package com.example.mealbuddy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mealbuddy.models.Plan;
import com.example.mealbuddy.models.User;

import java.util.Map;

/**
 * Created by Rob Ford on 3/9/2017.
 */

public class ShoppingListFragment extends Fragment {
    private static final String ARG_TEXT = "arg_text";

    private String mText;
    private TextView mTextView;
    private User mUser;

    public static Fragment newInstance(String text, User user) {
        Fragment frag = new ShoppingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putParcelable("User", user);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // retrieve text and color from bundle or savedInstanceState
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            mText = args.getString(ARG_TEXT);
            mUser = args.getParcelable("User");
        } else {
            mText = savedInstanceState.getString(ARG_TEXT);
            mUser = savedInstanceState.getParcelable("User");
        }

        TextView shoppingLists = (TextView) view.findViewById(R.id.shopping_list_text);
        shoppingLists.setMovementMethod(new ScrollingMovementMethod());

        StringBuffer listStringBuffer = new StringBuffer();

        for (Plan plan : mUser.getPlans()) {
            Map<Pair<String, String>, Float> ingredients = plan.summarize();

            listStringBuffer.append(String.format("%s: %s\n", plan.getTitle(), plan.getPlanPeriod()));

            for (Map.Entry<Pair<String, String>, Float> entry : ingredients.entrySet()) {
                Pair<String, String> key = entry.getKey();
                float amount = entry.getValue();

                String units = key.second == null ? "" : (key.second + " of ");
                listStringBuffer.append(String.format("%2.2f %s%s\n", amount, units, key.first));
            }

            listStringBuffer.append("\n");
        }

        shoppingLists.setText(listStringBuffer.toString());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_TEXT, mText);
        super.onSaveInstanceState(outState);
    }
}
