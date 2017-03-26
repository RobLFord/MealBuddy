package com.example.mealbuddy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.mealbuddy.models.DayPlan;
import com.example.mealbuddy.models.Ingredient;
import com.example.mealbuddy.models.Plan;
import com.example.mealbuddy.models.Recipe;
import com.example.mealbuddy.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;

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

                mUser = readUserFromFirebaseDatabase(uid, dataSnapshot);

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

    private User readUserFromFirebaseDatabase(String uid, DataSnapshot dataSnapshot) {
        User user = new User(uid);

        // Read user's meal plans
        for (DataSnapshot planSnapshot : dataSnapshot.child("mealPlans").getChildren()) {
            user.addPlan(readPlanFromFirebaseDatabase(planSnapshot));
        }

        return user;
    }

    private Plan readPlanFromFirebaseDatabase(DataSnapshot planSnapshot) {
        long durationDays = (long) planSnapshot.child("duration").getValue();
        Plan.Duration duration = durationDays == 14 ? Plan.Duration.TWO_WEEKS : Plan.Duration.ONE_WEEK;
        String startDate = (String) planSnapshot.child("startDate").getValue();
        String planTitle = (String) planSnapshot.child("title").getValue();

        Plan plan = new Plan(startDate, duration);
        plan.setTitle(planTitle);

        for (DataSnapshot daySnapshot : planSnapshot.child("days").getChildren()) {
            addDayPlanToPlan(plan, daySnapshot);
        }

        return plan;
    }

    private void addDayPlanToPlan(Plan plan, DataSnapshot daySnapshot) {
        long day = (Long) daySnapshot.child("day").getValue();
        GregorianCalendar planDate = new GregorianCalendar();
        planDate.setTime(plan.getStartDate());
        planDate.add(Calendar.DAY_OF_MONTH, (int) day - 1);

        DayPlan dayPlan = new DayPlan(planDate.getTime());

        for (DataSnapshot mealSnapshot : daySnapshot.child("meals").getChildren()) {
            String mealName = (String) mealSnapshot.child("name").getValue();
            long servings = (Long) mealSnapshot.child("servings").getValue();
            Vector<Ingredient> ingredients = new Vector<>();

            for (DataSnapshot ingredientSnapshot : mealSnapshot.child("ingredients").getChildren()) {
                double amount = ingredientSnapshot.child("amount").getValue(Double.class);
                String name = (String) ingredientSnapshot.child("name").getValue();
                String unit = (String) ingredientSnapshot.child("unit").getValue();

                ingredients.add(new Ingredient(name, (float) amount, unit));
            }

            dayPlan.addRecipe(new Recipe(mealName, (int) servings, ingredients));
        }

        plan.addDayPlan(dayPlan, (int) day - 1);
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
