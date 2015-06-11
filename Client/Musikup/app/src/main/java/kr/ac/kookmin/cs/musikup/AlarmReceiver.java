package kr.ac.kookmin.cs.musikup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
//        wl.acquire();
//
//
//        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // example
//
//        wl.release();

        boolean[] week = intent.getBooleanArrayExtra("weekday");
        Calendar calendar = Calendar.getInstance();

        if(!week[calendar.get(Calendar.DAY_OF_WEEK)]) //Sunday=1, Monday=2... Saturday=7
            return;
        
        Intnet notiyfy = new Intent(context,)

        
    }

    private long setTriggerTime()
    {
        // current Time
        long atime = System.currentTimeMillis();
        // timepicker
        Calendar curTime = Calendar.getInstance();
       // curTime.set(Calendar.HOUR_OF_DAY, this.mAlarmData.getHour(this));
       // curTime.set(Calendar.MINUTE, this.mAlarmData.getMinute(this));
        curTime.set(Calendar.SECOND, 0);
        curTime.set(Calendar.MILLISECOND, 0);
        long btime = curTime.getTimeInMillis();
        long triggerTime = btime;
        if (atime > btime)
            triggerTime += 1000 * 60 * 60 * 24;

        return triggerTime;
    }

//    @Override
//    public void onReceive(Context context, Intent intent)
//    {
//
//        Bundle extra = intent.getExtras();
//        if (extra != null)
//        {
//            boolean isOneTime = extra.getBoolean("one_time");
//            if (isOneTime)
//            {
//              //  AlarmDataManager.getInstance().setAlarmEnable(context, false);
//                // 알람 울리기.
//            }
//            else
//            {
//                boolean[] week = extra.getBooleanArray("day_of_week");
//
//                Calendar cal = Calendar.getInstance();
//
//                if (!week[cal.get(Calendar.DAY_OF_WEEK)])
//                    return;
//
//                // 알람 울리기.
//            }
//        }
//    }
}

