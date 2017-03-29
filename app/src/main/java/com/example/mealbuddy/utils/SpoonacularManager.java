package com.example.mealbuddy.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SpoonacularManager {

    private String mApiKey;

    private RequestQueue mRequestQueue;

    private static final String AUTOCOMPLETE_RECIPE_ENDPOINT =
            "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/autocomplete?number=%d&query=%s";

    private static final String GET_RECIPE_ENDPOINT =
            "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/%d/information?includeNutrition=false";

    private final Map<String, String> mHeaders = new HashMap<>();

    public SpoonacularManager(String apiKey, Context context) {
        mApiKey = apiKey;
        mRequestQueue = Volley.newRequestQueue(context);

        mHeaders.put("X-Mashape-Key", mApiKey);
        mHeaders.put("Accept", "application/json");
    }

    public Request<?> requestAutoCompleteRecipes(int number, String query,
             Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener) {

        JsonArrayRequest request = new JsonArrayRequest(
                buildAutoCompleteRecipeEndpoint(number, query), responseListener, errorListener) {
            public Map<String, String> getHeaders() {
                return mHeaders;
            }
        };
        mRequestQueue.add(request);

        return request;
    }

    public Request<?> requestRecipeInformation(int id,
            Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {

        JsonObjectRequest request = new JsonObjectRequest(buildGetRecipeEndpoint(id), null,
                responseListener, errorListener) {
            public Map<String, String> getHeaders() {
                return mHeaders;
            }
        };
        mRequestQueue.add(request);

        return request;
    }

    private String buildAutoCompleteRecipeEndpoint(int number, String query) {
        return String.format(AUTOCOMPLETE_RECIPE_ENDPOINT, number, query);
    }

    private String buildGetRecipeEndpoint(int number) {
        return String.format(GET_RECIPE_ENDPOINT, number);
    }
}
