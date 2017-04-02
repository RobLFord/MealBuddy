package com.example.mealbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Stores information about an ingredient for a recipe.
 */
public class Ingredient implements Parcelable {

    /**
     * The name of the ingredient.
     */
    private String mIngredientName;

    /**
     * The amount of this ingredient for the recipe.
     */
    private float mAmount;

    /**
     * The units for the amount.
     */
    private String mUnit;

    /**
     * Gets the name of the ingredient.
     * @return the ingredient name
     */
    public String getName() {
        return mIngredientName;
    }

    /**
     * Gets the ingredient amount
     * @return the ingredient amount
     */
    public float getAmount() {
        return mAmount;
    }

    /**
     * Gets the units for the ingredient amount
     * @return the ingredient units
     */
    public String getUnit() {
        return mUnit;
    }

    /**
     * Creates a new Ingredient object with the given parameters.
     * @param ingredientName the name of the ingredient
     * @param amount the amount of the ingredient
     * @param unit the units of the amount
     */
    public Ingredient(String ingredientName, float amount, String unit) {
        mIngredientName = ingredientName;
        mAmount = amount;
        mUnit = unit;
    }

    /**
     * Private constructor required for the Parcelable interface.
     * @param in Parcel to extract the Ingredient from
     */
    private Ingredient(Parcel in) {
        mIngredientName = in.readString();
        mAmount = in.readFloat();
        mUnit = in.readString();
    }

    /**
     * Creator object required for the Parcelable interface.
     */
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
