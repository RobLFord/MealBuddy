package com.example.mealbuddy.models;

import java.util.UUID;

/**
 * Created by Rob Ford on 3/27/2017.
 */

public class BrowserMeal {
    private UUID mId;
    private String mTitle;

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public BrowserMeal() {
        mId = UUID.randomUUID();
    }
}
