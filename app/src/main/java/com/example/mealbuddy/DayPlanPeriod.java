package com.example.mealbuddy;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Rob Ford on 3/18/2017.
 */

public class DayPlanPeriod {
    private static final String TAG = "DayPlanPeriod";

    private static DayPlanPeriod sDayPlanPeriod;

    private List<DayPlan> mDayPlans;

    public static DayPlanPeriod get(Context context) {
        if (sDayPlanPeriod == null) {
            sDayPlanPeriod = new DayPlanPeriod(context);
        }
        return sDayPlanPeriod;
    }

    private DayPlanPeriod(Context context){
        mDayPlans = new ArrayList<>();
        Date planDate = new Date();
        for (int i = 1; i < 15; i++) {
            DayPlan dayPlan = new DayPlan(planDate);
            mDayPlans.add(dayPlan);
            planDate = addDay(planDate);
        }
    }

    public List<DayPlan> getDayPlans() {
        return mDayPlans;
    }

    private static Date addDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }
}
