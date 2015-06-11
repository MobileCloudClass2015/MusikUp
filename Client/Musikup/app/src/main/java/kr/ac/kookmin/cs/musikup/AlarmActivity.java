package kr.ac.kookmin.cs.musikup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.Calendar;


public class AlarmActivity extends Activity {
    SharedPreferences pref;
    SharedPreferences.Editor edit;

    final Context mContext = this;
    AlarmManager mAlarmMgr;
    Dialog customDialog;
    TimePicker timePicker;
    Button setAlarmBtn, cancelAlarmBtn, selectBtn, selectSeedBtn, setBtn, exitBtn;
    ToggleButton toggleSun, toggleMon, toggleTue, toggleWed, toggleThu, toggleFri, toggleSat;
    String[] weekday = {"일","월","화","수","목","금","토"};
    boolean[] week;
    TextView mTimeDisplay, mSeedSongDisplay;
    int mHour, mMinute=-1;

    String musicFilePath = null;
    String artist = null;
    String title = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = getSharedPreferences("AlarmTable", MODE_PRIVATE);
        edit = pref.edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        mAlarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        mTimeDisplay = (TextView) findViewById(R.id.timePick);
        setAlarmBtn = (Button) findViewById(R.id.setAlarmButton);
        cancelAlarmBtn = (Button) findViewById(R.id.cancelAlarmButton);

        if(pref.getInt("minute",-1)!=-1){
            updateDisplay();
        }
        ButtonHandler();

    }

    public void ButtonHandler(){

        setAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pref.getInt("minute", -1) != -1) {
                    showMessage("이미 알람이 존재합니다.");
                }
                else {
                    init(); //Dialog init

                    //Dialog Button Listener
                    selectBtn.setOnClickListener(new View.OnClickListener() { // Open Music List which User has on user's phone..
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), MusicListActivity.class);
                            startActivityForResult(intent, 1);
                        }
                    });

                    setBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean[] weekCheck = {false, toggleSun.isChecked(), toggleMon.isChecked(), toggleTue.isChecked(),
                                    toggleWed.isChecked(), toggleThu.isChecked(), toggleFri.isChecked(), toggleSat.isChecked()};
                            week = weekCheck;

                            edit.putBoolean("Sun",toggleSun.isChecked());
                            edit.putBoolean("Mon",toggleMon.isChecked());
                            edit.putBoolean("Tue",toggleTue.isChecked());
                            edit.putBoolean("Wed",toggleWed.isChecked());
                            edit.putBoolean("Thu",toggleThu.isChecked());
                            edit.putBoolean("Fri",toggleFri.isChecked());
                            edit.putBoolean("Sat",toggleSat.isChecked());

                            edit.commit();

                            Intent intent = new Intent(mContext, AlarmReceiver.class);
                            intent.putExtra("weekday", weekCheck); //put Checked weekday
                            SetAlarm(intent);
                            System.out.println(mHour + ":" + mMinute);

                            updateDisplay();
                            customDialog.dismiss();
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
            }
        });

        cancelAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pref.getInt("minute",-1)!=-1){
                    CancelAlarm();
                }
                else    showMessage("해제할 알람이 없습니다.");
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

        selectBtn = (Button) customDialog.findViewById(R.id.selectBtn);
        setBtn = (Button) customDialog.findViewById(R.id.setBtn);
        exitBtn = (Button) customDialog.findViewById(R.id.cancelBtn);
    }

   TimePicker.OnTimeChangedListener mTimeChangedListener=
            new TimePicker.OnTimeChangedListener(){
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinute = minute;
                }
            };

    public void SetAlarm(Intent mIntent)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, mHour);
        calendar.set(Calendar.MINUTE, mMinute-1);
        calendar.set(Calendar.MILLISECOND, 30000);

        edit.putInt("hour",mHour);
        edit.putInt("minute",mMinute-1);
        edit.putInt("millisecond", 30000);
        edit.commit();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void CancelAlarm()
    {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        sender.cancel();
        edit.clear();
        edit.commit();
        mTimeDisplay.setText("");
    }

    private void updateDisplay() {
        String am_pm = "오전";
        int hour = pref.getInt("hour",-1);
        int minute = pref.getInt("minute",-1);
        if(hour>12) {
            am_pm = "오후";
            hour -= 12;
        }
        StringBuilder str = new StringBuilder().append(am_pm+" ").append(pad(hour)).append(":").append(pad(minute+1)).append(" /");

        boolean[] w = {false, pref.getBoolean("Sun",false),pref.getBoolean("Mon",false),pref.getBoolean("Tue",false),pref.getBoolean("Wed",false),
                pref.getBoolean("Thu",false),pref.getBoolean("Fri",false),pref.getBoolean("Sat",false)};
        for(int i=1; i<w.length; i++){
            if(w[i])
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            musicFilePath = data.getStringExtra("filepath");
            artist = data.getStringExtra("artist");
            title = data.getStringExtra("title");

            selectBtn.setText(title + "-" + artist);

         //   pushSeed();
        }
    }

    public void pushSeed(){
        try{
            Socket sock = new Socket("52.68.250.226", 8000);

            try{
                DataInputStream input = new DataInputStream(new FileInputStream(
                        new File(musicFilePath)));
                DataOutputStream output = new DataOutputStream(sock.getOutputStream());

                byte[] buf = new byte[1024];
                while(input.read(buf)>0){
                    output.write(buf);
                    output.flush();
                }
                output.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally
            {
                sock.close();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
