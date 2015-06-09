package kr.ac.kookmin.cs.musikup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


public class AlarmActivity extends Activity {
    AlarmManager mAlarmMgr;

    private Button setAlarmBtn, cancelAlarmBtn;
    private TextView mTimeDisplay;

    private int mHour;
    private int mMinute;

    static final int TIME_DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        mAlarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);

        mTimeDisplay = (TextView)findViewById(R.id.timePick);
        setAlarmBtn = (Button)findViewById(R.id.setAlarmButton);
        setAlarmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog(TIME_DIALOG_ID);
            }
        });

        Calendar cal = Calendar.getInstance();



    }

    private void updateDisplay() {
        // TODO Auto-generated method stub
        mTimeDisplay.setText(new StringBuilder().append(pad(mHour)).append(":").append(pad(mMinute)));
    }

    private static String pad(int c) {
        // TODO Auto-generated method stub
        if(c >= 10){
            return String.valueOf(c);
        }else
            return "0" + String.valueOf(c);
    }

    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    // TODO Auto-generated method stub
                    mHour = hourOfDay;
                    mMinute = minute;

                    updateDisplay();
                }
            };

    @Override
    protected Dialog onCreateDialog(int id){
        switch (id) {
            case TIME_DIALOG_ID:
                TimePickerDialog mTimPickerDialog = new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, false);
                mTimPickerDialog.setTitle("알림 시간 설정");
                return mTimPickerDialog;
        }
        return null;
    }
}
