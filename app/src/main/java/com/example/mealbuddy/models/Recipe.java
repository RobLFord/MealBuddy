package com.example.mealbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * The Recipe class stores information about a recipe for a user's plan.
 */
public class Recipe implements Parcelable {

    /**
     * The name of the recipe.
     */
    private String mName;

    /**
     * The number of servings the recipe makes.
     */
    private int mServings;

    /**
     * The list of ingredients for the recipe.
     */
    private Vector<Ingredient> mIngredients = new Vector<>();

    /**
     * Add an ingredient to the recipe.
     * @param ingredient the ingredient to add
     */
    public void addIngredient(Ingredient ingredient) {
        mIngredients.add(ingredient);
    }

    /**
     * Adds ingredients to the recipe.
     * @param ingredients the collection of recipes to add
     */
    public void addIngredients(Collection<Ingredient> ingredients) {
        mIngredients.addAll(ingredients);
    }

    /**
     * Get the ingredients for the recipe.
     * @return the ingredients for the recipe
     */
    public Collection<Ingredient> getIngredients() {
        return new Vector<Ingredient>(mIngredients);
    }

    /**
     * Get the name of the recipe.
     * @return the name
     */
    public String getName() {
        return mName;
    }

    /**
     * Get the number of servings.
     * @return the number of servings
     */
    public int getServings() { return mServings; }

    /**
     * Creates a new Recipe with the given name and number of servings.
     * @param name the name of the recipe
     * @param servings the number of servings for the recipe
     */
    public Recipe(String name, int servings) {
        mName = name;
        mServings = servings;
    }

    /**
     * Creates a new Recipe with the given name, number of servings and list ingredients.
     * @param name the name of the recipe
     * @param servings the number of servings for the recipe
     * @param ingredients the list of ingredients
     */
    public Recipe(String name, int servings, Collection<Ingredient> ingredients) {
        this(name, servings);
        mIngredients.addAll(ingredients);
    }

    /**
     * Constructor required for the Parcelable interface
     * @param in the parcel to extract the Recipe from
     */
    private Recipe(Parcel in) {
        mName = in.readString();
        mServings = in.readInt();
        in.readTypedList(mIngredients, Ingredient.CREATOR);
    }

    /**
     * Creator object required for the Parcelable interface.
     */
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
