package com.actions.pcbatest;


import android.nfc.INfcTag;
import android.os.Handler;
import android.os.Message;

import java.io.*;

/**
 * Created by zivy on 17-8-4.
 */
public class SerialPort {
    private static final String TAG = "SerialPort";
    private static final int PASS = 1;
    private static final int FAIL = 2;

    private FileDescriptor mFd;
    private InputStream mInputStream;
    private OutputStream mOutputStrem;
    private static final String SEND_DATA = "hello china";
    private boolean isRunning = true;
    private byte[] buffers = null;
    private SerialPortListener spListener;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case PASS:
                    spListener.updateSerialPort(true);
                    break;
                case FAIL:
                    spListener.updateSerialPort(false);
                    break;
            }
        }
    };

    public SerialPort(String path,int baudrate,SerialPortListener spListener) throws SecurityException,IOException{
        mFd = open(path,baudrate);
        this.spListener = spListener;
        if(mFd != null){
            mInputStream = new FileInputStream(mFd);
            mOutputStrem = new FileOutputStream(mFd);
        }
    }

    public void test(){
        if(mOutputStrem == null || mInputStream == null){
            return ;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isRunning){
                    try {
                        Thread.sleep(1000);
                        buffers = new byte[64];
                        mOutputStrem.write(SEND_DATA.getBytes("UTF-8"));
                        int i = mInputStream.read(buffers);
                        String str = new String(buffers,0,i,"UTF-8");
                        if(str.contains("hello")){
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }else{
                            Message message = new Message();
                            message.what = 2;
                            handler.sendMessage(message);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                }
            }
        }).start();

    }

    public void closeThread(){
        isRunning = false;
    }

    private native FileDescriptor open(String path,int baudrate);
    static{
        System.load("/system/SerialPortlib.so");
    }
}
