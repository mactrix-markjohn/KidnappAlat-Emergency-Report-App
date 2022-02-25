package com.example.kidnappalat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.example.kidnappalat.BroadcastReceivers.RestarterBroadcast;
import com.example.kidnappalat.BroadcastReceivers.ServiceBroadcast;
import com.example.kidnappalat.Services.VolumeService;
import com.example.kidnappalat.Model.SharedPref;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Introduction extends AppCompatActivity {


    private static final int REQUEST_CHECK_SETTINGS = 4353;
    TextView continueApp;
    ImageView distress;
    SharedPref stopdistress;
    SharedPref distressSigna;
    Button continuebutton;
    private LocationRequest highRequest;
    private LocationRequest balanceRequest;
    private double xlatitude;
    private double xlongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        // fix location coordinate to check

        xlatitude  =  7.444259300000001;
        xlongitude =  3.8994576999999997;



        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.SEND_SMS,Manifest.permission.RECEIVE_SMS,Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        }





        distressSigna = new SharedPref(this, getString(R.string.DistressSigna));
        stopdistress = new SharedPref(this,getString(R.string.stopdistress));



        continueApp = (TextView)findViewById(R.id.contineapp);
        distress = (ImageView)findViewById(R.id.distress);


        TextView distressText = (TextView)findViewById(R.id.distresstext);
        TextView kidnappalat = (TextView)findViewById(R.id.kidnappalat);


        continuebutton = (Button) findViewById(R.id.continuebutton);

        // disable the buttons until it is verified that the user is within the fixed location
       /* continueApp.setEnabled(false);
        continuebutton.setEnabled(false);
        distress.setEnabled(false);*/


        // get the user location and check if the user is within a particular region, if not close the application
        getLocation();


        continueApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                startActivity(new Intent(Introduction.this,KidnappAlat.class));
            }
        });

        continuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(new Intent(Introduction.this,KidnappAlat.class));


            }
        });


        distress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Continue other actitivities on Distress here....

                // if the Version is the Lite version
                distressSigna.setInt(1);
                stopdistress.setInt(0);
                //startService(new Intent(Introduction.this,SMSDistressService.class));
                sendBroadcast(new Intent(Introduction.this, ServiceBroadcast.class));







            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void getLocation(){

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            return;
        }

        balanceRequest = new LocationRequest();
        balanceRequest.setFastestInterval(10000);
        balanceRequest.setFastestInterval(5000);
        balanceRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        highRequest = new LocationRequest();
        highRequest.setInterval(10000);
        highRequest.setFastestInterval(5000);
        highRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(balanceRequest);
        builder.addLocationRequest(highRequest);
        builder.setNeedBle(true);


        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                LocationSettingsStates states = locationSettingsResponse.getLocationSettingsStates();
                if(states.isGpsUsable()){


                    addLocationservice(highRequest);

                }else{

                    addLocationservice(balanceRequest);
                }




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                ApiException ex = (ApiException)e;

                switch (ex.getStatusCode()){

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:


                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(Introduction.this,REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e1) {
                            // Ignore the error
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        addLocationservice(balanceRequest);

                        break;
                    default:

                        addLocationservice(balanceRequest);

                        break;


                }




            }
        });





    }

    private void addLocationservice(LocationRequest locationRequest) {

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            return;
        }

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // Here we collect the current user location every 10 seconds
                Location location = locationResult.getLastLocation();

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // calculate the distance between the current user location and the fix location
                float[] result = new float[2];
                Location.distanceBetween(xlatitude,xlongitude,latitude,longitude,result);

                // check to see that the user distane is less than or equal to 5500 meters

                if(result[0] <= 5500){

                    // if it is less that 5500 meter , then enable all the buttons
                    continueApp.setEnabled(true);
                    continuebutton.setEnabled(true);
                    distress.setEnabled(true);

                    // start service
                    startService(new Intent(Introduction.this, VolumeService.class));
                    setAlarm();


                }else{

                    Toast.makeText(Introduction.this, "Sorry, You are not within University of Ibadan, so you can not use this app", Toast.LENGTH_LONG).show();


                    finishAffinity();



                }







            }
        }, Looper.myLooper());





    }


    public void setAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, RestarterBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Introduction.this,0,alarmIntent,0);


        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+AlarmManager.INTERVAL_FIFTEEN_MINUTES,AlarmManager.INTERVAL_HALF_DAY,pendingIntent);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CHECK_SETTINGS) {

                final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
                if (states.isGpsUsable()) {
                    addLocationservice(highRequest);

                }else{

                    addLocationservice(balanceRequest);
                }

            }
        }else{

            Toast.makeText(this, "Something went wrong with Intent call.", Toast.LENGTH_SHORT).show();
        }




    }



}
