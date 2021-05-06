package com.example.mymidi;

import android.app.Activity;
import android.content.Context;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.Stack;
import java.util.logging.Handler;

public class CheckData {
    static int pedalFlag=0;           // 0 : 페달 뗌   1 : 페달 밟음
    static int[] isKeyOn=new int[89];  // 0 : 건반 뗀 상태 1 : 건반 누른 상태  2 : 페달 밟는 도중에 건반 뗀 상태
    static int initFlag=0;
    static Stack<Integer> relNote = new Stack<>();
    static String testString; // test 버튼 출력용

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void CheckNote(byte[] data, SoundPool spools, int[] keys){
        int pitch;
        float velocity;
        int status;
        int channel;
 /*       if(initFlag==0){
            for(int i=0; i<89; i++)
                isKeyOn[i]=0;
            initFlag=1;
        }*/
        status=(Byte.toUnsignedInt(data[1])&0xf0)>>4;
        channel=Byte.toUnsignedInt(data[1])&0x0f;
        pitch=Byte.toUnsignedInt(data[2])-21;
        velocity=(float)Byte.toUnsignedInt(data[3])/127;
        //velocity=velocity * (velocity+(float)0.1); // 일정 볼륨 이상은 소리크기가 비슷비슷해져서 2차함수 그래프 형식으로 볼륨크기 조절

        //testString=receivedDataString; // 테스트 출력용
        Log.i("status : ",Integer.toString(status));
        Log.i("channel : ", Integer.toString(channel));
        Log.i("pitch : ", Integer.toString(pitch));
        Log.i("velocity : ", Float.toString(velocity));
                     //건반 신호
            if ((status!=11) && pitch < 88 && pitch >=0) {
                if (status==9) { // 건반 눌렀을 때
                    PlayNote.noteOff(spools, keys[pitch]);
                    PlayNote.noteOn(spools, keys[pitch], velocity);
                    isKeyOn[pitch]=1;
                } else if (status==8) { //건반 뗐을 때
                    if (pedalFlag == 0) {
                        PlayNote.noteOff(spools, keys[pitch]);
                        isKeyOn[pitch] = 0;
                    } else if (pedalFlag == 1) {
                        isKeyOn[pitch] = 2;  //페달 밟은 상태로 건반 뗐을 때
                        relNote.push(pitch);
                    }
                }
            }
            else {   //페달 신호
                if (velocity==1) { // 페달 밟았을 때
                    pedalFlag = 1;
                } else  { //페달 뗐을 때
                    pedalFlag = 0;
                    while(!relNote.empty()){
                        int pit=relNote.pop();
                        if(isKeyOn[pit]!=1) { // 페달 밟는 중 두 번째 눌려있는 상태의 건반은 소리를 지속시키기 위함.
                            isKeyOn[pit] = 0;
                            PlayNote.noteOff(spools, keys[pit]);
                        }
                    }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getPitch(byte data){
        return Byte.toUnsignedInt(data)-21;   // 미디신호상으로 hex코드 15 (10진수 21)이 가장 낮은 음이므로 21을 빼야함
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static float getVelocity(byte data){
        return (float)Byte.toUnsignedInt(data)/127;
    }

}
