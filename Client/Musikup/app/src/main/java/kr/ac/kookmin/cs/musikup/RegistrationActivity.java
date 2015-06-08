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


public class RegistrationActivity extends Activity {
    String id,pwd,rePwd = "";
    List<NameValuePair> params;
    Button RegistrationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        RegistrationBtn = (Button) findViewById(R.id.RegistrationBtn);

        buttonEventHandle();
    }

    public void buttonEventHandle(){
        RegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText idCheck = (EditText) findViewById(R.id.idCheck);
                EditText pwdCheck = (EditText) findViewById(R.id.pwdCheck);
                EditText rePwdCheck = (EditText) findViewById(R.id.rePwdCheck);
                id = idCheck.getText().toString();
                pwd = pwdCheck.getText().toString();
                rePwd = rePwdCheck.getText().toString();

                if(id.equals("")){
                    showMessage("ID를 입력하세요.");
                }

                else if(pwd.equals("")){
                    showMessage("비밀번호를 입력하세요.");
                }

                else if(rePwd.equals("")){
                    showMessage("비밀번호를 한 번 더 입력하세요.");
                }


                else if(!rePwd.equals("")&&!pwd.equals(rePwd)){
                    showMessage("비밀번호가 일치하지 않습니다.");
                }

                else {
                    registerTask();
                }
            }
        });

    }

    public void registerTask(){
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", id));
        params.add(new BasicNameValuePair("password", pwd));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON("http://52.68.250.226:3000/register",params);

        if(json != null){
            try{
                String jsonstr = json.getString("response");
                if(json.getBoolean("res")){ //register success
                    Intent myIntent = new Intent();
                    myIntent.putExtra("user_id", id); //send user id to login activity
                    setResult(RESULT_OK, myIntent);
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
