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

import java.util.HashMap;

public class MainActivity extends SingleFragmentActivity
        implements LoginFragment.LoginListener, MealPlannerFragment.PlannerListener {

    private static final String TAG = "MainActivity";

    private User mUser;

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

                mUser = new User(uid);

                for (DataSnapshot planSnapshot : dataSnapshot.child("mealPlans").getChildren()) {
                    long duration = (long) planSnapshot.child("duration").getValue();
                    String startDate = (String) planSnapshot.child("startDate").getValue();
                    Plan.Duration planDuration = duration == 14 ? Plan.Duration.TWO_WEEKS : Plan.Duration.ONE_WEEK;

                    Plan plan = new Plan(startDate, planDuration);

                    DataSnapshot titleSnapshot = planSnapshot.child("title");
                    if (titleSnapshot != null) {
                        plan.setTitle((String) titleSnapshot.getValue());
                    }


                    mUser.addPlan(plan);
                }

                Bundle bundle = new Bundle();
                bundle.putParcelable("User", mUser);

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

    @Override
    public void OnAddPlan(Plan newPlan) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users/" + mUser.getUid() + "/mealPlans");
        HashMap<String, Object> planValues = new HashMap<>();
        planValues.put("duration", newPlan.getDuration().days());
        planValues.put("startDate", newPlan.getStartDateString());
        planValues.put("title",  newPlan.getTitle());
        ref.push().setValue(planValues);
    }
}
