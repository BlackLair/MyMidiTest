package com.example.mymidi;

import android.media.SoundPool;
import android.widget.Toast;

import java.util.Stack;

public class CheckData {
    static int pedalFlag;
    static int[] isKeyOn=new int[89];
    public static void CheckNote(String receivedDataString, SoundPool spools, int[] keys){
        int pitch;
        float velocity;
        int[] flag= new int[89];     // 이중 입력 방지
 //       int[] isKeyOn=new int[89] ;
        // 0 : 건반 뗀 상태 1 : 건반 누른 상태  2 : 페달 밟는 도중에 건반 뗀 상태

        pitch=getPitch(receivedDataString);
        velocity=getVelocity(receivedDataString);
        //for(int i=0; i<88; i++) isKeyOn[i]=0;




        if (flag[pitch]==0) {
             if(receivedDataString.substring(0,8).equals("01b0407f")) { // 페달 밟았을 때
                 pedalFlag = 1;
             }
             else if(receivedDataString.substring(0,8).equals("01b04000")){ //페달 뗐을 때
                 pedalFlag=0;
                 for(int i=0; i<88; i++){
                     if(isKeyOn[i]==2) {
                         PlayNote.noteOff(spools, keys[i]);
                         isKeyOn[i] = 0;
                     }
                 }
             }
             if (receivedDataString.substring(0, 3).equals("019")) { // 건반 눌렀을 때
                 if(isKeyOn[pitch]==2)
                     PlayNote.noteOff(spools, keys[pitch]);
                 PlayNote.noteOn(spools, keys[pitch], velocity);
                 isKeyOn[pitch]=1;
             } else if (receivedDataString.substring(0, 3).equals("018")) { //건반 뗐을 때
                 if(pedalFlag==0) {
                     PlayNote.noteOff(spools, keys[pitch]);
                     isKeyOn[pitch]=0;
                 }
                 else if(pedalFlag==1) {

                     isKeyOn[pitch] = 2;  //페달 밟은 상태로 건반 뗐을 때
                 }
             }
             flag[pitch]=1;
        }
        else
            flag[pitch]=0;
    }

    public static int getPitch(String receivedDataString){
        return Integer.parseInt(receivedDataString.substring(4,6), 16)-21;   // 미디신호상으로 hex코드 15 (10진수 21)이 가장 낮은 음이므로 21을 빼야함
    }
    public static float getVelocity(String receivedDataString){
        return (float)Integer.parseInt(receivedDataString.substring(6,8),16)/130;
    }


}
