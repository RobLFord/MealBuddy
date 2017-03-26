package com.example.mealbuddy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealbuddy.models.DayPlan;
import com.example.mealbuddy.models.Plan;
import com.example.mealbuddy.models.Recipe;

import java.util.List;

/**
 * Created by Rob Ford on 3/18/2017.
 */

public class DayPlanPeriodFragment extends Fragment {
    private static final String TAG = "DayPlanPeriodFragment";

    private RecyclerView mDayPlanPeriodRecyclerView;
    private DayPlanAdapter mDayPlanAdapter;
    private TextView mPlanTitleDatePeriod;
    private Plan mPlan;

    private static final String ARG_TEXT = "arg_text";

    private String mText;

    public static Fragment newInstance(String text, Plan plan) {
        Fragment frag = new DayPlanPeriodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putParcelable("Plan", plan);
        frag.setArguments(args);
        return frag;
    }

    private class DayPlanAdapter extends RecyclerView.Adapter<DayPlanHolder> {
        private List<DayPlan> mDayPlans;

        public DayPlanAdapter(List<DayPlan> dayPlans) {
            mDayPlans = dayPlans;
        }

        @Override
        public DayPlanHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new DayPlanHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(DayPlanHolder holder, int position) {
            DayPlan dayPlan = mDayPlans.get(position);
            holder.bind(dayPlan);
        }

        @Override
        public int getItemCount() {
            return mDayPlans.size();
        }
    }

    private class DayPlanHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private TextView mDayOfWeekTextView;
        private TextView mDayOfMonthTextView;
        private EditText mMealNameList;

        private DayPlan mDayPlan;

        public void bind(DayPlan dayPlan) {
            mDayPlan = dayPlan;
            mDayOfWeekTextView.setText(mDayPlan.getDayOfWeek());
            mDayOfMonthTextView.setText(Integer.toString(mDayPlan.getDayOfMonth()));

            StringBuffer mealText = new StringBuffer();
            for (Recipe recipe : mDayPlan.getRecipes()) {
                mealText.append(recipe.getName() + "\n");
            }
            mMealNameList.setText(mealText.toString());
        }

        public DayPlanHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_day_plan, parent, false));
            itemView.setOnClickListener(this);

            mDayOfWeekTextView = (TextView) itemView.findViewById(R.id.plan_period_dayOfWeek);
            mDayOfMonthTextView = (TextView) itemView.findViewById(R.id.day_of_month_textView);
            mMealNameList = (EditText) itemView.findViewById(R.id.meal_for_day_text);
        }

        @Override
        public void onClick(View view){
            Toast.makeText(getActivity(),
                    mDayPlan.getDayOfWeek() + " clicked!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_day_plan_period, container, false);

        //Will have to pass an intent of Plan Title and Date periods inorder to populate
        // mPlanTitleDatePeriod = (TextView) view.findViewById(R.id.plan_title_and_date_period);
        //mPlanTitleDatePeriod.setText();

        mDayPlanPeriodRecyclerView = (RecyclerView) view.findViewById(R.id.day_plan_period_recycler_view);
        mDayPlanPeriodRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mPlan = getArguments().getParcelable("Plan");

        mDayPlanAdapter = new DayPlanAdapter(mPlan.getDayPlans());
        mDayPlanPeriodRecyclerView.setAdapter(mDayPlanAdapter);

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
}
