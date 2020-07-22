package com.example.android0722;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageActivity extends AppCompatActivity {
    private ImageView imageview;
    private Button drawimage, saveimage;

    //이미지를 다운로드 받아서 바로 출력할 스레드 와 핸들러
    class SaveThread extends Thread{
        public void run(){
            try{
                URL url = new URL("https://mimgnews.pstatic.net/image/609/2020/05/12/202005121301182410_3_20200512130304439.jpg?type=w540");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setUseCaches(false);
                con.setConnectTimeout(3000);

                //바이트 스트림 생성
                InputStream is = con.getInputStream();
                //다운로드 받은 내용을 저장할 배열을 생성
                byte [] raster = new byte[con.getContentLength()];
                //파일의 내용을 읽어서 raster에 저장하고 파일에 기록
                FileOutputStream fos = openFileOutput("1.png",0);
                while (true){
                    int read = is.read(raster);
                    if(read <= 0){
                        break;
                    }
                    fos.write(raster,0,read);
                }
                is.close();
                fos.close();
                con.disconnect();
            }catch (Exception e){
                Log.e("다운로드 예외",e.getMessage());
            }
            Message message = new Message();
            message.obj = "1.png";
            saveHandler.sendMessage(message);
        }
    }
    Handler saveHandler = new Handler(Looper.getMainLooper()){
        public void handlerMessage(Message message){
            String filename = (String)message.obj;
            String imagePath = Environment.getDataDirectory().getAbsolutePath() + "/data/com.example.android0722/file/" + filename;
            imageview.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
    };
    class DrawThread extends Thread{
        @Override
        public void run(){
            try{
                //이미지 URL 만들기
                URL url = new URL("https://ssl.pstatic.net/mimgnews/image/609/2020/05/12/202005121301182410_2_20200512130304425.jpg?type=w540");
                //이미지의 스트림을 생성
                InputStream is = url.openStream();
                //비트맵으로 변환
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                //핸들러에게 전송
                Message msg = new Message();
                msg.obj = bitmap;
                drawHandler.sendMessage(msg);
            }catch(Exception e){
                Log.e("다운로드 에러", e.getMessage());
            }
        }
    }

    Handler drawHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message message){
            Bitmap bitmap = (Bitmap)message.obj;
            imageview.setImageBitmap(bitmap);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageview = (ImageView)findViewById(R.id.imageview);
        drawimage = (Button)findViewById(R.id.drawimage);
        saveimage = (Button)findViewById(R.id.saveimage);

        Button.OnClickListener listener =
                new Button.OnClickListener(){public void onClick(View view){ switch (view.getId()){
                            case R.id.drawimage:
                                new DrawThread().start();
                                break;
                            case R.id.saveimage:
                                //자신의 data 디렉토리에 이미지 파일이 있는지 확인
                                String imagePath = Environment.getDataDirectory().getAbsolutePath()
                                        +"/data/com.example.android0722/file/" + "1.png";
                                //File 객체 생성
                                File file = new File(imagePath);
                                if(file.exists()){
                                    Toast.makeText(ImageActivity.this,"파일이 존재",Toast.LENGTH_LONG).show();
                                    imageview.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                                }else{
                                    Toast.makeText(ImageActivity.this,"파일이 존재하지 않음",Toast.LENGTH_LONG).show();
                                    //다운로드 받아서 파일로 저장한 후 출력
                                    new SaveThread().start();
                                }
                                break;
                        }
                    }
                };
        drawimage.setOnClickListener(listener);
        saveimage.setOnClickListener(listener);
    }
}