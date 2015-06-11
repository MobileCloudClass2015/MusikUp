package kr.ac.kookmin.cs.musikup;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {

        boolean[] week = intent.getBooleanArrayExtra("weekday");
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar);
        System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
        System.out.println(week[calendar.get(Calendar.DAY_OF_WEEK)]);
        if(!week[calendar.get(Calendar.DAY_OF_WEEK)]) //Sunday=1, Monday=2... Saturday=7
            return;
        
        Intent notify = new Intent(context,AlarmPopupActivity.class);
        notify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent sender = PendingIntent.getActivity(context, 0, notify, 0);

        try {
            sender.send();
        } catch(Exception e){
            e.printStackTrace();
        }


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

