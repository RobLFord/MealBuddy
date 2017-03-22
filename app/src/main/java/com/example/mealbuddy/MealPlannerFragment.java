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

import com.example.mealbuddy.models.Plan;

import java.util.List;

/**
 * Created by Rob Ford on 3/9/2017.
 */

public class MealPlannerFragment extends Fragment {
    private RecyclerView mPlanRecyclerView;
    private PlanAdapter mPlanAdapter;


    private static final String ARG_TEXT = "arg_text";

    private String mText;


    public static Fragment newInstance(String text) {
        Fragment frag = new MealPlannerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        frag.setArguments(args);
        return frag;
    }

    private class PlanAdapter extends RecyclerView.Adapter<PlanHolder> {
        private List<Plan> mPlans;

        public PlanAdapter(List<Plan> plans) {
            mPlans = plans;
        }

        @Override
        public PlanHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new PlanHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(PlanHolder holder, int position) {
            Plan plan = mPlans.get(position);
            holder.bind(plan);
        }

        @Override
        public int getItemCount() {
            return mPlans.size();
        }
    }

    private class PlanHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

        private TextView mTitleTextView;
        private TextView mDatePeriodTextView;

        private Plan mPlan;

        public void bind(Plan plan) {
            mPlan = plan;
            mTitleTextView.setText(mPlan.getTitle());
        /*
          Will need to add a method to Plan Class to calculate the period based
          on the start and end date.
          This is just a place holder for now
        */
            mDatePeriodTextView.setText(mPlan.getStartDate().toString());
        }

        public PlanHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_plan, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.plan_title);
            mDatePeriodTextView = (TextView) itemView.findViewById(R.id.plan_period_dates);
        }

        @Override
        public void onClick(View view){
            updateToolbarText(mPlan.getTitle());

            Fragment frag = DayPlanPeriodFragment.newInstance(mPlan.getTitle());
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_main_container, frag, frag.getTag());
            ft.commit();


            Toast.makeText(getActivity(),
                    mPlan.getTitle() + " clicked!", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meal_planner, container, false);

        mPlanRecyclerView = (RecyclerView) view.findViewById(R.id.plans_recycler_view);
        mPlanRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
        PlannerCatalog plannerCatalog = PlannerCatalog.get(getActivity());
        List<Plan> plans = plannerCatalog.getPlans();

        mPlanAdapter = new PlanAdapter(plans);
        mPlanRecyclerView.setAdapter(mPlanAdapter);
    }
}
