package com.example.mymidi;

import android.media.midi.MidiReceiver;
import android.view.View;

import java.io.IOException;


import static com.example.mymidi.MainActivity.spools;

public class MyReceiver extends MidiReceiver {

    public void onSend(byte[] data, int offset, int count, long timestamp) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {         // data의 값들을 하나씩 b에 대입하여 반복하는 문법
            sb.append(String.format("%02x", b & 0xff));           // 받은 데이터를 헥스 문자열로 변환
            if(sb.toString().length()>=20)    // 필요한 부분만 남기고 다음 메시지는 걸러 레이턴시 크게 감소
                break;
        }
        String receivedDataString = sb.toString();//.substring(0, 20);       // 문자열 뒤쪽 필요없는 데이터 거름

        CheckData.CheckNote(receivedDataString, spools, MainActivity.keys);
    }
}
