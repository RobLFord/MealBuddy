package com.example.mealbuddy;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mealbuddy.models.DayPlan;
import com.example.mealbuddy.models.Ingredient;
import com.example.mealbuddy.models.Plan;
import com.example.mealbuddy.models.Recipe;
import com.example.mealbuddy.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class MainActivity extends SingleFragmentActivity
        implements LoginFragment.LoginListener, MealPlannerFragment.PlannerListener,
        BrowseFragment.MealBrowserListener {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_item:
                FirebaseAuth.getInstance().signOut();
                Fragment fragment = new LoginFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void OnMealAdded(int id) {
        final List<Plan> plans = mUser.getPlans();
        String[] planTitles = new String[plans.size()];
        for (int i = 0; i < plans.size(); ++i) {
            planTitles[i] = plans.get(i).getTitle();
        }

        showSelectPlanDialog(plans);
    }

    private void showSelectPlanDialog(final List<Plan> plans) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a plan")
                .setAdapter(new ArrayAdapter<Plan>(this, R.layout.plan_add_meal, plans) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        Plan p = getItem(position);
                        View v = getLayoutInflater().inflate(R.layout.plan_add_meal, parent, false);
                        TextView t = (TextView) v.findViewById(R.id.plan_add_meal_text);
                        t.setText(p.getTitle() + " - " + p.getPlanPeriod());
                        return v;
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showSelectDayPlanDialog(plans.get(which));
                    }
                })
                .show();
    }

    private void showSelectDayPlanDialog(Plan plan) {
        final List<DayPlan> dayPlans = plan.getDayPlans();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a day")
                .setAdapter(new ArrayAdapter<DayPlan>(this, R.layout.plan_add_meal, dayPlans) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        DayPlan dp = getItem(position);
                        View v = getLayoutInflater().inflate(R.layout.plan_add_meal, parent, false);
                        TextView t = (TextView) v.findViewById(R.id.plan_add_meal_text);
                        t.setText(dp.getDayOfWeek() + ", " + new SimpleDateFormat("MMM dd, yyyy").format(dp.getDate()));
                        return v;
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Log.i(TAG, "Selected day");
                    }
                })
                .show();
    }
}
