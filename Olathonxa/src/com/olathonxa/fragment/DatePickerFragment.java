package com.olathonxa.fragment;

import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.olathonxa.util.Util;

public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	Context mContext;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}
	
	public DatePickerFragment() {
		
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(mContext, mListener, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		String mYearStr = String.format(Locale.ENGLISH, "%02d", year);
		String mMonthStr = String.format(Locale.ENGLISH, "%02d", month + 1);
		String mDayStr = String.format(Locale.ENGLISH, "%02d", day);
		String selectedDate = mYearStr + "-" + mMonthStr + "-" + mDayStr;
		String formattedDate = Util.dateChangeFormat(selectedDate,
				"yyyy-mm-dd", Util.getDeviceDatePattern(mContext));
		DatePickerListener activity = (DatePickerListener) mContext;
		activity.onDateSelected(DatePickerFragment.this, formattedDate);
	}

	/*
	 * The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks. Each method
	 * passes the DialogFragment in case the host needs to query it.
	 */
	public interface DatePickerListener {
		public void onDateSelected(DialogFragment dialog, String selectedDate);
	}

	// Use this instance of the interface to deliver action events
	OnDateSetListener mListener;
	
	public void setCallBack(OnDateSetListener ondate) {
		mListener = ondate;
	}
}