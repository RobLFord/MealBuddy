package com.example.mealbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by Rob Ford on 3/18/2017.
 */

/**
 * The Plan class stores and coordinates information about multiple days of recipes.
 */
public class Plan implements Parcelable {
    /**
     * Date format for storing dates in the class when Parcelized.
     */
    public static final String PARCEL_FORMAT_STRING = "yyyy-MM-dd";

    /**
     * Date format for presenting in a more user friendly format
     */
    public static final String DISPLAY_FORMAT_STRING = "MMMM d, yyyy";

    /**
     * Date formatters
     */
    private static final SimpleDateFormat PARCEL_DATE_FORMAT
            = new SimpleDateFormat(PARCEL_FORMAT_STRING);
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT
            = new SimpleDateFormat(DISPLAY_FORMAT_STRING);

    /**
     * UID for the Plan
     */
    private UUID mId;

    /**
     * User entered title for the meal plan.
     */
    private String mTitle;

    /**
     * The starting date for the plan.
     */
    private GregorianCalendar mStartDate = new GregorianCalendar();

    /**
     * The ending date for the plan.
     */
    private GregorianCalendar mEndDate = new GregorianCalendar();

    /**
     * The length of time the plan lasts for.
     */
    private Duration mDuration = Duration.ONE_WEEK;

    /**
     * The days within the plan.
     */
    private ArrayList<DayPlan> mDayPlans = new ArrayList<>();

    /**
     * Get the plan UID.
     * @return the plan UID
     */
    public UUID getId() {
        return mId;
    }

    /**
     * Get the plan title.
     * @return the plan title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Get a string representation of the plan period.
     * @return plan period string
     */
    public String getPlanPeriod(){
        return DISPLAY_DATE_FORMAT.format(mStartDate.getTime()) + " - "
                + DISPLAY_DATE_FORMAT.format(mEndDate.getTime());
    }

    /**
     * Sets the title of the plan.
     * @param title the new plan title
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Gets the start date of the plan.
     * @return the start date of the plan
     */
    public Date getStartDate() {
        return mStartDate.getTime();
    }

    /**
     * Gets a string representing the start date of the plan.
     * @return the start date string
     */
    public String getStartDateString() {
        return PARCEL_DATE_FORMAT.format(mStartDate.getTime());
    }

    /**
     * Sets the start date of the plan.
     * @param startDate the start date
     */
    public void setStartDate(Date startDate) {
        mStartDate.setTime(startDate);
        updateEndDate();
    }

    /**
     * Gets the end date of the plan.
     * @return the end date
     */
    public Date getEndDate() {
        return mEndDate.getTime();
    }

    /**
     * Gets the a string representing the end date of the plan.
     * @return the end date string
     */
    public String getEndDateString() {
        return PARCEL_DATE_FORMAT.format(mEndDate.getTime());
    }

    /**
     * Gets the duration of the plan
     * @return the plan duration
     */
    public Duration getDuration() {
        return mDuration;
    }

    /**
     * Sets the duration of the plan and adjusts the end date accordingly.
     * @param duration the new duration of the plan
     */
    public void setDuration(int duration) {
        if (duration == 7){
            mDuration = Duration.ONE_WEEK;
        } else {
            mDuration = Duration.TWO_WEEKS;
        }
        updateEndDate();
    }

    /**
     * Adds a new day to the plan.
     * @param dayPlan the day to add
     * @param index the index to insert the plan at
     */
    public void addDayPlan(DayPlan dayPlan, int index) {
        DayPlan temp = mDayPlans.get(index);
        temp.replaceRecipes(dayPlan.getRecipes());
    }

    /**
     * Gets a copy of the days for the plan.
     * @return the days of the plan
     */
    public List<DayPlan> getDayPlans() {
        // Create a temporary list to return and copy the plans into it
        Vector<DayPlan> plans = new Vector<>(mDuration.days());

        for (DayPlan plan : mDayPlans) {
            plans.add(plan);
        }

        return plans;
    }

    /**
     * Gets the total number of meals contained within all the days of the plan.
     * @return the number of meals
     */
    public int getMealCount() {
        int count = 0;

        // Iterate through each day to collect the total number of meals
        for (DayPlan dayPlan : mDayPlans) {
            for (Recipe recipe : dayPlan.getRecipes()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Summarize the ingredients for all the meals within the entire plan.
     * @return a mapping of ingredients amounts for all the meals within the plan. The units of the
     * ingredients are stored as a pair representing the (ingredient name, units)
     */
    public Map<Pair<String, String>, Float> summarize() {
        HashMap<Pair<String, String>, Float> ingredients = new HashMap<>();

        // Iterate through the recipes within each day.
        for (DayPlan dayPlan : mDayPlans) {
            for (Recipe recipe : dayPlan.getRecipes()) {
                for (Ingredient ingredient : recipe.getIngredients()) {
                    // Create a label to store the ingredient name and unit
                    Pair<String, String> ingredientLabel = new Pair(ingredient.getName(), ingredient.getUnit());

                    float tempAmount = ingredients.containsKey(ingredientLabel) ? ingredients.get(ingredientLabel) : 0.0f;

                    tempAmount += ingredient.getAmount();
                    ingredients.put(ingredientLabel, tempAmount);
                }
            }
        }

        return ingredients;
    }

    /**
     * Update the end date of the plan based on the value of the duration.
     */
    private void updateEndDate() {
        // Reset the end date based on the duration value
        mEndDate.setTime(mStartDate.getTime());
        mEndDate.add(GregorianCalendar.DAY_OF_MONTH, mDuration.days());
    }

    /**
     * Create a new plan that starts on the given date and for the given duration.
     * @param date the date the plan starts on
     * @param duration the duration of the plan
     */
    public Plan(Date date, Duration duration) {
        this(PARCEL_DATE_FORMAT.format(date), duration);
    }

    /**
     * Create a new plan that starts on the given day and for the given duration.
     * @param startDate the date the plan starts
     * @param duration the duration of the plan
     */
    public Plan(String startDate, Duration duration) {
        mId = UUID.randomUUID(); //Using a random ID for now

        try {
            mStartDate.setTime(PARCEL_DATE_FORMAT.parse(startDate));
        } catch (ParseException e) {
            mStartDate.set(1970, 1, 1);
        }

        mDuration = duration;

        // Create the individual days of the plan
        for (int i = 0; i < duration.days(); ++i) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(mStartDate.getTime());
            c.add(Calendar.DAY_OF_MONTH, i);
            mDayPlans.add(new DayPlan(c.getTime()));
        }

        updateEndDate();
    }

    /**
     * Private constructor required for Parcelable interface.
     * @param in Parcel to extract object from
     */
    private Plan(Parcel in) {
        String startDate = in.readString();
        int duration = in.readInt();

        in.readTypedList(mDayPlans, DayPlan.CREATOR);

        try {
            mStartDate.setTime(PARCEL_DATE_FORMAT.parse(startDate));
        } catch (ParseException e) {
            mStartDate.set(1970, 1, 1);
        }

        switch (duration) {
            case 14:
                mDuration = Duration.TWO_WEEKS;
                break;
            default:
                mDuration = Duration.ONE_WEEK;
                break;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getStartDateString());
        dest.writeInt(mDuration.days());
        dest.writeTypedList(mDayPlans);
    }

    /**
     * Creator object required for the Parcelable interface.
     */
    public static final Parcelable.Creator<Plan> CREATOR =
            new Parcelable.Creator<Plan>() {
                @Override
                public Plan createFromParcel(Parcel source) {
                    return new Plan(source);
                }

                @Override
                public Plan[] newArray(int size) {
                    return new Plan[size];
                }
    };

    /**
     * Enumeration for the number of days of duration supported by the application.
     */
    public enum Duration {
        ONE_WEEK(7),
        TWO_WEEKS(14);

        private int mDays;

        Duration(int days) {
            mDays = days;
        }

        public int days() {
            return mDays;
        }
    }
}
