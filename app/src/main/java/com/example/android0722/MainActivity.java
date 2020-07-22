package com.example.android0722;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    //뷰의 참조를 저장하기 위한 변수
    EditText send;
    TextView receive;
    Button btn;

    //네트워크 사용을 위한 스레드
    //스레드는 한 번 사용하면 새로 생성해야 사용이 가능합니다.
    //
    class ThreadEx extends Thread{
        @Override
      public void run(){
            String content = null;
          try{
              //서버에 접속하는 소켓 생성
              Socket socket =
                      new Socket(
                              "192.168.0.200",
                              11000);
              //스트림 생성
              ObjectOutputStream oos =
                      new ObjectOutputStream(
                              socket.getOutputStream()
                      );
               oos.writeObject(send.getText().toString());
               oos.flush();

              ObjectInputStream ois =
                      new ObjectInputStream(
                              socket.getInputStream()
                      );
              content = (String)ois.readObject();
              //스레드에서는 UI갱신을 못함
              //receive.setText(content);
              socket.close();

          }catch(Exception e){
              Log.e("전송 에러", e.getMessage());
          }
            //핸들러에게 전송할 메시지 생성
            Message msg = new Message();
            msg.obj = content;
            //핸들러 호출
            handler.sendMessage(msg);
      }
    };

    //스레드가 전송한 내용을 출력하기 위한 핸들러
    Handler handler = new Handler(
            Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            String temp = (String)msg.obj;
            receive.setText(temp);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send = (EditText)findViewById(R.id.send);
        receive = (TextView)findViewById(R.id.receive);
        btn = (Button)findViewById(R.id.btn);

        //버튼의 클릭 이벤트 처리
        btn.setOnClickListener(
                new Button.OnClickListener(){
            public void onClick(View view){
                //스레드 시작
                new ThreadEx().start();
            }
        });

    }
}