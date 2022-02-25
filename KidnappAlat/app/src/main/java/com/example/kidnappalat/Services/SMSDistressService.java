package com.example.kidnappalat.Services;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.kidnappalat.BroadcastReceivers.ServiceBroadcast;
import com.example.kidnappalat.Model.Constants;
import com.example.kidnappalat.Model.GeoSQLLite;
import com.example.kidnappalat.Model.SharedPref;
import com.example.kidnappalat.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;

public class SMSDistressService extends Service {

    GoogleApiClient googleApiClient;
    GeoSQLLite emergencycontact;
    Cursor cursor;
    private SharedPref stopdistress;
    private SharedPref distressSigna;
    private MediaRecorder mediaRecorder;
    WindowManager windowManager;

    SharedPref sendphone;

    SharedPref isDistressOn;
    private SharedPref videocounter;
    private SharedPref videofeedsetting;
    private SharedPref stopvideorecording;
    private File filing;
    private SharedPref sharedversion;
    private SharedPref sharedfullname;
    private MyResultReceiver resultReceiver;
    private String geocode;
    private SharedPref distressServicePref;
    private SharedPref serviceCallControlPref;


    public SMSDistressService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }





    @Override
    public void onCreate() {
        super.onCreate();

        // SharedPreference to check whether DistressService should to stop or not
        stopdistress = new SharedPref(this,getString(R.string.stopdistress));
        distressSigna = new SharedPref(this,getString(R.string.DistressSigna));
        videocounter = new SharedPref(this,getString(R.string.videofeedcounter));
        stopvideorecording = new SharedPref(this,getString(R.string.stopvideorecording));
        videofeedsetting = new SharedPref(this,getString(R.string.videofeedsetting));
        sharedversion = new SharedPref(this,getString(R.string.sharedversion));
        sharedfullname = new SharedPref(this,getString(R.string.sharedfullname));
        distressServicePref = new SharedPref(this,getString(R.string.distressServicePref));// this permits the service to start itself with closed

        // this is to ensure that once the Service is turned off the process is surely turned off. Location call seems to run independent of the service.
        serviceCallControlPref = new SharedPref(this,getString(R.string.serviceCallControlPref));


        Toast.makeText(this, "SMS service checked", Toast.LENGTH_SHORT).show();

        // TODO: Result receiver class initialiation
        resultReceiver = new MyResultReceiver(new Handler());

        //mediaRecorder = new MediaRecorder();


        mediaRecorder = new MediaRecorder();
        //Toast.makeText(this, "SMS Distress Service is active", Toast.LENGTH_SHORT).show();


        // TODO: This is to enable the app to restart the service when it is force to stop
        stopdistress = new SharedPref(this,getString(R.string.stopdistress));

        // TODO: This is the notify the internet change receiver that distress service was started
        distressSigna = new SharedPref(this,getString(R.string.DistressSigna));

        // TODO: This contain the phone number of the external user
        sendphone = new SharedPref(this,getString(R.string.sendphone));

        // TODO: To keep track of whether the distress service is running
        isDistressOn = new SharedPref(this, getString(R.string.isDistressOn));
        isDistressOn.setInt(1);

        emergencycontact = new GeoSQLLite(this,getString(R.string.emergencycontactbase),null,1);
        cursor = emergencycontact.getAllGeofence();


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                        myLocation();


                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();

        googleApiClient.connect();




    }


    public void myLocation(){


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){


            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(200000)
                    .setFastestInterval(120000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {


                   /* // TODO: get the lat lng and get the Geocode
                    // get the lat and lng
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                    // Initilize the intent class for starting the intent service
                    Intent intent = new Intent(SMSDistressService.this,GeoCodeIntentService.class);
                    intent.putExtra(Constants.LOCATION_DATA_EXTRA,latLng);
                    intent.putExtra(Constants.RECEIVER,resultReceiver);
                    startService(intent);*/


                   if (distressServicePref.getBoolean()){


                       Toast.makeText(SMSDistressService.this, "Location checked", Toast.LENGTH_SHORT).show();

                       if(cursor != null){

                           for(int i = 0; i< cursor.getCount(); i++){
                               cursor.moveToPosition(i);

                               String phone = cursor.getString(cursor.getColumnIndex(GeoSQLLite.MESSAGE));
                               sendSMS(location,phone);



                           }

                       }
                   }



                    /*if(sendphone.getText() != null){
                        String phonenum = sendphone.getText();
                        sendSMS(location,phonenum);
                    }*/

                    // send sms to the hardcoded number in the program
                   // sendSMS(location,getString(R.string.centreemergencyno));





                }
            });



        }








    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        googleApiClient.reconnect();
    }

    public void sendSMS(Location location, String senderTel){
        Toast.makeText(this, "Send SMS checking", Toast.LENGTH_SHORT).show();
        String owner  = !sharedfullname.getText().equalsIgnoreCase("null") ? sharedfullname.getText() : "This User"  ;
        String matric = new SharedPref(this,getString(R.string.usermatric)).getText();
        String department = new SharedPref(this, getString(R.string.userdepartment)).getText();
        String work = new SharedPref(this, getString(R.string.userwork)).getText();

        String address = geocode != null ? "Address: "+geocode : " is in Danger. Get location from the link";

        String message = "" + owner + ", " + work + ", "+ matric +", "+ department +" is in Danger. Get location from the link";


        String scAddress = null;
        PendingIntent pendingIntent = null, deliveryIntent = null;


        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(senderTel, scAddress,"http://maps.google.com/maps?q="+location.getLatitude()+","+location.getLongitude()+" "+message,pendingIntent,deliveryIntent);

        Toast.makeText(this, "Send SMS checked", Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Sms is sent to "+senderTel, Toast.LENGTH_SHORT).show();

    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();

        if (distressServicePref.getBoolean())
            sendBroadcast(new Intent(this, ServiceBroadcast.class));


    }



   // Result Receiver class to receive the Geocode of a location
    class MyResultReceiver extends android.os.ResultReceiver {



        public MyResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            // if(resultCode == Constants.SUCCESS_RESULT){


            geocode = resultData != null ? resultData.getString(Constants.SENTLOCATION) : "Unknown";

            //epaddress.setText(result);





            // }






        }
    }

}
