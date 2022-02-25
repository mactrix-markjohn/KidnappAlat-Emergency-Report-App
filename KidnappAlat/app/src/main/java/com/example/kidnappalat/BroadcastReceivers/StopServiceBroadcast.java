package com.example.kidnappalat.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.kidnappalat.Services.SMSDistressService;
import com.example.kidnappalat.Services.VideoFeedService;

public class StopServiceBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        context.stopService(new Intent(context, SMSDistressService.class));
        context.stopService(new Intent(context, VideoFeedService.class));
    }
}
