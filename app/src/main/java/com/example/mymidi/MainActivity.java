package com.example.mymidi;

import android.content.Context;
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

import java.io.IOException;

import static android.os.Looper.loop;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "mymidi";
    MidiManager m;
    int numOutputs;
    MidiOutputPort outputPort;
    TextView myData, tv_byte;
    Button btn;

    int[] midiData = new int[5];String receivedDataString;

    SoundPool spool, spool2;
    int key, key2;

    final protected static char[] hexArray="0123456789ABCDEF".toCharArray();
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myData=(TextView)findViewById(R.id.myData);
        btn=(Button)findViewById(R.id.btn);
        tv_byte=(TextView)findViewById(R.id.tv_byte);
        m=(MidiManager)getApplicationContext().getSystemService(Context.MIDI_SERVICE);
        MidiDeviceInfo[] infos=m.getDevices();


        spool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        spool2= new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        key = spool.load(this, R.raw.ride,1);
        key2= spool2.load(this, R.raw.crash, 1);


        m.registerDeviceCallback(new MidiManager.DeviceCallback() {      // 실시간으로 장치가 연결되거나 해제되면 알림
            public void onDeviceAdded( MidiDeviceInfo info ) {
                Bundle properties = info.getProperties();       // 연결된 장치의 정보를 저장할 멤버
                String manufacturer = properties.getString(MidiDeviceInfo.PROPERTY_NAME);   // 연결된 장치의 이름을 가져옴
                Toast.makeText(getApplicationContext(),"device connected:"+manufacturer, Toast.LENGTH_SHORT).show();
                m.openDevice(info, new MidiManager.OnDeviceOpenedListener(){        //장치 열기
                            @Override
                            public void onDeviceOpened(MidiDevice device){
                                if(device==null){
                                    Log.e(TAG, "Could not open device"+info);
                                }else{
                                    class MyReceiver extends MidiReceiver {     //리시버 클래스
                                        public void onSend(byte[] data, int offset, int count, long timestamp) throws IOException{
                                            StringBuilder sb=new StringBuilder();           // 받은 데이터를 헥스 문자열로 변환
                                            for( byte b : data)
                                                sb.append(String.format("%02x", b&0xff));
                                            receivedDataString=sb.toString().substring(0,20);
                                            if(receivedDataString.substring(0,3).equals("019")) { // 입력받은 데이터가 건반 누름 신호인지 확인
                                                // 소리 재생 테스트!!!
                                                if(receivedDataString.substring(4,6).equals("3c"))  //가온 도
                                                    spool.play(key,1,1,0,0,1);
                                                else                    //도 아닐경우
                                                    spool2.play(key2, 1, 1, 0, 0, 1);
                                            }
                                            else if(receivedDataString.substring(0,3).equals("018")){  // 입력받은 데이터가 건반 뗌 신호인지 확인
                                                spool.autoPause();
                                            }
                                        }
                                    }
                                    outputPort=device.openOutputPort(0);
                                    outputPort.connect(new MyReceiver());
                                }
                            }
                        }, new Handler(Looper.getMainLooper())
                );

            }
            public void onDeviceRemoved( MidiDeviceInfo info ) {
                Toast.makeText(getApplicationContext(),"device removed", Toast.LENGTH_SHORT).show();

            }
        }, Handler.createAsync(Looper.getMainLooper()));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myText;
                myText=receivedDataString;
                tv_byte.setText(myText);
            }
        });
    }


}
