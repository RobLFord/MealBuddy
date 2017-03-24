package com.example.mealbuddy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealbuddy.models.Plan;
import com.example.mealbuddy.models.PlannerCatalog;
import com.example.mealbuddy.models.User;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Rob Ford on 3/9/2017.
 */

public class MealPlannerFragment extends Fragment {
    private static final String TAG = "MealPlannerFragment";

    private RecyclerView mPlanRecyclerView;
    private PlanAdapter mPlanAdapter;
    private FloatingActionButton mAddPlanButton;

    private User mUser;

    private static final String ARG_TEXT = "arg_text";
    private static final String DIALOG_PLAN = "DialogPlan";

    private static final int ADD_PLAN = 0;

    private String mText;


    public static Fragment newInstance(String text, User user) {
        Fragment frag = new MealPlannerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putParcelable("User", user);
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
            mDatePeriodTextView.setText(mPlan.getPlanPeriod());
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
            ft.addToBackStack(null).commit();


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

        if (savedInstanceState == null) {
            Bundle args = getArguments();
            mUser = args.getParcelable("User");
        } else {
            mUser = savedInstanceState.getParcelable("User");
        }

        updateUI();

        mAddPlanButton = (FloatingActionButton) view.findViewById(R.id.add_plan_button);
        mAddPlanButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                AddPlanFragment dialog = AddPlanFragment.newInstance();
                dialog.setTargetFragment(MealPlannerFragment.this, ADD_PLAN);
                dialog.show(manager, DIALOG_PLAN);
            }
        });

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
        outState.putParcelable("User", mUser);
        super.onSaveInstanceState(outState);
    }

    private void updateToolbarText(CharSequence text) {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

    private void updateUI() {
//        PlannerCatalog plannerCatalog = PlannerCatalog.get(getActivity());
//        List<Plan> plans = plannerCatalog.getPlans();


        //mPlanAdapter = new PlanAdapter(mUser.getPlans());
        if(mPlanAdapter == null){
            mPlanAdapter = new PlanAdapter(mUser.getPlans());
            mPlanRecyclerView.setAdapter(mPlanAdapter);
        } else {
            mPlanAdapter.notifyDataSetChanged();
        }

    }

    PlannerListener mPlannerListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.i(TAG, "onAttach: " + context.toString());

        try {
            mPlannerListener = (PlannerListener) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException(context.toString()
                    + " must implement PlannerListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        } else {
            if (mPlannerListener != null) {
                Plan newPlan = data.getExtras().getParcelable("NewPlan");
                mUser.addPlan(newPlan);
                mPlannerListener.OnAddPlan(newPlan);
                updateUI();
            } else {
                Log.e(TAG, "onActivityResult: listener is null");
            }
        }
    }

    public interface PlannerListener {
        void OnAddPlan(Plan newPlan);
    }
}
