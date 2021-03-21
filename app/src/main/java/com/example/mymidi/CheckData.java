package com.example.mymidi;

import android.media.SoundPool;

public class CheckData {

    public static void CheckNote(String receivedDataString, SoundPool[] spools, int[] keys){
        int pitch;
        float velocity;
        pitch=getPitch(receivedDataString);
        velocity=getVelocity(receivedDataString);
        if(receivedDataString.substring(0,3).equals("019")){
            PlayNote.noteOn(spools[pitch],keys[pitch],velocity);
        }
        else if(receivedDataString.substring(0,3).equals("018")){
            PlayNote.noteOff(spools[pitch], keys[pitch]);
        }
    }

    public static int getPitch(String receivedDataString){
        return Integer.parseInt(receivedDataString.substring(4,6), 16)-21;   // 미디신호상으로 hex코드 15 (10진수 21)이 가장 낮은 음이므로 21을 빼야함
    }
    public static float getVelocity(String receivedDataString){
        return (float)Integer.parseInt(receivedDataString.substring(6,8),16)/127;
    }


}
