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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.mealbuddy.models.DayPlan;
import com.example.mealbuddy.models.Ingredient;
import com.example.mealbuddy.models.Plan;
import com.example.mealbuddy.models.Recipe;
import com.example.mealbuddy.models.User;
import com.example.mealbuddy.utils.SpoonacularManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Class : MainActivity
 *
 * Description :
 *
 * This Activity responsible for passing data between the fragments and the Classes located in
 * models. Also this Activity is responsible for calling the fragment. This Activity is called
 * when the MealBuddy App is launched.
 *
 */
public class MainActivity extends SingleFragmentActivity
        implements LoginFragment.LoginListener, MealPlannerFragment.PlannerListener,
        BrowseFragment.MealBrowserListener {

    /**
     * String used for identification during logging.
     */
    private static final String TAG = "MainActivity";

    /**
     * The user that is currently logged in to the app
     */
    private User mUser;

    /**
     * Creates the initial fragment that is presented when the app starts.
     * @return the fragment to display
     */
    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

    /**
     * Function callback when the user logs in.
     * @param uid UID for the user that is logged in.
     */
    @Override
    public void OnUserLogin(final String uid) {
        Log.d(TAG, "User login: " + uid);

        // Look up the user information from the database
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users/" + uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Read the user information and load the MainFragment for the current user.
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

    /**
     * Reads the user information from the database.
     * @param uid the uid for the User to read information for
     * @param dataSnapshot the node in the database for user data
     * @return User object containing the data read from the database
     */
    private User readUserFromFirebaseDatabase(String uid, DataSnapshot dataSnapshot) {
        User user = new User(uid);

        // Read user's meal plans
        for (DataSnapshot planSnapshot : dataSnapshot.child("mealPlans").getChildren()) {
            user.addPlan(readPlanFromFirebaseDatabase(planSnapshot));
        }

        return user;
    }

    /**
     * Reads the user's plan information from the database.
     * @param planSnapshot node in the database for the user's plan information
     * @return Plan object containing data for a single meal plan
     */
    private Plan readPlanFromFirebaseDatabase(DataSnapshot planSnapshot) {

        // Create a new plan with data from the database
        long durationDays = (long) planSnapshot.child("duration").getValue();
        Plan.Duration duration = durationDays == 14 ? Plan.Duration.TWO_WEEKS : Plan.Duration.ONE_WEEK;
        String startDate = (String) planSnapshot.child("startDate").getValue();
        String planTitle = (String) planSnapshot.child("title").getValue();

        Plan plan = new Plan(startDate, duration);
        plan.setTitle(planTitle);

        // Read each day for the plan from the database
        for (DataSnapshot daySnapshot : planSnapshot.child("days").getChildren()) {
            addDayPlanToPlan(plan, daySnapshot);
        }

        return plan;
    }

    /**
     * Add a plan to the meal plan.
     * @param plan the plan to add
     * @param daySnapshot node in the database for the plan
     */
    private void addDayPlanToPlan(Plan plan, DataSnapshot daySnapshot) {

        // Create a day object to store the data
        long day = (Long) daySnapshot.child("day").getValue();
        GregorianCalendar planDate = new GregorianCalendar();
        planDate.setTime(plan.getStartDate());
        planDate.add(Calendar.DAY_OF_MONTH, (int) day - 1);

        DayPlan dayPlan = new DayPlan(planDate.getTime());

        // Iterate through the meals for the day
        for (DataSnapshot mealSnapshot : daySnapshot.child("meals").getChildren()) {
            String mealName = (String) mealSnapshot.child("name").getValue();
            long servings = (Long) mealSnapshot.child("servings").getValue();
            Vector<Ingredient> ingredients = new Vector<>();

            // Iterate through the meal ingredients
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

    /**
     * API Callback to create the main menu that includes the logout button.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * API Callback for when the logout button is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_item:
                // Log the user out and load the LoginFragment
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

    /**
     * Callback when a new plan is added by the user.
     * @param newPlan the plan added by the user
     */
    @Override
    public void OnAddPlan(Plan newPlan) {
        // Put the plan in a Map and add it to the user's plans in the database
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users/" + mUser.getUid() + "/mealPlans");
        HashMap<String, Object> planValues = new HashMap<>();
        planValues.put("duration", newPlan.getDuration().days());
        planValues.put("startDate", newPlan.getStartDateString());
        planValues.put("title",  newPlan.getTitle());
        ref.push().setValue(planValues);
    }

    /**
     * Callback when a new meal is added by the user.
     * @param id the ID of the meal added by the user
     */
    @Override
    public void OnMealAdded(int id) {
        // User the Spoonacular API to get the meal information
        SpoonacularManager manager = new SpoonacularManager(getString(R.string.spoonacular_key), this);
        manager.requestRecipeInformation(id,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            // Extract the meal information from the API response
                            String title = response.getString("title");
                            Recipe recipe = new Recipe(title, 0);

                            JSONArray ingredients = response.getJSONArray("extendedIngredients");
                            for (int i = 0; i < ingredients.length(); ++i) {
                                JSONObject ingredient = ingredients.getJSONObject(i);
                                String name = ingredient.getString("name");
                                float amount = (float) ingredient.getDouble("amount");
                                String unit = ingredient.getString("unit");

                                recipe.addIngredient(new Ingredient(name, amount, unit));
                            }

                            // Show the plan selection dialog to the user
                            showSelectPlanDialog(mUser.getPlans(), recipe);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Request recipe information error: " + error.toString());
                    }
                });
    }

    /**
     * Presents the plan selection dialog to the user.
     * @param plans list of plans to include in the dialog
     * @param recipe the recipe to be added
     */
    private void showSelectPlanDialog(final List<Plan> plans, final Recipe recipe) {

        // Build a dialog that displays a list of plans to the user
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
                        // After the user selects a plan, display the list of days within the plan
                        // to select
                        dialog.dismiss();
                        showSelectDayPlanDialog(plans.get(which), recipe);
                    }
                })
                .show();
    }

    /**
     * Presents the list of days to the user.
     * @param plan list of days within the plan to display
     * @param recipe the recipe to be added
     */
    private void showSelectDayPlanDialog(final Plan plan, final Recipe recipe) {

        // Build a dialog that displays the list of days to the user
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
                    public void onClick(DialogInterface dialog, final int which) {
                        // Add the given recipe to the selected day plan
                        dialog.dismiss();
                        dayPlans.get(which).addRecipe(recipe);
                        addRecipeToDatabase(plan, which, recipe);

                    }
                })
                .show();
    }

    /**
     * Adds the given recipe to the user's plan in the database
     * @param plan the plan to add the recipe to
     * @param which the day selected to add the recipe to
     * @param recipe the recipe to add to the user's plan
     */
    private void addRecipeToDatabase(final Plan plan, final int which, final Recipe recipe) {

        // Get the reference to the user's plans within the database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users/" + mUser.getUid() + "/mealPlans").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Iterate through the user's meal plans to find the correct plan to add meal to
                        for (DataSnapshot planSnapshot : dataSnapshot.getChildren()) {
                            String start_date = (String) planSnapshot.child("startDate").getValue();
                            DataSnapshot daysSnapshot = null;
                            if (start_date.equals(plan.getStartDateString())) {
                                daysSnapshot = planSnapshot.child("days");
                            }

                            // Create new recipe to add
                            Map<String, Object> newMeal = new HashMap<>();
                            newMeal.put("name", recipe.getName());
                            newMeal.put("servings", recipe.getServings());

                            // Extract the ingredients
                            List<Map<String, Object>> ingredientsList = new Vector<>();
                            for (Ingredient ingredient : recipe.getIngredients()) {
                                Map<String, Object> ingredientMap = new HashMap<>();
                                ingredientMap.put("amount", ingredient.getAmount());
                                ingredientMap.put("name", ingredient.getName());
                                ingredientMap.put("unit", ingredient.getUnit());
                                ingredientsList.add(ingredientMap);
                            }

                            newMeal.put("ingredients", ingredientsList);

                            // Get the list of days from the database for the given plan. If there
                            // are no days for the current plan, use an empty vector instead.
                            List<Map<String, Object>> days;
                            if (daysSnapshot == null) {
                                List<Map<String, Object>> meals = new Vector<>();
                                meals.add(newMeal);

                                days = new Vector<>();
                                Map<String, Object> day = new HashMap<>();
                                day.put("day", which + 1);
                                day.put("meals", meals);

                                days.add(day);
                            } else {
                                days = daysSnapshot.getValue(new GenericTypeIndicator<List<Map<String, Object>>>() {
                                });


                                for (Map<String, Object> day : days) {
                                    Log.i(TAG, "Found day");
                                    if (day != null && (Long) day.get("day") == (which + 1)) {
                                        List<Map<String, Object>> mealsList = (List<Map<String, Object>>) day.get("meals");

                                        mealsList.add(newMeal);
                                    }
                                }
                            }

                            planSnapshot.getRef().child("days").setValue(days);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }
}
