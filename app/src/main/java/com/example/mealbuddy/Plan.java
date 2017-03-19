package com.example.mealbuddy;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Rob Ford on 3/18/2017.
 */

public class Plan {
    private UUID mId;
    private String mTitle;
    private Date mStartDate;
    private Date mEndDate;

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setEndDate(Date endDate) {
        mEndDate = endDate;
    }

    public Plan() {
        mId = UUID.randomUUID(); //Using a random ID for now
        mStartDate = new Date();
        mEndDate = new Date();
    }
}
