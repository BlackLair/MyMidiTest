package com.example.mymidi;

import android.media.SoundPool;

import java.util.Stack;

public class CheckData {
    static int pedalFlag=0;           // 0 : 페달 뗌   1 : 페달 밟음
    static int[] isKeyOn=new int[89];  // 0 : 건반 뗀 상태 1 : 건반 누른 상태  2 : 페달 밟는 도중에 건반 뗀 상태
    static int initFlag=0;
    static Stack<Integer> relNote = new Stack<>();
    static String testString; // test 버튼 출력용


    public static void CheckNote(String receivedDataString, SoundPool spools, int[] keys){
        int pitch;
        float velocity;

        if(initFlag==0){
            for(int i=0; i<89; i++)
                isKeyOn[i]=0;
            initFlag=1;
        }

        pitch=getPitch(receivedDataString);
        velocity=getVelocity(receivedDataString);
        velocity=velocity * (velocity+(float)0.1); // 일정 볼륨 이상은 소리크기가 비슷비슷해져서 2차함수 그래프 형식으로 볼륨크기 조절
        testString=receivedDataString; // 테스트 출력용


        if(receivedDataString.substring(0,3).equals("01b")) {   //페달 신호
            if (receivedDataString.substring(0, 8).equals("01b0407f")) { // 페달 밟았을 때
                pedalFlag = 1;
            } else if (receivedDataString.substring(0, 8).equals("01b04000")) { //페달 뗐을 때
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

        else {                      //건반 신호
            if (pitch < 88 && pitch >=0) {
                if (receivedDataString.substring(0, 3).equals("019")) { // 건반 눌렀을 때
                    if (isKeyOn[pitch] == 2) {
                        PlayNote.noteOff(spools, keys[pitch]);
                        isKeyOn[pitch] = 0;
                    }
                    if (velocity == 0) {       // 건반을 놓을 때 off 신호가 아닌 on신호와 velocity 0을 이용하는 건반을 위한 로직
                        PlayNote.noteOff(spools, keys[pitch]);
                        isKeyOn[pitch] = 0;
                    } else if (isKeyOn[pitch] == 0) {
                        PlayNote.noteOn(spools, keys[pitch], velocity);
                        isKeyOn[pitch] = 1;

                    }

                } else if (receivedDataString.substring(0, 3).equals("018")) { //건반 뗐을 때
                    if (pedalFlag == 0) {
                        PlayNote.noteOff(spools, keys[pitch]);
                        isKeyOn[pitch] = 0;
                    } else if (pedalFlag == 1) {
                        isKeyOn[pitch] = 2;  //페달 밟은 상태로 건반 뗐을 때
                        relNote.push(pitch);
                    }
                }
            }
        }

    }

    public static int getPitch(String receivedDataString){
        return Integer.parseInt(receivedDataString.substring(4,6), 16)-21;   // 미디신호상으로 hex코드 15 (10진수 21)이 가장 낮은 음이므로 21을 빼야함
    }
    public static float getVelocity(String receivedDataString){
        return (float)Integer.parseInt(receivedDataString.substring(6,8),16)/130;
    }

}
