package com.example.mealbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredient implements Parcelable {

    private String mIngredientName;
    private float mAmount;
    private String mUnit;

    public String getName() {
        return mIngredientName;
    }

    public float getAmount() {
        return mAmount;
    }

    public String getUnit() {
        return mUnit;
    }

    public Ingredient(String ingredientName, float amount, String unit) {
        mIngredientName = ingredientName;
        mAmount = amount;
        mUnit = unit;
    }

    private Ingredient(Parcel in) {
        mIngredientName = in.readString();
        mAmount = in.readFloat();
        mUnit = in.readString();
    }

    public static final Parcelable.Creator<Ingredient> CREATOR
            = new Parcelable.Creator<Ingredient>() {

        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mIngredientName);
        dest.writeFloat(mAmount);
        dest.writeString(mUnit);
    }
}
