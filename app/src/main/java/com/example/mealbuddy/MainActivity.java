package com.example.mealbuddy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.mealbuddy.models.Plan;
import com.example.mealbuddy.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends SingleFragmentActivity
        implements LoginFragment.LoginListener {

    private static final String TAG = "MainActivity";

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

    @Override
    public void OnUserLogin(final String uid) {
        Log.d(TAG, "User login: " + uid);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users/" + uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = new User(uid);

                for (DataSnapshot planSnapshot : dataSnapshot.child("mealPlans").getChildren()) {
                    long duration = (long) planSnapshot.child("duration").getValue();
                    String startDate = (String) planSnapshot.child("startDate").getValue();

                    Plan.Duration planDuration = duration == 14 ? Plan.Duration.TWO_WEEKS : Plan.Duration.ONE_WEEK;

                    user.addPlan(new Plan(startDate, planDuration));
                }

                Bundle bundle = new Bundle();
                bundle.putParcelable("User", user);

                Fragment fragment = new MainFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
