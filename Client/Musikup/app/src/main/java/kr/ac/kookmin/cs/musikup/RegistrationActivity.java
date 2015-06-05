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


public class RegistrationActivity extends Activity {
    String id,pwd,rePwd = "";
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
                    new registerTask().execute();
                }
            }
        });
    }

    private class registerTask extends AsyncTask<Void, Void, Character> {

        private char state;
        private Socket socket;
        private BufferedOutputStream outstream;
        private BufferedInputStream instream;

        public registerTask() {
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

                //"r" registration header  ; r+id+pwd+name+age+sex
                String data = "r".concat("+").concat(id).concat("+").concat(pwd);
                byte[] ref = data.getBytes("UTF-8");

                System.out.println("Byte alloc OK");
                outstream.write(ref);
                outstream.flush();

                int stat = instream.read();
                System.out.println("Stat is : " + stat);
                if(stat != -1)
                    this.state = (char)stat;

            } catch (Exception e) {
                this.state = 'n';
                e.printStackTrace();
            }

            System.out.println("receive");
            System.out.println(getStat());

            return getStat();
        }

        @Override
        protected void onPostExecute(Character stat) {
            if(stat == 'r'){
                socketClose();
                System.out.println("Success");
                showMessage("회원가입에 성공하였습니다.");
                Intent myIntent = new Intent();
                myIntent.putExtra("user_id", getId());
                setResult(RESULT_OK, myIntent);
                finish();
            }
            else if(stat == 'f'){
                System.out.println("Failed : DB");
                showMessage("이미 사용중인 ID입니다. 다시 입력하세요.");
            }
            else if(stat == 'n'){
                System.out.println("Failed : disconnection");
                showMessage("Sever disconnection.");
            }
        }

        public String getId() {
            return id;
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
