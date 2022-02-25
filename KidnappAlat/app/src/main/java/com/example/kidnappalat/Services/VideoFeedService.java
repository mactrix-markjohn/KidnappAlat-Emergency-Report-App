package com.example.kidnappalat.Services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.kidnappalat.BroadcastReceivers.ServiceBroadcast;
import com.example.kidnappalat.Model.SharedPref;
import com.example.kidnappalat.R;

import java.io.File;
import java.io.IOException;

public class VideoFeedService extends Service implements SurfaceHolder.Callback {
    private WindowManager windowManager;
    private MediaRecorder mediaRecorder;
    private File filing;
    private SharedPref videopref;
    private SharedPref distressServicePref;


    public VideoFeedService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();


        mediaRecorder = new MediaRecorder();
        videopref = new SharedPref(VideoFeedService.this,getString(R.string.videofeedpref));
        distressServicePref = new SharedPref(this,getString(R.string.distressServicePref));





    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(videopref.getBoolean() && distressServicePref.getBoolean()){
            startVideoRecording();
        }


        return START_STICKY;
    }


    public void startVideoRecording(){

        try {


            windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
            SurfaceView surfaceView = new SurfaceView(this);
            //surfaceView.setVisibility(View.VISIBLE);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(1,1,WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT);
            layoutParams.gravity = Gravity.LEFT|Gravity.TOP;
            windowManager.addView(surfaceView,layoutParams);
            surfaceView.getHolder().addCallback(this);
            //surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        } catch (Exception e) {
            //e.printStackTrace();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
            }
            Toast.makeText(this, "Activate Draw Over Other Apps", Toast.LENGTH_SHORT).show();



        }




    }
    String videname = " ";



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {


        try {






            File file  = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/KidnappAlat");
            boolean makecheck = file.mkdir();

            //android.hardware.Camera camera = android.hardware.Camera.open();
            //camera.unlock();




            mediaRecorder.setPreviewDisplay(holder.getSurface());
            // mediaRecorder.setCamera(camera);

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);


            //mediaRecorder.setCamera(camera);


            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_CIF));
            filing = new File(file.getAbsolutePath()+"/videofeed"+mediaRecorder.hashCode()+".3gp");
            mediaRecorder.setOutputFile(filing.getAbsolutePath());

            // TODO : Video will record for 30s
            mediaRecorder.setMaxDuration(300000);
            //mediaRecorder.setMaxFileSize(5000000);
            // mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            //mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);



            try {
                mediaRecorder.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }


            // TODO: Start the Video Recording after 3s
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    mediaRecorder.start();

                    Toast.makeText(VideoFeedService.this, "Start", Toast.LENGTH_SHORT).show();

                    // TODO: Stop the Video Recording after 320s
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mediaRecorder.stop();
                            Toast.makeText(VideoFeedService.this, "Stop", Toast.LENGTH_SHORT).show();


                            // TODO: Start another Video Recording after 10s
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(videopref.getBoolean()){
                                        startVideoRecording();
                                    }
                                }
                            }, 10000);


                        }
                    }, 320000);
                    //mediaRecorder.stop();


                }
            }, 3000);

        }catch (IllegalStateException e){

            mediaRecorder = new MediaRecorder();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startVideoRecording();
                }
            }, 2000);

        }









    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (distressServicePref.getBoolean())
            sendBroadcast(new Intent(this, ServiceBroadcast.class));



    }
}
