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

public class DayPlan implements Parcelable {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    private Date mDate;
    private int mDayOfMonth;
    private int mDayOfWeek;
    private Vector<Recipe> mRecipes = new Vector<>();

    public void addRecipe(Recipe recipe) {
        mRecipes.add(recipe);
    }

    public void insertRecipe(Recipe recipe, int index) {
        mRecipes.add(index, recipe);
    }

    public void replaceRecipes(List<Recipe> recipes) {
        mRecipes.clear();
        mRecipes.addAll(recipes);
    }

    public List<Recipe> getRecipes() {
        return mRecipes;
    }

    public Date getDate() {
        return mDate;
    }

    public int getDayOfMonth() {
        return mDayOfMonth;
    }

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

    public DayPlan(Date date) {
        mDate = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        mDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    }

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
