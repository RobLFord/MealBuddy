package com.example.mealbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class Recipe implements Parcelable {

    private String mName;
    private int mServings;
    private Vector<Ingredient> mIngredients = new Vector<>();

    public void addIngredient(Ingredient ingredient) {
        mIngredients.add(ingredient);
    }

    public void addIngredients(Collection<Ingredient> ingredients) {
        mIngredients.addAll(ingredients);
    }

    public Collection<Ingredient> getIngredients() {
        return new Vector<Ingredient>(mIngredients);
    }

    public String getName() {
        return mName;
    }

    public Recipe(String name, int servings) {
        mName = name;
        mServings = servings;
    }

    public Recipe(String name, int servings, Collection<Ingredient> ingredients) {
        this(name, servings);
        mIngredients.addAll(ingredients);
    }

    private Recipe(Parcel in) {
        mName = in.readString();
        mServings = in.readInt();
        in.readTypedList(mIngredients, Ingredient.CREATOR);
    }

    public static final Parcelable.Creator<Recipe> CREATOR
            = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeInt(mServings);
        dest.writeTypedList(mIngredients);
    }
}
