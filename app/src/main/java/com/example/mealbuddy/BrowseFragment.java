package com.example.mealbuddy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import java.util.List;

/**
 * Created by Rob Ford on 3/9/2017.
 */

public class BrowseFragment extends Fragment {
    private RecyclerView mBrowserRecyclerView;
    private BrowserListAdapter mBrowserListAdapter;

    private static final String ARG_TEXT = "arg_text";

    private String mText;
    private TextView mTextView;

    public static Fragment newInstance(String text) {
        Fragment frag = new BrowseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        frag.setArguments(args);
        return frag;
    }

    private class BrowserListAdapter extends RecyclerView.Adapter<BrowserListHolder> {
        private List<BrowserMeal> mBrowserMeals;

        public BrowserListAdapter(List<BrowserMeal> browserMeals) {
            mBrowserMeals = browserMeals;
        }

        @Override
        public BrowserListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new BrowserListHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(BrowserListHolder holder, int position) {
            BrowserMeal browserMeal = mBrowserMeals.get(position);
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

        private BrowserMeal mBrowserMeal;

        public void bind(BrowserMeal browserMeal) {
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

    private void updateToolbarText(CharSequence text) {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

    private void updateUI() {
        BrowseMealCatalog browseMealCatalog = BrowseMealCatalog.get(getActivity());
        List<BrowserMeal> browserMeals = browseMealCatalog.getBrowserMeals();

        mBrowserListAdapter = new BrowserListAdapter(browserMeals);
        mBrowserRecyclerView.setAdapter(mBrowserListAdapter);
    }
}
