package com.example.mealbuddy;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rob Ford on 3/18/2017.
 */

public class DayPlan {
    private Date mDate;
    private int mDayOfMonth;
    private int mDayOfWeek;

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
}
