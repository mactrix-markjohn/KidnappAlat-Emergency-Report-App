package com.example.kidnappalat.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Toast;

import com.example.kidnappalat.BroadcastReceivers.ServiceBroadcast;
import com.example.kidnappalat.BroadcastReceivers.StopServiceBroadcast;
import com.example.kidnappalat.Model.SharedPref;
import com.example.kidnappalat.R;

import java.util.Timer;
import java.util.TimerTask;

public class VolumeReceiver extends BroadcastReceiver {

    int volumePrev = 0;
    private SharedPref volUpPref;
    private SharedPref voldownpref;
    long countertime = 0;
    int upcount = 0;
    int downcount = 0;

    private SharedPref volUpPrf;
    private SharedPref voldownprf;
    int volumeP = 0;
    int upc = 0;
    int downc = 0;
    long counttime = 0;

    Context context;
    private SharedPref runpref;
    private SharedPref preVolumePref;
    private SharedPref volumeactivePref;
    private int n;
    private int m;
    private int i;
    private SharedPref volupactivepref;
    private int prevolume;
    private int volume;
    private SharedPref volumezero;
    private int c;
    private int v;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        beginDistress(context, intent);

        /*volume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE",0);

        volumezero = new SharedPref(context,context.getString(R.string.voldownpref));

        if(volume == 5){

            int zeros = volumezero.getInt();
            zeros ++;
            volumezero.setInt(zeros);

            Toast.makeText(context, "UP : "+zeros, Toast.LENGTH_SHORT).show();
            //clearAbortBroadcast();

        }*/


    }

    private void beginDistress(Context context, Intent intent) {
        volume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE",0);
        // Toast.makeText(context, ""+volume, Toast.LENGTH_SHORT).show();
        volumeactivePref = new SharedPref(context, context.getString(R.string.volumeactivepref));

        volumezero = new SharedPref(context,context.getString(R.string.voldownpref));
        n = 20;
        v = 3;


        // if the activation is false
        if(volumeactivePref.getInt() == 0){


            // if the volume number is zero, then count is incremented
            if(volume == v){

                int zeros = volumezero.getInt();
                zeros ++;
                volumezero.setInt(zeros);

                Toast.makeText(context, "UP : "+zeros, Toast.LENGTH_SHORT).show();
            }


            // if the number of count is equal to n - 1, then the servicebroadcast is started
            int zeros = volumezero.getInt();
            if(zeros == (n-1)){

                context.sendBroadcast(new Intent(context, ServiceBroadcast.class));



                volumeactivePref.setInt(-1);
                new Handler().postDelayed(() -> volumeactivePref.setInt(1),10000);

                volumezero.setInt(0);

            }

            // The whole task is to be done in 10s afterwhich everything will reset

            if(zeros == 1) {

                new Handler().postDelayed(() -> {

                    volumezero.setInt(0);
                    Toast.makeText(context, "UP_REEST ", Toast.LENGTH_SHORT).show();


                }, 30000);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {

                        volumezero.setInt(0);
                        //new Handler().post(() -> Toast.makeText(context, "DOWN_REEST ", Toast.LENGTH_SHORT).show());

                    }
                }, 30000);
            }




        }else if (volumeactivePref.getInt() == 1) {

            // if the volume number is zero, then count is incremented
            if(volume == v){

                int zeros = volumezero.getInt();
                zeros ++;
                volumezero.setInt(zeros);

                Toast.makeText(context, "DOWN : "+zeros, Toast.LENGTH_SHORT).show();
            }


            // if the number of count is equal to n - 1, then the servicebroadcast is started
            int zeros = volumezero.getInt();
            if(zeros == (n-1)){

                context.stopService(new Intent(context,ServiceBroadcast.class));



                volumeactivePref.setInt(-1);
                new Handler().postDelayed(() -> volumeactivePref.setInt(0),10000);

                volumezero.setInt(0);

            }

            // The whole task is to be done in 10s afterwhich everything will reset
            if (zeros == 1) {
                new Handler().postDelayed(() -> {

                    volumezero.setInt(0);
                    Toast.makeText(context, "DOWN_REEST ", Toast.LENGTH_SHORT).show();


                },30000);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {

                        volumezero.setInt(0);

                        //new Handler().post(() -> Toast.makeText(context, "DOWN_REEST ", Toast.LENGTH_SHORT).show());

                    }
                },30000);
            }


        }
    }

    private void startupdestress(Context context, Intent intent) {
        runpref = new SharedPref(context,context.getString(R.string.runpref));
        preVolumePref = new SharedPref(context,context.getString(R.string.prevolumepref));
        volumeactivePref = new SharedPref(context,context.getString(R.string.volumeactivepref));

        volUpPref = new SharedPref(context,context.getString(R.string.voluppref));
        voldownpref = new SharedPref(context,context.getString(R.string.voldownpref));

        //Toast.makeText(context, "It is volume working", Toast.LENGTH_SHORT).show();

        n = 4;
        m = 3;
        volume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE",0);


        // check for volume Up and Down

        if(!volumeactivePref.getBoolean()) {

            prevolume = preVolumePref.getInt();

            if (volume > prevolume) {

                // Volume Up button

                int up = volUpPref.getInt();

                up++;
                volUpPref.setInt(up);
                Toast.makeText(context, ""+up, Toast.LENGTH_SHORT).show();

                Toast.makeText(context, "up", Toast.LENGTH_SHORT).show();


            } else if (volume < prevolume) {

                // Volume down button

                int down = voldownpref.getInt();
                ++ down;
                voldownpref.setInt(down);
                Toast.makeText(context, "down", Toast.LENGTH_SHORT).show();
                // Toast.makeText(context, ""+down, Toast.LENGTH_SHORT).show();
            }



            // check to see if the volume up and down is greater than 3
            int up = volUpPref.getInt();
            int down = voldownpref.getInt();
            if(up == (n-1) || down == (n-1)){


                // activate the volume distress pattern
                volumeactivePref.setBoolean(true);

            }

        }

        // set up the timer to reset the count to zero if user spends more that


        i = 0;


        CountDownTimer countDownTimer = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {


                if(millisUntilFinished == 0){

                    volUpPref.setInt(0);
                    voldownpref.setInt(0);
                    volumeactivePref.setBoolean(false);
                }

            }

            @Override
            public void onFinish() {

            }
        };

        countDownTimer.start();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {


                volUpPref.setInt(0);
                voldownpref.setInt(0);
                volumeactivePref.setBoolean(false);

            }
        },0,20000);


        // once the activation is true , then check to see if the up volume is presses (m - 1) time

        if(volumeactivePref.getBoolean()) {

            prevolume = preVolumePref.getInt();
            volupactivepref = new SharedPref(context,context.getString(R.string.volupactivepref));

            if (volume > prevolume) {

                // Volume Up button

                int up = volumeactivePref.getInt();
                up++;
                volumeactivePref.setInt(up);


            }

            int up = volumeactivePref.getInt();

            if(up == (m-1)){


                // start the servicebroadcast broadcast receiver
                context.sendBroadcast(new Intent(context,ServiceBroadcast.class));
                Toast.makeText(context, "Started the Service Broadcast", Toast.LENGTH_SHORT).show();


            }



        }


      /*  if (runpref.getBoolean()){


            stopdistress(context,intent);


        }else{

            startdistress(context, intent);


        }


*/

        preVolumePref.setInt(volume);
        this.context = context;
    }

    private void startdistress(final Context context, Intent intent) {



        // this increment the up and down counter by one,when the user click on the up and down volume

        if ("android.media.VOLUME_CHANGED_ACTION".equals(intent.getAction())){

            int volume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE",0);


            volumePrev = volume;

        }






        // if the count down reaches zero then the up and down volume count goes back to zero

        CountDownTimer countDownTimer = new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                countertime = millisUntilFinished/1000;

                Toast.makeText(context, ""+countertime/1000, Toast.LENGTH_SHORT).show();

                if(countertime == 1){

                    upcount = volUpPref.getInt();
                    upcount++;
                    volUpPref.setInt(upcount);
                }


            }

            @Override
            public void onFinish() {



            }
        };

        countDownTimer.start();




        int upcounter = volUpPref.getInt();
        int downcounter = voldownpref.getInt();

        // check if the up and down count is greater then 3 and 2 respectively after which we take it back to zero

        if(upcounter >= 5 || downcounter >= 5 ){


            Toast.makeText(context, "We start the Service Broadcast", Toast.LENGTH_SHORT).show();
            context.sendBroadcast(new Intent(context,ServiceBroadcast.class));
            voldownpref.setInt(0);
            volUpPref.setInt(0);

            runpref.setBoolean(true);
        }

    }

    private void stopdistress(Context context, Intent intent) {
        // create a shared Preference the hold the previous state of the broadcast receiver

        volUpPrf = new SharedPref(context,context.getString(R.string.voluppre));
        voldownprf = new SharedPref(context,context.getString(R.string.voldownpre));


        // this increment the up and down counter by one,when the user click on the up and down volume

        if ("android.media.VOLUME_CHANGED_ACTION".equals(intent.getAction())){

            int volume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE",0);



            volumeP = volume;

        }


        // if the count down reaches zero then the up and down volume count goes back to zero

        CountDownTimer countDownTimer = new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                counttime = millisUntilFinished/1000;

                if(counttime == 1){
                    upc = volUpPrf.getInt();
                    upc++;
                    volUpPrf.setInt(upc);


                }

            }

            @Override
            public void onFinish() {



            }
        };

        countDownTimer.start();


        // check if the up and down count is greater then 3 and 2 respectively after which we take it back to zero

        int upcounter = volUpPrf.getInt();
        int downcounter = voldownprf.getInt();

        if(downcounter >= 10 || upcounter >= 10 ){


            Toast.makeText(context, "We stop the Stop Service Broadcast", Toast.LENGTH_SHORT).show();
            context.sendBroadcast(new Intent(context, StopServiceBroadcast.class));
            voldownprf.setInt(0);
            volUpPrf.setInt(0);

            runpref.setBoolean(false);
        }


    }



}
