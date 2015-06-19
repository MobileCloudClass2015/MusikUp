package kr.ac.kookmin.cs.musikup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;


public class AlarmReceiver extends BroadcastReceiver {

    boolean[] week;
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences pref = context.getSharedPreferences("AlarmTable", Context.MODE_PRIVATE);
        Calendar calendar = Calendar.getInstance();

        if(pref.getInt("minute",-1)!=-1) { //알람이 있는 경우만 실행
            if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) { //재부팅시
                //Time Set
                int minute = pref.getInt("minute", -1);
                int hour = pref.getInt("hour", -1);
                int milli = pref.getInt("millisecond", -1);

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute);
                cal.set(Calendar.MILLISECOND, milli);

                //Day Set
                boolean[] w = {false, pref.getBoolean("Sun", false), pref.getBoolean("Mon", false), pref.getBoolean("Tue", false), pref.getBoolean("Wed", false),
                        pref.getBoolean("Thu", false), pref.getBoolean("Fri", false), pref.getBoolean("Sat", false)};

                week = w;

                //Alarm Set
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
            else{
                week = intent.getBooleanArrayExtra("weekday");
                System.out.println("요일설정완료");
            }
            System.out.println(week[calendar.get(Calendar.DAY_OF_WEEK)]);
            if (!week[calendar.get(Calendar.DAY_OF_WEEK)]) //Sunday=1, Monday=2... Saturday=7
                return; //false Check Day doesn't execute Alarm Popup Activity

            //Alarm Popup Activity
            Intent notify = new Intent(context, AlarmPopupActivity.class);
            PendingIntent sender = PendingIntent.getActivity(context, 0, notify, 0);

            try {
                sender.send();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

