package com.example.kidnappalat.BroadcastReceivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.kidnappalat.Model.SharedPref;
import com.example.kidnappalat.R;
import com.example.kidnappalat.Services.SMSDistressService;
import com.example.kidnappalat.Services.VideoFeedService;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class ServiceBroadcast extends BroadcastReceiver {

    private LocationRequest balanceRequest;
    private LocationRequest highRequest;

    private double xlatitude;
    private double xlongitude;

    boolean nonnull, falsenull = false; // this is a boolean variable that checks if the distress service are active so it is not called multiple times by the system
    private SharedPref distressServicePref;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        // fix location coordinate to check

        xlatitude  =  7.444259300000001;
        xlongitude =  3.8994576999999997;

        //sharedpreference to check if the distress service is on
        distressServicePref = new SharedPref(context,context.getString(R.string.distressServicePref));
        distressServicePref.setBoolean(false);




        getLocation(context);

        //Toast.makeText(context, "The Service Broadcast is called", Toast.LENGTH_SHORT).show();
    }


    public void getLocation(Context context){

        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

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


        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());

        result.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                LocationSettingsStates states = locationSettingsResponse.getLocationSettingsStates();
                if(states.isGpsUsable()){


                    addLocationservice(highRequest,context);

                }else{

                    addLocationservice(balanceRequest,context);
                }




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                addLocationservice(balanceRequest, context);
            }
        });





    }

    private void addLocationservice(LocationRequest locationRequest, Context context) {

        if(ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            return;
        }

        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, new LocationCallback() {

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

                // check to see that the user distance is less than or equal to 5500 meters

                if((result[0] <= 5500) || true){

                    // if it is less that 5500 meter , then enable all the buttons


                    if(!nonnull) {

                        distressServicePref.setBoolean(true);
                        context.startService(new Intent(context, SMSDistressService.class));
                        context.startService(new Intent(context, VideoFeedService.class));

                        nonnull = true;


                    }


                }else{

                    if (!falsenull){

                        Toast.makeText(context, "Sorry, You are not within University of Ibadan, so you can not use this app", Toast.LENGTH_LONG).show();

                        //TODO: Please change this to False once you are done testing
                        distressServicePref.setBoolean(false);


                    }



                }







            }
        }, Looper.myLooper());





    }
}
