package kr.ac.kookmin.cs.musikup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AlarmActivity extends Activity {
    SharedPreferences pref;
    SharedPreferences.Editor edit;

    final Context mContext = this;
    AlarmManager mAlarmMgr;
    Dialog customDialog;
    TimePicker timePicker;
    Button setAlarmBtn, cancelAlarmBtn, selectBtn, setBtn, exitBtn;
    ToggleButton toggleSun, toggleMon, toggleTue, toggleWed, toggleThu, toggleFri, toggleSat;
    String[] weekday = {"일","월","화","수","목","금","토"};
    boolean[] week;
    TextView mTimeDisplay, mSeedSongDisplay;
    int mHour, mMinute=-1;

    String musicFilePath = null;
    String artist = null;
    String title = null;

    private URL url;
    private HttpURLConnection urlConnection;
    private static String boundary = "ABAB***ABAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = getSharedPreferences("AlarmTable", MODE_PRIVATE);
        edit = pref.edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        mAlarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        mTimeDisplay = (TextView) findViewById(R.id.timePick);
        mSeedSongDisplay = (TextView) findViewById(R.id.songPick);
        setAlarmBtn = (Button) findViewById(R.id.setAlarmButton);
        cancelAlarmBtn = (Button) findViewById(R.id.cancelAlarmButton);

        if(pref.getInt("minute",-1)!=-1){ //이미 알람이 등록 되어 있을 경우 알람 설정 내용을 보여준다.
          //  mSeedSongDisplay.setText(pref.getString("title", null) + "-" + pref.getString("artist",null));
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

                    /*Dialog Button Listener*/

                    // Open Music List which User has on user's phone.
                    selectBtn.setOnClickListener(new View.OnClickListener() {
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
                            pushSeedInfo();
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
        customDialog.setTitle("알람 시간과 요일을 선택하세요.");

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
        calendar.set(Calendar.MILLISECOND, 20000);

        edit.putInt("hour",mHour);
        edit.putInt("minute",mMinute-1);
        edit.putInt("millisecond", 20000);
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
        mSeedSongDisplay.setText("");
    }

    private void updateDisplay() {
        String am_pm = "오전";
        int hour = pref.getInt("hour",-1);
        int minute = pref.getInt("minute", -1);
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
        mSeedSongDisplay.setText(pref.getString("title", "") + " - " + pref.getString("artist",""));
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
            title = data.getStringExtra("filename");
            edit.putString("title",title);
            edit.putString("artist",artist);
            edit.commit();
            selectBtn.setText(title + "-" + artist);

         //   sendSeedFile();
        }
    }

    public void pushSeedInfo(){ //push Seed Info

        ServerRequest sr = new ServerRequest();
        List<NameValuePair> seedInfo = new ArrayList<NameValuePair>();
        seedInfo.add(new BasicNameValuePair("title", title));
        seedInfo.add(new BasicNameValuePair("artist", artist));
        JSONObject json;

        json = sr.getJSON("http://52.68.250.226:3000/music/search",seedInfo); //send id,pwd info
        if(json != null){
            System.out.println("받았다"+json);
            try{
                String jsonstr = json.getString("response");

                if(json.getBoolean("res")){         //보나셀 서버에 seed 정보 있으면 서버에서 추천음악 정보를 줌
                   //prepare ULR // Recommand Music

                    String reTitle = json.getString("title");
                    String reArtist = json.getString("artist");
                    edit.putString("reTitle",reTitle);
                    edit.putString("reArtist", reArtist);
                    edit.commit();
                }
                else{                   //보나셀 서버에 seed 정보 없으면
                    showMessage("Seed File을 전송하고 있습니다. 잠시만 기다리세요.");
                    System.out.println("sendSeedFile func");
                    sendSeedFile();
                }

                showMessage(jsonstr);

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void askRecomInfo(){ //ReAsk Recommend Music Info

        ServerRequest sr = new ServerRequest();
        List<NameValuePair> seedInfo = new ArrayList<NameValuePair>();
        seedInfo.add(new BasicNameValuePair("title", title));
        seedInfo.add(new BasicNameValuePair("artist", artist));
        JSONObject json;

        json = sr.getJSON("http://52.68.250.226:3000/music/feature",seedInfo); //send id,pwd info
        if(json != null){
            System.out.println("받았다"+json);
            try{
                String jsonstr = json.getString("response");

                if(json.getBoolean("res")){         //보나셀 서버에 seed 정보 있으면 서버에서 추천음악 정보를 줌
                    //prepare ULR // Recommand Music

                    String reTitle = json.getString("title");
                    String reArtist = json.getString("artist");
                    edit.putString("reTitle",reTitle);
                    edit.putString("reArtist", reArtist);
                    edit.commit();
                }

                showMessage(jsonstr);

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }



    }

    public void sendSeedFile(){

        Thread work = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    url = new URL("http://52.68.250.226:3000/upload");
                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    //urlConnection.setChunkedStreamingMode(0);
                    urlConnection.setUseCaches(false);

                    urlConnection.setRequestMethod("POST");
                    urlConnection
                            .setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());

                    File path = new File(musicFilePath);
                    if (!path.exists()) {
                        Log.e("mytag", "file not exists");
                        return;
                    }

                    out.writeBytes("--" + boundary + "\r\n");
                    out.writeBytes(
                            "Content-Disposition: form-data;" + "name=\"playinfo\";" + "\r\n");
                    out.writeBytes("\r\n");
                    out.writeBytes(
                            "{\"title\":\"" + title + "\"," + "\"artist\":\"" + artist + "\"}" + "\r\n");
                    out.flush();

                    out.writeBytes("--" + boundary + "\r\n");
                    out.writeBytes(
                            "Content-Disposition: form-data;" + "name=\"userinfo\";" + "\r\n");
                    out.writeBytes("\r\n");
                    out.writeBytes("{\"user_id\":\"guest\"," + "\"request\":\"play\"}" + "\r\n");
                    out.flush();

                    out.writeBytes("--" + boundary + "\r\n");
                    out.writeBytes(
                            "Content-Disposition: form-data;" + "name=\"uploaded\";"
                                    + "filename=\"" + title + "\"" + "\r\n");
                    out.writeBytes("\r\n");

                    FileInputStream filestream = new FileInputStream(path);

                    int bytesAvailable = filestream.available();
                    int bufsize = Math.min(bytesAvailable, 1024 * 32);
                    byte[] buff = new byte[bufsize];
                    while (filestream.read(buff, 0, bufsize) > 0) {
                        out.write(buff, 0, bufsize);
                        bytesAvailable = filestream.available();
                        bufsize = Math.min(bytesAvailable, 1024 * 32);
                    }

                    out.writeBytes("\r\n");
                    out.writeBytes("--" + boundary + "--\r\n");

                    filestream.close();
                    out.flush();
                    out.close();

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    int data;
                    String result = "";
                    while ((data = in.read()) != -1) {
                        result += (char) data;
                    }
                    Log.i("mytag", result);
                    in.close();

                    //Toast.makeText(getApplicationContext(), "recv data : " + result, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            }

        });

        work.start();

        if(!work.isAlive()) {
            showMessage("File 전송이 완료되었습니다.");
            askRecomInfo();
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
