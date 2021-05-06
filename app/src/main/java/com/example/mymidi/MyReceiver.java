package com.example.mymidi;

import android.app.Activity;
import android.content.Intent;
import android.media.midi.MidiReceiver;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.io.IOException;

import static com.example.mymidi.MainActivity.spools;

public class MyReceiver extends MidiReceiver {
    private Activity activity;
    TextView myText;
    public MyReceiver(Activity activity){
        super();
        this.activity=activity;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onSend(byte[] data, int offset, int count, long timestamp) throws IOException {
        StringBuilder sb = new StringBuilder();
        myText=activity.findViewById(R.id.myData);
        for (byte b : data) {         // data의 값들을 하나씩 b에 대입하여 반복하는 문법
            sb.append(String.format("%02x", b & 0xff));           // 받은 데이터를 헥스 문자열로 변환
            if(sb.toString().length()>=8)    // 필요한 부분만 남기고 다음 메시지는 걸러 레이턴시 크게 감소
                break;
        }
        String receivedDataString = sb.toString();
        //.substring(0, 20);       // 문자열 뒤쪽 필요없는 데이터 거름
        new Thread(new Runnable(){/////////////////////(중요) 쓰레드 내에서 UI 작업하는 기능 !!!!!!!!!
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        myText.setText(receivedDataString);
                    }
                });
            }
        }).start();
        CheckData.CheckNote(data, spools, MainActivity.keys);
    }

}
