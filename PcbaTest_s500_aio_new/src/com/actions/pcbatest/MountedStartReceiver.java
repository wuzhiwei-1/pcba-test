package com.actions.pcbatest;

import java.io.File;
import java.io.IOException;
import java.lang.String;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class MountedStartReceiver extends BroadcastReceiver {
    private String TAG = "MountedStartReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
         Log.v(TAG,"Intent.ACTION_MEDIA_MOUNTED");
       if (action.equals(Intent.ACTION_MEDIA_MOUNTED)){     
            autoStartPcba(context);
        }
    }
    private void autoStartPcba(Context context){
    	Log.v(TAG, "autoStartPcba");
        String uhostPaths[] = {"/storage/uhost2","/storage/uhost1/","/storage/uhost/",
                "/mnt/uhost2","/mnt/uhost1/","/mnt/uhost/"};
        for (String s : uhostPaths){
            File file = new File(s.concat("Actions/π314.test"));
            Log.v(TAG, "file.exists():"+file.exists());
            if (file.exists()){
                Log.i(TAG,"π 314 is exists !");
                Intent it = new Intent(context,PcbaTestActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
                break;
            }
        }
    }
}
