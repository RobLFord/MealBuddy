package com.example.mealbuddy;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.mealbuddy.models.Plan;
import com.example.mealbuddy.models.PlannerCatalog;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Class : AddPlanFragment
 *
 * Description :
 *
 * This fragment displays the visual representation for a dialog window to create a meal plan.
 * The purpose of this fragment is to bind the data to the XML objects and pass the data entered
 * to the models. This fragment is called when the user selects the add botton in the botton right
 * corner of the screen.
 *
 */

public class AddPlanFragment extends DialogFragment{
    public static final String EXTRA_PLAN_ID =
            "com.example.mealbuddy.plan";

    private static final String ARG_PLAN_ID = "plan_id";

    private EditText mPlanName;
    private DatePicker mDatePicker;
    private EditText mServingSize;
    private RadioButton mOneWeekButton;
    private RadioButton mTwoWeeksButton;

    private Plan mPlan;

    public static AddPlanFragment newInstance(UUID planId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLAN_ID, planId);

        AddPlanFragment fragment = new AddPlanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static AddPlanFragment newInstance() {
        return new AddPlanFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_add_plan, null);

        mPlanName = (EditText) v.findViewById(R.id.plan_name_dialog);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        mOneWeekButton = (RadioButton) v.findViewById(R.id.one_week_button);
        mTwoWeeksButton = (RadioButton) v.findViewById(R.id.two_week_button);


        mServingSize = (EditText) v.findViewById(R.id.serving_size_dialog);


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.add_plan_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPlan = new Plan();
                                mPlan.setTitle(mPlanName.getText().toString());

                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth();
                                int day = mDatePicker.getDayOfMonth();
                                Date date = new GregorianCalendar(year, month, day).getTime();

                                mPlan.setStartDate(date);

                                if(mOneWeekButton.isChecked()){
                                    mPlan.setDuration(7);
                                } else if(mTwoWeeksButton.isChecked()){
                                    mPlan.setDuration(14);
                                }

                                sendResult(Activity.RESULT_OK, mPlan.getId());
                            }
                        })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED, mPlan.getId());
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, UUID planId) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("NewPlan", mPlan);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
