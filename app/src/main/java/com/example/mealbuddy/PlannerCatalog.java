package com.example.mealbuddy;

import android.content.Context;

import com.example.mealbuddy.models.Plan;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Rob Ford on 3/18/2017.
 */

public class PlannerCatalog {
    private static PlannerCatalog sPlannerCatalog;

    private List<Plan> mPlans;

    public static PlannerCatalog get(Context context){
        if (sPlannerCatalog == null) {
            sPlannerCatalog = new PlannerCatalog(context);
        }
        return  sPlannerCatalog;
    }

    private PlannerCatalog(Context context){
        mPlans = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Plan plan = new Plan();
            plan.setTitle("Plan #" + i);
            mPlans.add(plan);
        }
    }

    public List<Plan> getPlans() {
        return mPlans;
    }

    public Plan getPlan(UUID id) {
        for (Plan plan : mPlans) {
            if (plan.getId().equals(id)) {
                return plan;
            }
        }
        return null;
    }
}
