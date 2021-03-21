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
    Button btn;     //임시 버튼

    int[] midiData = new int[5];String receivedDataString;

    SoundPool spool, spool2;  // spool은 건반 1개당 1개씩 필요하므로 실제론 88개가 필요함.<배열로 정의 가능
    int key, key2;          // spool에 할당된 오디오 파일을 구분할 키값

    final protected static char[] hexArray="0123456789ABCDEF".toCharArray();
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myData=(TextView)findViewById(R.id.myData);
        btn=(Button)findViewById(R.id.btn);             // 임시_누를 때마다 마지막 신호를 보여줄 버튼
        tv_byte=(TextView)findViewById(R.id.tv_byte);       //임시_미디 신호에서 파싱한 값을 보여줄 텍스트뷰
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
                Toast.makeText(getApplicationContext(),"device connected:"+manufacturer, Toast.LENGTH_SHORT).show();    //장치가 연결되었을 때 연결된 장치이름을 알림
                m.openDevice(info, new MidiManager.OnDeviceOpenedListener(){        //장치 열기
                            @Override
                            public void onDeviceOpened(MidiDevice device){  // 장치와의 통신이 열렸을 때 수행
                                if(device==null){
                                    Log.e(TAG, "Could not open device"+info);
                                }else{
                                    class MyReceiver extends MidiReceiver {     //리시버 클래스
                                        public void onSend(byte[] data, int offset, int count, long timestamp) throws IOException{
                                            StringBuilder sb=new StringBuilder();
                                            for( byte b : data)
                                                sb.append(String.format("%02x", b&0xff));           // 받은 데이터를 헥스 문자열로 변환
                                            receivedDataString=sb.toString().substring(0,20);       // 문자열 뒤쪽 필요없는 데이터 거름
                                            if(receivedDataString.substring(0,3).equals("019")) { // 임시_입력받은 데이터가 건반 누름 신호인지 확인 // 소리 재생 테스트!!!
                                                if(receivedDataString.substring(4,6).equals("3c"))  // 가온 도를 눌렀을 경우
                                                    spool.play(key,1,1,0,0,1);
                                                else                    //  도 아닐경우
                                                    spool2.play(key2, 1, 1, 0, 0, 1);
                                            }
                                            else if(receivedDataString.substring(0,3).equals("018")){  // 입력받은 데이터가 건반을 떼는 신호인지 확인
                                                spool.autoPause();  // 같은 spool을 다른 건반에 사용하면 두 건반을 누르고 한 건반만 떼도 둘 다 소리가 멈추므로 건반별로 spool을 생성해야함
                                            }
                                        }
                                    }
                                    outputPort=device.openOutputPort(0);    // 미디 아웃 포트 열기
                                    outputPort.connect(new MyReceiver());       // 포트로 리시버 연결
                                }
                            }
                        }, new Handler(Looper.getMainLooper())  // 장치가 연결된 동안 항상 동작하도록 쓰레드 작동
                );

            }
            public void onDeviceRemoved( MidiDeviceInfo info ) { // 장치 연결 해제 알림
                Toast.makeText(getApplicationContext(),"device removed", Toast.LENGTH_SHORT).show();

            }
        }, Handler.createAsync(Looper.getMainLooper()));  // createAsync는 API 28 필요

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {   // 테스트 코드.  버튼을 누르면 마지막으로 받은 미디 신호를 hex코드로 출력(정렬안됨)
                String myText;
                myText=receivedDataString;
                tv_byte.setText(myText);
            }
        });
    }


}
