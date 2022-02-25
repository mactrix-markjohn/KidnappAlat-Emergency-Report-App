package com.example.kidnappalat.Services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.example.kidnappalat.BroadcastReceivers.RestarterBroadcast;
import com.example.kidnappalat.BroadcastReceivers.SMSReceivers;
import com.example.kidnappalat.BroadcastReceivers.VolumeReceiver;

public class VolumeService extends Service {
    public VolumeService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();


        VolumeReceiver volumeReceiver = new VolumeReceiver();
        registerReceiver(volumeReceiver,new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));


        SMSReceivers smsReceivers = new SMSReceivers();
        registerReceiver(smsReceivers,new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));




    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sendBroadcast(new Intent(this, RestarterBroadcast.class));


    }
}
