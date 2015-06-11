package kr.ac.kookmin.cs.musikup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;


public class AlarmActivity extends Activity {
    final Context mContext = this;
    AlarmManager mAlarmMgr;
    Dialog customDialog;
    TimePicker timePicker;
    Button setAlarmBtn, cancelAlarmBtn, setBtn, exitBtn;
    ToggleButton toggleSun, toggleMon, toggleTue, toggleWed, toggleThu, toggleFri, toggleSat;
    String[] weekday = {"일","월","화","수","목","금","토"};
    boolean[] week;
    TextView mTimeDisplay;
    int mHour, mMinute=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        mAlarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        mTimeDisplay = (TextView) findViewById(R.id.timePick);
        setAlarmBtn = (Button) findViewById(R.id.setAlarmButton);
        cancelAlarmBtn = (Button) findViewById(R.id.cancelAlarmButton);
        ButtonHandler();

    }

    public void ButtonHandler(){

        setAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                init(); //Dialog init

                setBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean[] weekCheck ={false, toggleSun.isChecked(), toggleMon.isChecked(), toggleTue.isChecked(), toggleWed.isChecked(),
                                toggleThu.isChecked(), toggleFri.isChecked(), toggleSat.isChecked()};
                        week = weekCheck;

                        updateDisplay();
                        customDialog.dismiss();

                        Intent intent = new Intent(mContext, AlarmReceiver.class);
                        intent.putExtra("weekday",weekCheck); //put Checked weekday
                        SetAlarm(intent);
                    }
                });

                exitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                    }
                });
                customDialog.show();
            }
        });

        cancelAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelAlarm();
            }
        });

    }

    private void init(){

        customDialog = new Dialog(mContext);   //create CustomDialog
        customDialog.setContentView(R.layout.custom_dialog);
        customDialog.setTitle("알림 시간과 요일을 선택하세요.");

        timePicker = (TimePicker) customDialog.findViewById(R.id.timePicker);
        timePicker.setOnTimeChangedListener(mTimeChangedListener); //set time, day, music

        toggleSun = (ToggleButton) customDialog.findViewById(R.id.toggle_sun);
        toggleMon = (ToggleButton) customDialog.findViewById(R.id.toggle_mon);
        toggleTue = (ToggleButton) customDialog.findViewById(R.id.toggle_tue);
        toggleWed = (ToggleButton) customDialog.findViewById(R.id.toggle_wed);
        toggleThu = (ToggleButton) customDialog.findViewById(R.id.toggle_thu);
        toggleFri = (ToggleButton) customDialog.findViewById(R.id.toggle_fri);
        toggleSat = (ToggleButton) customDialog.findViewById(R.id.toggle_sat);

        setBtn = (Button) customDialog.findViewById(R.id.setBtn);
        exitBtn = (Button) customDialog.findViewById(R.id.cancelBtn);
    }

   TimePicker.OnTimeChangedListener mTimeChangedListener=
            new TimePicker.OnTimeChangedListener(){
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinute = minute;
                    System.out.println(mHour + ":" + mMinute);
                }
            };

    public void SetAlarm(Intent mIntent)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, mHour);
        calendar.set(Calendar.MINUTE, mMinute);
        calendar.set(Calendar.MILLISECOND, 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void CancelAlarm()
    {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    private void updateDisplay() {
        String am_pm = "오전";
        int hour = mHour;
        if(mHour>12) {
            am_pm = "오후";
            hour = mHour-12;
        }
        StringBuilder str = new StringBuilder().append(am_pm+" ").append(pad(hour)).append(":").append(pad(mMinute)).append(" /");
        for(int i=1; i<week.length; i++){
            if(week[i])
                str.append(" "+weekday[i-1]);
        }
        mTimeDisplay.setText(str);
    }

    private static String pad(int c) {
        if(c >= 10){
            return String.valueOf(c);
        }else
            return "0" + String.valueOf(c);
    }

    private void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
