package kr.ac.kookmin.cs.musikup;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;


public class AlarmPopupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        PopupAlarmWidget alarmWidget = new PopupAlarmWidget(this);
//        LayoutInflater inflater = LayoutInflater.from(this);
//        setContentView(inflater.inflate(R.layout.activity_alarm_popup,null));

        System.out.println("popup!!!!!!!!!!!!!!");

        //  as long as this window is visible to the user, keep the device's screen turned on and bright
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // blur everything behind this window.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        PopupAlarmWidget alarmWidget = new PopupAlarmWidget(this);
        alarmWidget.show();
        alarmWidget.alarmTimeStart();
    }

}
