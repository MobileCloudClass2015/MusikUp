package kr.ac.kookmin.cs.musikup;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;


public class LoginActivity extends Activity {

    public static final int REQUEST_CODE_REGISTER = 1004;

    Button loginBtn, registrationBtn;
    EditText idEntry,pwdEntry;
    String[] userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                    new loginTask().execute();
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

    private class loginTask extends AsyncTask<Void, Void, Character> {
        private String id, pwd;
        private char state;
        private Socket socket;
        private BufferedOutputStream outstream;
        private BufferedInputStream instream;

        public loginTask() {
            this.id = idEntry.getText().toString();
            this.pwd = pwdEntry.getText().toString();
            this.state = 'n';
        }

        @Override
        protected Character doInBackground(Void... params) {

            try {
                //connect
                socket = new Socket("52.68.250.226", 8000);
                System.out.println("Socket OK");
                outstream = new BufferedOutputStream(socket.getOutputStream());
                System.out.println("Outstream OK");
                instream = new BufferedInputStream(socket.getInputStream());
                System.out.println("Instream OK");

                //"l" login header
                String sendData = "l".concat("+").concat(id).concat("+").concat(pwd); //데이터스트링
                byte[] ref = sendData.getBytes("UTF-8"); //바이트배열로 변환

                System.out.println("Byte alloc OK");
                outstream.write(ref); // 서버에 변환된 데이터스트링 보냄
                outstream.flush();

                byte[] contents = new byte[1024]; //서버에서 보낸 데이터 받을 공간
                int bytesRead = 0;
                String str = null;

                bytesRead = instream.read(contents);
                System.out.println("byte:" + bytesRead);

                if (bytesRead != -1)
                    str = new String(contents, 0, bytesRead); //바이트 스트링으로 변환
                if (str != null) {
                    System.out.println("Received data : " + str);

                    if (str.charAt(0) == 'l') {
                        String[] receiveData = str.split(",");
                        state = receiveData[0].charAt(0);

                    } else {
                        state = str.charAt(0);
                    }
                }


            } catch (Exception e) {
                this.state = 'n';
                e.printStackTrace();
            }

            System.out.println("success");
            System.out.println(getStat());

            return getStat();
        }

        @Override
        protected void onPostExecute(Character stat) {
            if(stat == 'l'){
                socketClose();

                System.out.println("Success");
                showMessage("로그인에 성공하였습니다.");

                Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                startActivity(intent);
                finish();

            }
            else if(stat == 'f'){
                System.out.println("Failed : DB");
                showMessage("ID와 비밀번호를 다시 확인하세요.");
            }
            else if(stat == 'n'){
                System.out.println("Failed : disconnection");
                showMessage("Sever disconnection.");
            }
        }

        public char getStat() {
            return this.state;
        }

        public void socketClose() {
            try {
                socket.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
