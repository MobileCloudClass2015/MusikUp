package org.androidtown.media.audio.player;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UTFDataFormatException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    String musicFilePath = null;
    String artist = null;
    String filename = null;
    String title = null;
    String album = null;

    private URL url;
    private HttpURLConnection urlConnection;
    private static String boundary = "ABAB***ABAB";

    Button selectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //버튼 클릭시 발생하는 event
        selectButton = (Button) findViewById(R.id.selectbtn);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MusicListActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            // 액티비티가 정상적으로 종료되었을 경우
            musicFilePath = data.getStringExtra("filepath");
            artist = data.getStringExtra("artist");
            filename = data.getStringExtra("name");
            album = data.getStringExtra("album");
            title = data.getStringExtra("title");

            selectButton.setText(filename + "-" + artist);
        }
    }

    public void onSettingButtonClicked(View v){
        pushSeed();
        Toast.makeText(getApplicationContext(), "push seed", Toast.LENGTH_SHORT).show();
    }

    public void pushSeed(){

        Thread work = new Thread(new Runnable() {
            @Override
            public void run() {

                    try {
                        url = new URL("http://52.68.250.226:3000/upload");
                        urlConnection = (HttpURLConnection) url.openConnection(); // HTTP ����

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
                                        + "filename=\"" + filename + "\"" + "\r\n");
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
    }

/*
    public void pushSeed() {

        HttpClient client = new DefaultHttpClient();
        String postURL = "http://52.68.250.226:3000/music/search";
        HttpPost post = new HttpPost(postURL);


        MultipartEntity reqEntity = new MultipartEntity(
                HttpMultipartMode.BROWSER_COMPATIBLE);

        StringBody sb10 = new StringBody(hpNo + subject, Charset.forName("UTF-8"));  //스트링바디에 인코딩 캐릭터셋 추가

        reqEntity.addPart("subject", sb10);
        reqEntity.addPart("msg", sb10);


        ////2012 11 14 추가된 함수 파일 카운트 계산하여 반복처리
        ///널이 아닐경우만

        int i = 0;

        for (i = 0; i < arrFileName.length; i++) {
            if (arrFileName[i] != null) {
                reqEntity.addPart("fileName" + Integer.toString(i + 1), new FileBody(new File(arrFileName[i]), arrFileName[i], arrMimeType[i], null));
            }

        }
        post.setEntity(reqEntity);
        HttpResponse responsePOST = client.execute(post);
        HttpEntity resEntity = responsePOST.getEntity();

        Log.v("HOTDRAG", responsePOST.toString());

        if (resEntity != null) {
            Log.w("RESPONSE", EntityUtils.toString(resEntity));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }*/

    /*
    public void pushSeed(){

        String serverIP="52.68.250.226";
        int serverPort = 3000;

        System.out.print("111111111111111111111");
        try{
            InetAddress serverAddr = InetAddress.getByName(serverIP);
            Socket sock = new Socket(serverAddr, serverPort);
            System.out.print("2222222222222222222222");
            try{
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                        sock.getOutputStream())), true);
                out.println(title);
                out.flush();

                DataInputStream input = new DataInputStream(new FileInputStream(
                        new File(musicFilePath)));
                DataOutputStream output = new DataOutputStream(sock.getOutputStream());

                byte[] buf = new byte[1024];
                while(input.read(buf)>0){
                    output.write(buf);
                    output.flush();
                }
                output.close();
                System.out.print("33333333333333333333333333");
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally
            {
                sock.close();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
