package com.olathonxa.fragment;

import android.app.TimePickerDialog;
import android.content.Context;

public class SafeTimePickerDialog extends TimePickerDialog {
    private boolean isStopped = false;
    public SafeTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute,
                                boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isStopped = false;
    }

    @Override
    protected void onStop() {
        isStopped = true;
        super.onStop();
    }

    public boolean isStopped() {
        return isStopped;
    }
}

