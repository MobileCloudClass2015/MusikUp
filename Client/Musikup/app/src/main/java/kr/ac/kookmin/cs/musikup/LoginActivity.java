package kr.ac.kookmin.cs.musikup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends Activity {

    public static final int REQUEST_CODE_REGISTER = 1004;

    Button loginBtn, registrationBtn;
    EditText idEntry,pwdEntry;
    String id, pwd;
    List<NameValuePair> params;
    ServerRequest sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sr = new ServerRequest();

        idEntry = (EditText) findViewById(R.id.idEntry);
        pwdEntry = (EditText) findViewById(R.id.pwdEntry);
        buttonEventHandle();
    }

    private void buttonEventHandle(){ //각 button의 동작
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = idEntry.getText().toString();
                String pwd = pwdEntry.getText().toString();

                if(id.equals("")){
                    showMessage("ID를 입력하세요.");
                }

                else if(pwd.equals("")){
                    showMessage("비밀번호를 입력하세요.");
                }
                else {
                    loginTask();
                }


            }
        });

        registrationBtn = (Button) findViewById(R.id.registrationBtn);
        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_REGISTER);
            }
        });

    }

    private void loginTask() {

        id = idEntry.getText().toString();
        pwd = pwdEntry.getText().toString();
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", id));
        params.add(new BasicNameValuePair("password", pwd));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON("http://52.68.250.226:3000/login",params); //send id,pwd info
        if(json != null){
            try{
                String jsonstr = json.getString("response");
                if(json.getBoolean("res")){ //login success
                    Intent intent = new Intent(LoginActivity.this, AlarmActivity.class);
                    startActivity(intent);
                    finish();
                }

                showMessage(jsonstr);

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
