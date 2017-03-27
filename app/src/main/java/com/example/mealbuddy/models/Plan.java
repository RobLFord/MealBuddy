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

public class Plan implements Parcelable {
    public static final String PARCEL_FORMAT_STRING = "yyyy-MM-dd";
    public static final String DISPLAY_FORMAT_STRING = "MMMM d, yyyy";

    private static final SimpleDateFormat PARCEL_DATE_FORMAT
            = new SimpleDateFormat(PARCEL_FORMAT_STRING);
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT
            = new SimpleDateFormat(DISPLAY_FORMAT_STRING);

    private UUID mId;
    private String mTitle;
    private GregorianCalendar mStartDate = new GregorianCalendar();
    private GregorianCalendar mEndDate = new GregorianCalendar();
    private Duration mDuration = Duration.ONE_WEEK;
    private ArrayList<DayPlan> mDayPlans;

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPlanPeriod(){
        return DISPLAY_DATE_FORMAT.format(mStartDate.getTime()) + " - "
                + DISPLAY_DATE_FORMAT.format(mEndDate.getTime());
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getStartDate() {
        return mStartDate.getTime();
    }

    public String getStartDateString() {
        return PARCEL_DATE_FORMAT.format(mStartDate.getTime());
    }

    public void setStartDate(Date startDate) {
        mStartDate.setTime(startDate);
        updateEndDate();
    }

    public Date getEndDate() {
        return mEndDate.getTime();
    }

    public String getEndDateString() {
        return PARCEL_DATE_FORMAT.format(mEndDate.getTime());
    }

    public Duration getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        if (duration == 7){
            mDuration = Duration.ONE_WEEK;
        } else {
            mDuration = Duration.TWO_WEEKS;
        }
        updateEndDate();
    }

    public void addDayPlan(DayPlan dayPlan, int index) {
        DayPlan temp = mDayPlans.get(index);
        temp.replaceRecipes(dayPlan.getRecipes());
    }

    public List<DayPlan> getDayPlans() {
        Vector<DayPlan> plans = new Vector<>(mDuration.days());

        for (DayPlan plan : mDayPlans) {
            plans.add(plan);
        }

        return plans;
    }

    public int getMealCount() {
        int count = 0;

        for (DayPlan dayPlan : mDayPlans) {
            for (Recipe recipe : dayPlan.getRecipes()) {
                count++;
            }
        }
        return count;
    }

    public Map<Pair<String, String>, Float> summarize() {
        HashMap<Pair<String, String>, Float> ingredients = new HashMap<>();

        for (DayPlan dayPlan : mDayPlans) {
            for (Recipe recipe : dayPlan.getRecipes()) {
                for (Ingredient ingredient : recipe.getIngredients()) {
                    Pair<String, String> ingredientLabel = new Pair(ingredient.getName(), ingredient.getUnit());

                    float tempAmount = ingredients.containsKey(ingredientLabel) ? ingredients.get(ingredientLabel) : 0.0f;

                    tempAmount += ingredient.getAmount();
                    ingredients.put(ingredientLabel, tempAmount);
                }
            }
        }

        return ingredients;
    }

    private void updateEndDate() {
        // Reset the end date based on the duration value
        mEndDate.setTime(mStartDate.getTime());
        mEndDate.add(GregorianCalendar.DAY_OF_MONTH, mDuration.days());
    }

    public Plan() {
        mId = UUID.randomUUID(); //Using a random ID for now
        mDayPlans = new ArrayList<>();
    }

    public Plan(String startDate, Duration duration) {
        this();

        try {
            mStartDate.setTime(PARCEL_DATE_FORMAT.parse(startDate));
        } catch (ParseException e) {
            mStartDate.set(1970, 1, 1);
        }

        mDuration = duration;
        for (int i = 0; i < duration.days(); ++i) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(mStartDate.getTime());
            c.add(Calendar.DAY_OF_MONTH, i);
            mDayPlans.add(new DayPlan(c.getTime()));
        }

        updateEndDate();
    }

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
