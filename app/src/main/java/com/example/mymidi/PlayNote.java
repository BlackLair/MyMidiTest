package com.example.mymidi;

import android.app.Activity;
import android.content.Context;
import android.media.SoundPool;

public class PlayNote {
    static int[] streamID= new int[89];
    public static void noteOn(SoundPool spool, int key, float velocity){
        streamID[key]=spool.play(key, velocity, velocity, 0, 0, 1);
    }
    public static void noteOff(SoundPool spool, int key){

        spool.stop(streamID[key]);
    }
}
