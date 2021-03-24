package com.example.mymidi;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;

public class SoundSelector extends AppCompatActivity {
    static int myIds[]= new int[89];;
    public static SoundPool load(int[] keys, int selected, Context context) {      // selected 0 : piano 1 : violin
        SoundPool spools;

        for (int i = 0; i < 88; i++) {

            if(i<9)
                myIds[i]=context.getResources().getIdentifier("@raw/p"+"0"+Integer.toString(i+1),"raw",context.getPackageName());
            else
                myIds[i]=context.getResources().getIdentifier("@raw/p"+Integer.toString(i+1),"raw",context.getPackageName());
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {               // API 21 이후
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

                spools = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(256).build();
        } else {                // API 21 미만
                spools = new SoundPool(256, AudioManager.STREAM_NOTIFICATION, 0);
        }




        if(selected==0) { // key값에 spools[i].load 할당
            for (int i = 0; i < 88; i++)
                keys[i] = spools.load(context, myIds[i], 1);
        }
 /*           keys[0] = spools[0].load(context, R.raw.p01, 1);
            keys[1] = spools[1].load(context, R.raw.p02, 1);
            keys[2] = spools[2].load(context, R.raw.p03, 1);
            keys[3] = spools[3].load(context, R.raw.p04, 1);
            keys[4] = spools[4].load(context, R.raw.p05, 1);
            keys[5] = spools[5].load(context, R.raw.p06, 1);
            keys[6] = spools[6].load(context, R.raw.p07, 1);
            keys[7] = spools[7].load(context, R.raw.p08, 1);
            keys[8] = spools[8].load(context, R.raw.p09, 1);
            keys[9] = spools[9].load(context, R.raw.p10, 1);
            keys[10] = spools[10].load(context, R.raw.p11, 1);
            keys[11] = spools[11].load(context, R.raw.p12, 1);
            keys[12] = spools[12].load(context, R.raw.p13, 1);
            keys[13] = spools[13].load(context, R.raw.p14, 1);
            keys[14] = spools[14].load(context, R.raw.p15, 1);
            keys[15] = spools[15].load(context, R.raw.p16, 1);
            keys[16] = spools[16].load(context, R.raw.p17, 1);
            keys[17] = spools[17].load(context, R.raw.p18, 1);
            keys[18] = spools[18].load(context, R.raw.p19, 1);
            keys[19] = spools[19].load(context, R.raw.p20, 1);
            keys[20] = spools[20].load(context, R.raw.p21, 1);
            keys[21] = spools[21].load(context, R.raw.p22, 1);
            keys[22] = spools[22].load(context, R.raw.p23, 1);
            keys[23] = spools[23].load(context, R.raw.p24, 1);
            keys[24] = spools[24].load(context, R.raw.p25, 1);
            keys[25] = spools[25].load(context, R.raw.p26, 1);
            keys[26] = spools[26].load(context, R.raw.p27, 1);
            keys[27] = spools[27].load(context, R.raw.p28, 1);
            keys[28] = spools[28].load(context, R.raw.p29, 1);
            keys[29] = spools[29].load(context, R.raw.p30, 1);
            keys[30] = spools[30].load(context, R.raw.p31, 1);
            keys[31] = spools[31].load(context, R.raw.p32, 1);
            keys[32] = spools[32].load(context, R.raw.p33, 1);
            keys[33] = spools[33].load(context, R.raw.p34, 1);
            keys[34] = spools[34].load(context, R.raw.p35, 1);
            keys[35] = spools[35].load(context, R.raw.p36, 1);
            keys[36] = spools[36].load(context, R.raw.p37, 1);
            keys[37] = spools[37].load(context, R.raw.p38, 1);
            keys[38] = spools[38].load(context, R.raw.p39, 1);
            keys[39] = spools[39].load(context, R.raw.p40, 1);
            keys[40] = spools[40].load(context, R.raw.p41, 1);
            keys[41] = spools[41].load(context, R.raw.p42, 1);
            keys[42] = spools[42].load(context, R.raw.p43, 1);
            keys[43] = spools[43].load(context, R.raw.p44, 1);
            keys[44] = spools[44].load(context, R.raw.p45, 1);
            keys[45] = spools[45].load(context, R.raw.p46, 1);
            keys[46] = spools[46].load(context, R.raw.p47, 1);
            keys[47] = spools[47].load(context, R.raw.p48, 1);
            keys[48] = spools[48].load(context, R.raw.p49, 1);
            keys[49] = spools[49].load(context, R.raw.p50, 1);
            keys[50] = spools[50].load(context, R.raw.p51, 1);
            keys[51] = spools[51].load(context, R.raw.p52, 1);
            keys[52] = spools[52].load(context, R.raw.p53, 1);
            keys[53] = spools[53].load(context, R.raw.p54, 1);
            keys[54] = spools[54].load(context, R.raw.p55, 1);
            keys[55] = spools[55].load(context, R.raw.p56, 1);
            keys[56] = spools[56].load(context, R.raw.p57, 1);
            keys[57] = spools[57].load(context, R.raw.p58, 1);
            keys[58] = spools[58].load(context, R.raw.p59, 1);
            keys[59] = spools[59].load(context, R.raw.p60, 1);
            keys[60] = spools[60].load(context, R.raw.p61, 1);
            keys[61] = spools[61].load(context, R.raw.p62, 1);
            keys[62] = spools[62].load(context, R.raw.p63, 1);
            keys[63] = spools[63].load(context, R.raw.p64, 1);
            keys[64] = spools[64].load(context, R.raw.p65, 1);
            keys[65] = spools[65].load(context, R.raw.p66, 1);
            keys[66] = spools[66].load(context, R.raw.p67, 1);
            keys[67] = spools[67].load(context, R.raw.p68, 1);
            keys[68] = spools[68].load(context, R.raw.p69, 1);
            keys[69] = spools[69].load(context, R.raw.p70, 1);
            keys[70] = spools[70].load(context, R.raw.p71, 1);
            keys[71] = spools[71].load(context, R.raw.p72, 1);
            keys[72] = spools[72].load(context, R.raw.p73, 1);
            keys[73] = spools[73].load(context, R.raw.p74, 1);
            keys[74] = spools[74].load(context, R.raw.p75, 1);
            keys[75] = spools[75].load(context, R.raw.p76, 1);
            keys[76] = spools[76].load(context, R.raw.p77, 1);
            keys[77] = spools[77].load(context, R.raw.p78, 1);
            keys[78] = spools[78].load(context, R.raw.p79, 1);
            keys[79] = spools[79].load(context, R.raw.p80, 1);
            keys[80] = spools[80].load(context, R.raw.p81, 1);
            keys[81] = spools[81].load(context, R.raw.p82, 1);
            keys[82] = spools[82].load(context, R.raw.p83, 1);
            keys[83] = spools[83].load(context, R.raw.p84, 1);
            keys[84] = spools[84].load(context, R.raw.p85, 1);
            keys[85] = spools[85].load(context, R.raw.p86, 1);
            keys[86] = spools[86].load(context, R.raw.p87, 1);
            keys[87] = spools[87].load(context, R.raw.p88, 1);*/

             return spools;
        }


    public static void unLoad(SoundPool[] spools, int[] keys, int selected, Context context){
        for(int i=0; i<88; i++){
            spools[i].release();
            spools[i]=null;
        }
    }
}
