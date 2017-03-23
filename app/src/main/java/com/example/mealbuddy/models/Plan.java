package com.example.mealbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

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

    private void updateEndDate() {
        // Reset the end date based on the duration value
        mEndDate.setTime(mStartDate.getTime());
        mEndDate.add(GregorianCalendar.DAY_OF_MONTH, mDuration.days());
    }

    public Plan() {
        mId = UUID.randomUUID(); //Using a random ID for now
    }

    /*
    public Plan(String startDate, Duration duration) {
        mDuration = duration;

        try {
            mStartDate.setTime(PARCEL_DATE_FORMAT.parse(startDate));
        } catch (ParseException e) {
            mStartDate.set(1970, 1, 1);
        }

        updateEndDate();
    }*/

    private Plan(Parcel in) {
        String startDate = in.readString();
        int duration = in.readInt();

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
