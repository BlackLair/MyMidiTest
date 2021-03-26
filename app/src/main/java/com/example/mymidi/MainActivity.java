package com.example.mymidi;

import android.content.Context;
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




public class MainActivity extends AppCompatActivity {
    static final String TAG = "mymidi";
    MidiManager m;
    MidiOutputPort outputPort;
    TextView myData, tv_byte;
    Button btn;     //임시 버튼

    int testValue=0;


    static SoundPool spools;  // spool은 건반 1개당 1개씩 필요하므로 실제론 88개가 필요함.<배열로 정의 가능
    static int keys[];          // spool에 할당된 오디오 파일을 구분할 키값
    static Context context;
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myData=(TextView)findViewById(R.id.myData);
   //     btn=(Button)findViewById(R.id.btn);             // 임시_누를 때마다 마지막 신호를 보여줄 버튼
        tv_byte=(TextView)findViewById(R.id.tv_byte);       //임시_미디 신호에서 파싱한 값을 보여줄 텍스트뷰
        m=(MidiManager)getApplicationContext().getSystemService(Context.MIDI_SERVICE);
        MidiDeviceInfo[] infos=m.getDevices();
        context=getApplicationContext();
        btn=findViewById(R.id.btn);




        keys= new int[88];

        spools=SoundSelector.load(keys,0,context );


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

                                    outputPort=device.openOutputPort(0);    // 미디 아웃 포트 열기
                                    outputPort.connect(new MyReceiver());       // 포트로 리시버 연결
                                    new Thread(new Runnable(){/////////////////////(중요) 쓰레드 내에서 UI 작업하는 기능 !!!!!!!!!
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable(){
                                                @Override
                                                public void run(){
                                                    btn.setText("Device connected");
                                                }
                                            });
                                        }
                                    }).start();  ///////////////////////////////////////////////////////////////////////

                                }
                            }
                        }, new Handler(Looper.getMainLooper())  // 장치가 연결된 동안 항상 동작하도록 쓰레드 작동
                );

            }
            public void onDeviceRemoved( MidiDeviceInfo info ) { // 장치 연결 해제 알림
                Toast.makeText(getApplicationContext(),"device removed", Toast.LENGTH_SHORT).show();

               new Thread(new Runnable(){/////////////////////(중요) 쓰레드 내에서 UI 작업하는 기능 !!!!!!!!!
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                btn.setText("Device Disconnected : "+testValue);
                                testValue+=1;
                            }
                        });
                    }
                }).start();  ///////////////////////////////////////////////////////////////////////

            }
        }, Handler.createAsync(Looper.getMainLooper()));  // createAsync는 API 28 필요
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {   // 테스트 코드.  버튼을 누르면 마지막으로 받은 미디 신호를 hex코드로 출력(정렬안됨)

                tv_byte.setText(CheckData.testString);
            }
        });
    }
    public static Context getAppContext(){
        return MainActivity.context;
    }

}
