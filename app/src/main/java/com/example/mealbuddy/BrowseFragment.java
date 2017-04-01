package com.example.mealbuddy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealbuddy.models.BrowseMealCatalog;
import com.example.mealbuddy.models.BrowserMeal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Rob Ford on 3/9/2017.
 */

public class BrowseFragment extends Fragment {
    private RecyclerView mBrowserRecyclerView;
    private BrowserListAdapter mBrowserListAdapter;

    private static final String ARG_TEXT = "arg_text";

    private String mText;
    private TextView mTextView;

    private MealBrowserListener mListener;

    public static Fragment newInstance(String text) {
        Fragment frag = new BrowseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        frag.setArguments(args);
        return frag;
    }

    private class BrowserListAdapter extends RecyclerView.Adapter<BrowserListHolder> {
        private List<SpoonacularMeal> mBrowserMeals;
        private Map<Integer, String> mMealList;

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
            //Comment stuff will get used when SelectedBrowserMealFragment is updated
            //updateToolbarText(mBrowserMeal.getTitle());

            //Fragment frag = SelectedBrowserMealFragment.newInstance(mBrowserMeal.getTitle());
            //FragmentTransaction ft = getFragmentManager().beginTransaction();
            //ft.replace(R.id.fragment_main_container, frag, frag.getTag());
            //ft.commit();

            Toast.makeText(getActivity(),
                    mBrowserMeal.getTitle() + " clicked!", Toast.LENGTH_SHORT)
                    .show();

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

        updateUI();

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

    private void updateToolbarText(CharSequence text) {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

    private void updateUI() {
        BrowseMealCatalog browseMealCatalog = BrowseMealCatalog.get(getActivity());
        List<BrowserMeal> browserMeals = browseMealCatalog.getBrowserMeals();

        List<SpoonacularMeal> search_results = new Vector<>();

        try {
            JSONArray recipe_array = new JSONArray("[{\"id\":637876,\"title\":\"chicken 65\"},{\"id\":42569,\"title\":\"chicken bbq\"},{\"id\":74194,\"title\":\"chicken olé\"},{\"id\":83890,\"title\":\"chicken blt\"}]");
            int recipe_count = recipe_array.length();
            for (int i = 0; i < recipe_count; ++i) {
                JSONObject obj = recipe_array.getJSONObject(i);
                search_results.add(new SpoonacularMeal(obj.getInt("id"), obj.getString("title")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mBrowserListAdapter = new BrowserListAdapter(search_results);
        mBrowserRecyclerView.setAdapter(mBrowserListAdapter);
    }

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

    public interface MealBrowserListener {
        void OnMealAdded(int id);
    }
}
