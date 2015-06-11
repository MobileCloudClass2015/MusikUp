package kr.ac.kookmin.cs.musikup;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;


public class AlarmPopupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(this);
        setContentView(inflater.inflate(R.layout.activity_alarm_popup,null));
    }

}
