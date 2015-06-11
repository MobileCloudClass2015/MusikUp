package kr.ac.kookmin.cs.musikup;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class PopupAlarmWidget extends PopupWindow {
    Context mContext;
    private View popupView;
    PopupWindow popupWindow;
    ImageButton exitBtn;
    Button exitBtn2;
    int timer_sec = 0;
    int count = 0;
    private TimerTask second;
    private TextView timer_text;
    private final Handler handler = new Handler();

    public PopupAlarmWidget(Context context) {
        super(context);
        init(context);
    }


    public void init(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.activity_alarm_popup, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        exitBtn = (ImageButton) popupView.findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                showMessage("알람이 종료되었습니다.");
            }
        });

        exitBtn2 = (Button) popupView.findViewById(R.id.exitBtn2);
        exitBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                showMessage("알람이 종료되었습니다.");
            }
        });
//
//        show();
//        alarmTimeStart();
    }

    public void show(){
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 30);
    }


    public void alarmTimeStart() {
        timer_text = (TextView) popupView.findViewById(R.id.msg);
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
                    second.cancel();
                    timer_text.setText("알림창을 닫으면 알림이 종료됩니다.");
                    exitBtn.setVisibility(View.VISIBLE);
                    exitBtn2.setVisibility(View.VISIBLE);
                }
            }
        };
        handler.post(updater);
    }

    private void showMessage(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

}
