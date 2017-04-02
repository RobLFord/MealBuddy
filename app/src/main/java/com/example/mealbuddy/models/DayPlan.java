package com.example.mealbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Created by Rob Ford on 3/18/2017.
 */

/**
 * A DayPlan object represent an individual day within a meal plan.
 */
public class DayPlan implements Parcelable {
    /**
     * A date format for representing the date of the plan day.
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    /**
     * The date of this particular day.
     */
    private Date mDate;

    /**
     * The day of the month.
     */
    private int mDayOfMonth;

    /**
     * The day of the week.
     */
    private int mDayOfWeek;

    /**
     * The list of recipes for this current day.
     */
    private Vector<Recipe> mRecipes = new Vector<>();

    /**
     * Add a recipe to the day.
     * @param recipe the recipe to add
     */
    public void addRecipe(Recipe recipe) {
        mRecipes.add(recipe);
    }

    /**
     * Inserts a new recipe into the list.
     * @param recipe the recipe to add
     * @param index the location to insert the recipe to
     */
    public void insertRecipe(Recipe recipe, int index) {
        mRecipes.add(index, recipe);
    }

    /**
     * Replace the recipes for this day with a given list.
     * @param recipes the new recipes to replace this day with
     */
    public void replaceRecipes(List<Recipe> recipes) {
        mRecipes.clear();
        mRecipes.addAll(recipes);
    }

    /**
     * Get the list of recipes for the day.
     * @return the list of recipes
     */
    public List<Recipe> getRecipes() {
        return mRecipes;
    }

    /**
     * Get the date for this day plan.
     * @return the date
     */
    public Date getDate() {
        return mDate;
    }

    /**
     * Gets the day of the month of this plan.
     * @return the day of the month
     */
    public int getDayOfMonth() {
        return mDayOfMonth;
    }

    /**
     * Gets the day of the week for this day.
     * @return the day of the week
     */
    public String getDayOfWeek() {
        String day = "";

        switch (mDayOfWeek) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
        }
        return day;
    }

    /**
     * Creates a new DayPlan for the given date.
     * @param date the date represented by this day
     */
    public DayPlan(Date date) {
        mDate = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        mDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Private constructor required for Parcelable interface
     * @param in parcel to extract DayPlan from
     */
    private DayPlan(Parcel in) {
        String date_string = in.readString();
        in.readTypedList(mRecipes, Recipe.CREATOR);

        Calendar c = Calendar.getInstance();

        try {
            c.setTime(DATE_FORMAT.parse(date_string));
        } catch (ParseException e) {
            c.set(1970, 1, 1);
        }

        mDate = c.getTime();
    }

    /**
     * Creator object required for the Parcelable interface.
     */
    public static final Parcelable.Creator<DayPlan> CREATOR
            = new Parcelable.Creator<DayPlan>() {
        @Override
        public DayPlan createFromParcel(Parcel source) {
            return new DayPlan(source);
        }

        @Override
        public DayPlan[] newArray(int size) {
            return new DayPlan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(DATE_FORMAT.format(mDate));
        dest.writeTypedList(mRecipes);
    }
}
