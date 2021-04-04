package com.example.mymidi;

import android.app.Activity;
import android.content.Context;
import android.media.SoundPool;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.mymidi.MainActivity.context_main;
public class MyDeviceCallback extends MidiManager.DeviceCallback {
    MidiManager m=MainActivity.m;
    MidiOutputPort outputPort;
    Button btn;     //임시 버튼
    int testValue=0;
    private Activity activity;
    MidiDevice myDevice;
    MyReceiver receiver;
    int isConnected=0;
    public MyDeviceCallback(Activity activity){
        super();
        this.activity=activity;
    }
    public void onDeviceAdded( MidiDeviceInfo info ) {
        btn=activity.findViewById(R.id.btn);
        Bundle properties = info.getProperties();       // 연결된 장치의 정보를 저장할 멤버
        String manufacturer = properties.getString(MidiDeviceInfo.PROPERTY_NAME);   // 연결된 장치의 이름을 가져옴
        Toast.makeText(context_main,"device connected:"+manufacturer, Toast.LENGTH_SHORT).show();    //장치가 연결되었을 때 연결된 장치이름을 알림
        m.openDevice(info, new MidiManager.OnDeviceOpenedListener(){        //장치 열기
                    @Override
                    public void onDeviceOpened(MidiDevice device){  // 장치와의 통신이 열렸을 때 수행
                        if(device==null){
                            Log.e(MainActivity.TAG, "Could not open device"+info);
                        }else{
                            isConnected=1;
                            receiver=new MyReceiver(activity);
                            myDevice=device;
                            outputPort=device.openOutputPort(0);    // 미디 아웃 포트 열기
                            outputPort.connect(receiver);       // 포트로 리시버 연결
                            new Thread(new Runnable(){/////////////////////(중요) 쓰레드 내에서 UI 작업하는 기능 !!!!!!!!!
                                @Override
                                public void run() {
                                    activity.runOnUiThread(new Runnable(){
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
    @Override
    public void onDeviceRemoved( MidiDeviceInfo info ) { // 장치 연결 해제 알림
        btn=activity.findViewById(R.id.btn);
        isConnected=0;
        Toast.makeText(context_main,"device removed", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable(){/////////////////////(중요) 쓰레드 내에서 UI 작업하는 기능 !!!!!!!!!
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        btn.setText("Device Disconnected : "+testValue);
                        testValue+=1;
                    }
                });
            }
        }).start();  ///////////////////////////////////////////////////////////////////////

    }
    public void disConnect(){
        if(isConnected==1) //장치가 연결되어있었을 때만 연결 끊기 시도
            outputPort.onDisconnect(receiver);
    }

}
