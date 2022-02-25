package com.example.kidnappalat.BroadcastReceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class ReBootAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");


        context.sendBroadcast(new Intent(context, RestarterBroadcast.class));


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context,RestarterBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,alarmIntent,0);


        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+AlarmManager.INTERVAL_FIFTEEN_MINUTES,AlarmManager.INTERVAL_HALF_DAY,pendingIntent);
        }
    }
}
