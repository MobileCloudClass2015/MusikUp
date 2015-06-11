package kr.ac.kookmin.cs.musikup;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

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
        PendingIntent sender = PendingIntent.getActivity(context, 0, notify, 0);

        try {
            sender.send();
        } catch(Exception e){
            e.printStackTrace();
        }


    }

    public static boolean isScreenOn(Context context) {
        return ((PowerManager)context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
    }
}

