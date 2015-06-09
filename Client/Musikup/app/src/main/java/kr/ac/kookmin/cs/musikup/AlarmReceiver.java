package kr.ac.kookmin.cs.musikup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Toast.makeText(context, "hi", Toast.LENGTH_LONG).show();
    }
}

