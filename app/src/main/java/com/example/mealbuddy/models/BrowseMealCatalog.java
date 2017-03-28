package com.example.mealbuddy.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Rob Ford on 3/27/2017.
 */

public class BrowseMealCatalog {
    private static BrowseMealCatalog sBrowseMealCatalog;

    private List<BrowserMeal> mBrowserMeals;

    public static BrowseMealCatalog get(Context context){
        if (sBrowseMealCatalog == null) {
            sBrowseMealCatalog = new BrowseMealCatalog(context);
        }
        return  sBrowseMealCatalog;
    }

    private BrowseMealCatalog(Context context){
        mBrowserMeals = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BrowserMeal browserMeal = new BrowserMeal();
            browserMeal.setTitle("Browser Meal #" + i);
            mBrowserMeals.add(browserMeal);
        }
    }

    public List<BrowserMeal> getBrowserMeals() {
        return mBrowserMeals;
    }

    public BrowserMeal getBrowserMeal(UUID id) {
        for (BrowserMeal browserMeal : mBrowserMeals) {
            if (browserMeal.getId().equals(id)) {
                return browserMeal;
            }
        }
        return null;
    }
}
