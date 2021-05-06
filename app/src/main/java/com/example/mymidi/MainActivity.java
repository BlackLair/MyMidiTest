package com.example.mymidi;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.media.midi.MidiReceiver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.mymidi.CheckData.isKeyOn;


public class MainActivity extends AppCompatActivity {
    static final String TAG = "mymidi";
    static MidiManager m;
    MidiOutputPort outputPort;
    TextView myData, tv_byte;
    public Button btn, btn2;     //임시 버튼
    public static Context context_main;
    Button stopthread;

    static SoundPool spools;  // spool은 건반 1개당 1개씩 필요하므로 실제론 88개가 필요함.<배열로 정의 가능
    static int keys[];          // spool에 할당된 오디오 파일을 구분할 키값
    AtomicBoolean flag=new AtomicBoolean();
    Thread a;
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myData=(TextView)findViewById(R.id.myData);

        tv_byte=(TextView)findViewById(R.id.tv_byte);       //임시_미디 신호에서 파싱한 값을 보여줄 텍스트뷰
        m=(MidiManager)getApplicationContext().getSystemService(Context.MIDI_SERVICE);
        MidiDeviceInfo[] infos=m.getDevices();
        context_main=getApplicationContext();
        btn=findViewById(R.id.btn);
        btn2=findViewById(R.id.btn_unregister);
        context_main=this;
        keys= new int[88];

        for(int i=0; i<89; i++)
            isKeyOn[i]=0;


        flag.set(true);
        spools=SoundSelector.load(keys,0,context_main );
        stopthread=findViewById(R.id.stopthread);
        stopthread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag.set(false);
            }
        });
        MyDeviceCallback myMidiCallback = new MyDeviceCallback(this); // 콜백 클래스 생성
        m.registerDeviceCallback(myMidiCallback,Handler.createAsync(Looper.getMainLooper()) ); // 장치 연결시작

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {   // 테스트 코드.  버튼을 누르면 마지막으로 받은 미디 신호를 hex코드로 출력(정렬안됨)
                        tv_byte.setText(CheckData.testString);
                        flag.set(true);
                        a=new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while(true){
                                    int i=spools.play(keys[30],1,1,0,0,1);
                                    try {
                                        Thread.sleep(545);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    spools.stop(i);
                                    if(flag.get()==false)
                                        break;
                                }
                            }
                        });
                        a.start();
                    }
                });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context_main, "디바이스 콜백 해제", Toast.LENGTH_SHORT).show();
                m.unregisterDeviceCallback(myMidiCallback); // 디바이스 콜백 해제
                SoundSelector.unLoad(spools, keys, 0, context_main);
                myMidiCallback.disConnect();
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    @Override
    protected void onDestroy() {
        flag.set(false);
        super.onDestroy();
    }

    public static Context getAppContext(){
        return MainActivity.context_main;
    }

}
