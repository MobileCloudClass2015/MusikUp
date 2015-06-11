package kr.ac.kookmin.cs.musikup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class AlarmPopupActivity extends Activity {

    //View popupView;
    //PopupWindow popupWindow;
    Context mContext;
    Button exitBtn;
    int timer_sec = 0;
    int count = 0;
    private TimerTask second;
    private TextView timer_text, today;
    private final Handler handler = new Handler();
    boolean btnCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                             |WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                              |WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                               |WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        LayoutInflater inflater = LayoutInflater.from(this);
        setContentView(inflater.inflate(R.layout.activity_alarm_popup,null));

        System.out.println("popup!!!!!!!!!!!!!!");

        init();
        alarmTimeStart();
//

//        PopupAlarmWidget alarmWidget = new PopupAlarmWidget(this);
//        alarmWidget.show();
//        alarmWidget.alarmTimeStart();
    }

    public void init(){
        btnCheck = false;
        today = (TextView)findViewById(R.id.timeText);

        UpdateDisplay();

        exitBtn = (Button)findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCheck=true; //for TimerTask cancel
                finish();
            }
        });

    }

    public void alarmTimeStart() {
        timer_text = (TextView) findViewById(R.id.msg);
        timer_sec = 0;
        count = 0;

        second = new TimerTask() {

            @Override
            public void run() {
                Update();
                timer_sec++;
            }
        };
        Timer timer = new Timer();
        timer.schedule(second, 100, 1000);
    }

    protected void Update() {
        Runnable updater = new Runnable() {
            public void run() {
                if(timer_sec<10) {
                    timer_text.setText((11 - timer_sec) + "초 후에 알람창을 닫을 수 있습니다.");
                }
                else {
                    timer_text.setText("알림창을 닫으면 알림이 종료됩니다.");
                    exitBtn.setVisibility(View.VISIBLE);
                    timer_text.setVisibility(View.INVISIBLE);
                    UpdateDisplay();
                    if(btnCheck){
                        System.out.println("쓰레드 종료!!!!!!");
                        second.cancel(); //timerTask 종료
                    }
                }
            }
        };
        handler.post(updater);
    }

    public void UpdateDisplay(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String am_pm = "AM";

        if(hour>12){
            hour -= 12;
        }
        System.out.println(year+"-"+pad(month+1)+"-"+pad(day)+" "+am_pm+" "+pad(hour)+":"+pad(minute));
        today.setText(year+"-"+pad(month+1)+"-"+pad(day)+" "+am_pm+" "+pad(hour)+":"+pad(minute)); //2015-06-11 PM 12:02
    }
    private static String pad(int c) {
        if(c >= 10){
            return String.valueOf(c);
        }else
            return "0" + String.valueOf(c);
    }

    private void showMessage(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

}
