package com.example.mealbuddy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by Rob Ford on 3/8/2017.
 */

public class MainFragment extends Fragment{
    private static final String TAG = "MainFragment";
    private static final String SELECTED_ITEM = "arg_selected_item";

    private BottomNavigationView mBottomNav;
    private int mSelectedItem;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        User user = bundle.getParcelable("User");
        Log.i(TAG, "User: " + user.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        mBottomNav = (BottomNavigationView) v.findViewById(R.id.nav_menu);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });


        selectFragment(mBottomNav.getMenu().findItem(R.id.menu_meal_planner));
        mBottomNav.getMenu().findItem(R.id.menu_meal_planner).setChecked(true);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(outState);
    }

    private void selectFragment(MenuItem item) {
        // update selected item
        mSelectedItem = item.getItemId();

        // uncheck the other items.
        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
            if (menuItem.getItemId() != mSelectedItem) {
                menuItem.setChecked(false);
            }
        }

        Fragment frag = null;
        // init corresponding fragment
        switch (mSelectedItem) {
            case R.id.menu_meal_planner:
                frag = MealPlannerFragment.newInstance(getString(R.string.menu_meal_planner));
                break;
            case R.id.menu_my_meals:
                frag = MyMealsFragment.newInstance(getString(R.string.menu_my_meals));
                break;
            case R.id.menu_browse:
                frag = BrowseFragment.newInstance(getString(R.string.menu_browse));
                break;
            case R.id.menu_shopping_list:
                frag = ShoppingListFragment.newInstance(getString(R.string.menu_shopping_list));
                break;
        }


        updateToolbarText(item.getTitle());

        if (frag != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_main_container, frag, frag.getTag());
            ft.commit();
        }
    }

    private void updateToolbarText(CharSequence text) {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"OnPause");
        updateToolbarText(getResources().getString(R.string.app_name));
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"OnStop");
        updateToolbarText(getResources().getString(R.string.app_name));
    }
}
