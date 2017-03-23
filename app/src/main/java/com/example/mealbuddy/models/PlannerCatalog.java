package com.example.mealbuddy.models;

import android.content.Context;

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
    }

    public void addPlan(Plan plan){
        mPlans.add(plan);
    }

    public void removePlan(Plan plan){
        mPlans.remove(plan);
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
