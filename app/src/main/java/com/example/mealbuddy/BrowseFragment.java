package com.example.mealbuddy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.mealbuddy.utils.SpoonacularManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Vector;

/**
 * Class : BrowseFragment
 *
 * Description :
 *
 * This fragment displays the visual representation for browsing meals.
 * The purpose of this fragment is to allow the user to search for meals using the
 * spoonacular API. This fragment is called when the user selects the Browse button on the
 * bottom navigation bar.
 *
 */

public class BrowseFragment extends Fragment implements SearchView.OnQueryTextListener {
    /**
     * String for debug logging
     */
    private static final String TAG = "BrowseFragment";

    /**
     * RecyclerView members for displaying the list of meals to the user
     */
    private RecyclerView mBrowserRecyclerView;
    private BrowserListAdapter mBrowserListAdapter;

    private static final String ARG_TEXT = "arg_text";

    private String mText;
    private TextView mTextView;
    private SearchView mSearchView;

    private MealBrowserListener mListener;

    /**
     * Creates a new instance of the fragment.
     * @param text label for the fragment
     * @return a new instance of the fragment
     */
    public static Fragment newInstance(String text) {
        Fragment frag = new BrowseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Adapter for storing list of meals returned by the Spoonacular API.
     */
    private class BrowserListAdapter extends RecyclerView.Adapter<BrowserListHolder> {
        private List<SpoonacularMeal> mBrowserMeals;

        public BrowserListAdapter(List<SpoonacularMeal> browserMeals) {
            mBrowserMeals = browserMeals;
        }

        @Override
        public BrowserListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new BrowserListHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(BrowserListHolder holder, int position) {
            SpoonacularMeal browserMeal = mBrowserMeals.get(position);
            holder.bind(browserMeal);
        }

        @Override
        public int getItemCount() {
            return mBrowserMeals.size();
        }

    }

    /**
     * Holder to populate a card within the RecyclerView
     */
    private class BrowserListHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private TextView mTitleTextView;

        private SpoonacularMeal mBrowserMeal;

        public void bind(SpoonacularMeal browserMeal) {
            mBrowserMeal = browserMeal;
            mTitleTextView.setText(mBrowserMeal.getTitle());
        }

        public BrowserListHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_browse_item, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.browser_meal_title);
        }

        @Override
        public void onClick(View view){
            mListener.OnMealAdded(mBrowserMeal.getId());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse, container, false);

        mBrowserRecyclerView = (RecyclerView) view.findViewById(R.id.browse_recycler_view);
        mBrowserRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSearchView = (SearchView) view.findViewById(R.id.browser_searchView);
        mSearchView.setOnQueryTextListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // retrieve text and color from bundle or savedInstanceState
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            mText = args.getString(ARG_TEXT);
        } else {
            mText = savedInstanceState.getString(ARG_TEXT);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_TEXT, mText);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (MealBrowserListener) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException(context.toString() +
                    " must implement MealBrowserListener");
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        SpoonacularManager manager = new SpoonacularManager(getString(R.string.spoonacular_key), getContext());
        manager.requestAutoCompleteRecipes(10, query,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Search the API using the user provided string
                        List<SpoonacularMeal> search_results = new Vector<>();

                        try {
                            int recipe_count = response.length();
                            for (int i = 0; i < recipe_count; ++i) {
                                JSONObject recipe_object = response.getJSONObject(i);
                                search_results.add(new SpoonacularMeal(recipe_object.getInt("id"),
                                        recipe_object.getString("title")));
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing search reasults");
                            e.printStackTrace();
                        }

                        mBrowserListAdapter = new BrowserListAdapter(search_results);
                        mBrowserRecyclerView.setAdapter(mBrowserListAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(TAG, "Query request cancelled");
                    }
                });
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /**
     * Inner class for storing temporary data from the search query
     */
    class SpoonacularMeal {
        private int mId;
        private String mTitle;

        public String getTitle() { return mTitle; }
        public int getId() { return mId; }

        public SpoonacularMeal(int id, String title) {
            mId = id;
            mTitle = title;
        }
    }

    /**
     * Interface for callback when the user selects a meal from the search results.
     */
    public interface MealBrowserListener {
        void OnMealAdded(int id);
    }
}
