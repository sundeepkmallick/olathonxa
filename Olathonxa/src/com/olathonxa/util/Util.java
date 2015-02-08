package com.olathonxa.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.olathonxa.R;

public class Util {
	public static View getTabIndicator(Context context, int title) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(title);
        return view;
    }
	
	public static String dateChangeFormat(String str_date, String oldformat,
			String newformat) {
		String formattedDate = str_date;
		Date date = null;
		try {

			DateFormat formatter, sdf;
			formatter = new SimpleDateFormat(oldformat, Locale.ENGLISH);
			sdf = new SimpleDateFormat(newformat, Locale.ENGLISH);
			date = (Date) formatter.parse(str_date);
			formattedDate = sdf.format(date);
		} catch (ParseException e) {
			System.out.println("Exception :" + e);
		}
		return formattedDate;
	}
	
	public static String getDeviceDatePattern(Context context){
		String format = Settings.System.getString(
				context.getContentResolver(), Settings.System.DATE_FORMAT);
		if (TextUtils.isEmpty(format)) {
			format = "yyyy/MM/dd";
		}
		return format;
	}
	
	public static String daymonthyearToDate(Context context, int year, int monthOfYear,
			int dayOfMonth){
		String formattedDate = "";
		try{
			String mYearStr = String.format(Locale.ENGLISH, "%02d", year);
			String mMonthStr = String.format(Locale.ENGLISH, "%02d", monthOfYear + 1);
			String mDayStr = String.format(Locale.ENGLISH, "%02d", dayOfMonth);
			String selectedDate = mYearStr + "-" + mMonthStr + "-" + mDayStr;
			formattedDate = dateChangeFormat(selectedDate,
					"yyyy-MM-dd", getDeviceDatePattern(context));
		}catch(Exception e){
			Log.e("TAG", e.getMessage());
		}
		
		return formattedDate;
	}
	
	
}
