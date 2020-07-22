package com.example.android0722;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TextDownloadActivity extends AppCompatActivity {
    private TextView display;
    private Button download;

    //데이터를 다운로드 받을 클래스
    //Thread는 재사용이 안되므로 클래스로 만들어서
    //필요할 때 마다 객체를 생성해서 사용
    class ThreadEx extends Thread{
        public void run(){
            //다운로드 받은 문자열을 저장할 변수
            String html = null;
            try{
                //다운로드 받을 주소 생성
                URL url = new URL("http://www.nate.com/");
                //연결 객체를 생성하고 옵션을 설정
                HttpURLConnection con =
                        (HttpURLConnection)url.openConnection();
                con.setUseCaches(false);
                con.setConnectTimeout(30000);
                con.setRequestMethod("GET");
                //필요한 스트림을 생성해서 읽기
                //문자열을 읽을 스트림 생성
                BufferedReader br =
                        new BufferedReader(
                            new InputStreamReader(
                                con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                while(true){
                    //한 줄 읽기
                    String line = br.readLine();
                    //읽은 내용이 없으면 종료
                    if(line == null){
                        break;
                    }
                    //읽은 내용이 있으면 sb에 추가
                    sb.append(line + "\n");
                }
                html = sb.toString();
                //생성한 객체 닫기
                br.close();
                con.disconnect();

                //UI 갱신 할 거라면 Handler를 호출해서
                //데이터를 넘겨주기
                Message message = new Message();
                message.obj = html;
                handler.sendMessage(message);

                //메인 스레드가 아닌 스레드에서 화면 갱신 할 수 없음
                //display.setText(html);

            }catch(Exception e){
                Log.e("다운로드 에러", e.getMessage());
            }
        }
    }

    //데이터를 출력하기 위한 객체
    //재사용이 가능하므로 하나의 객체를 바로 생성해서 사용
    Handler handler =
            new Handler(Looper.getMainLooper()){
        public void handleMessage(Message message){
            //데이터 가져오기
            String html = (String)message.obj;
            display.setText(html);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_download);

        display = (TextView) findViewById(R.id.display);
        download = (Button) findViewById(R.id.download);




        download.setOnClickListener(
                new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ThreadEx().start();
            }
        });


    }
}