package kr.ac.kookmin.cs.musikup;

import android.app.TimePickerDialog;
import android.content.Context;

public class CustomDialog extends TimePickerDialog {
    public CustomDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);

    }
}
