package kr.ac.kookmin.cs.musikup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class AlarmActivity extends Activity {
    AlarmManager mAlarmMgr;
    Dialog customDialog;
    TimePicker timePicker;
    private Button setAlarmBtn, cancelAlarmBtn;
    private TextView mTimeDisplay;

    private int mHour, mMinute;

    //static final int TIME_DIALOG_ID = 0;
    final Context mContext = this;

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
                //showDialog(TIME_DIALOG_ID);
                showMessage("알림버튼누름");


                customDialog = new Dialog(mContext);
                customDialog.setContentView(R.layout.custom_dialog);
                customDialog.setTitle("알림 시간과 요일을 선택하세요.");

                timePicker = (TimePicker) customDialog.findViewById(R.id.timePicker);

                Button setBtn = (Button)customDialog.findViewById(R.id.setBtn);
                setBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timePicker.setOnTimeChangedListener(mTimeChangedListener); //set time, day, music
                        customDialog.dismiss();
                    }
                });

                Button exitBtn = (Button)customDialog.findViewById(R.id.cancelBtn);
                exitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                    }
                });
                customDialog.show();

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

    private TimePicker.OnTimeChangedListener mTimeChangedListener=
            new TimePicker.OnTimeChangedListener(){
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinute = minute;

                    updateDisplay();
                }
            };

//    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
//            new TimePickerDialog.OnTimeSetListener() {
//
//                @Override
//                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                    // TODO Auto-generated method stub
//                    mHour = hourOfDay;
//                    mMinute = minute;
//
//                    updateDisplay();
//                }
//            };

//    @Override
//    protected Dialog onCreateDialog(int id){
//        switch (id) {
//            case TIME_DIALOG_ID:
//                TimePickerDialog mTimPickerDialog = new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, false);
//                mTimPickerDialog.setTitle("알림 시간 설정");
//                return mTimPickerDialog;
//        }
//        return null;
//    }

    private void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
