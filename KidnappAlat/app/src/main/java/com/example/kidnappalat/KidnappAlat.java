package com.example.kidnappalat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.example.kidnappalat.BroadcastReceivers.RestarterBroadcast;
import com.example.kidnappalat.BroadcastReceivers.ServiceBroadcast;
import com.example.kidnappalat.Model.AlertRestrictClass;
import com.example.kidnappalat.Model.SharedPref;
import com.example.kidnappalat.Services.SMSDistressService;
import com.example.kidnappalat.Services.VideoFeedService;
import com.example.kidnappalat.Services.VolumeService;
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

import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class KidnappAlat extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;


    SharedPref login;

    SharedPref nologin;

    RelativeLayout overlay;

    private View litelayer;
    private CardView alat;
    private ImageView morelite;
    private ImageView personlite;
    private SharedPref videoPref;
    private SharedPref overlaypref;
    private SharedPref sharedfullname;
    private AlertRestrictClass alertretrict;


    private static final int REQUEST_CHECK_SETTINGS = 4353;
   // TextView continueApp;
    //ImageView distress;
    SharedPref stopdistress;
    SharedPref distressSigna;
   // Button continuebutton;
    private LocationRequest highRequest;
    private LocationRequest balanceRequest;
    private double xlatitude;
    private double xlongitude;
    private RelativeLayout layglow;
    private RelativeLayout alertback;
    private ImageView videofeedbutton;
    private SharedPref colorstatePref;
    private SharedPref distressServicePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kidnapp_alat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // fix location coordinate to check

        xlatitude  =  7.444259300000001;
        xlongitude =  3.8994576999999997;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.SEND_SMS,Manifest.permission.RECEIVE_SMS,Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        }


        // check the version and display the neccessay user interface

        // initialize the alert restrict class to retrist the alert button setting up the alert
        alertretrict = new AlertRestrictClass(this);

        // initialize the lite user interface

        litelayer =  findViewById(R.id.litelayer);
        morelite = (ImageView)findViewById(R.id.morelite);
        alat = (CardView)findViewById(R.id.alat);
        personlite = (ImageView)findViewById(R.id.personlite);

        // initialize design views
        layglow = (RelativeLayout)findViewById(R.id.layglow);
        alertback = (RelativeLayout)findViewById(R.id.alertback);
        videofeedbutton = (ImageView)findViewById(R.id.videofeed);



        // initialise a sharedpref to persistently hold the state of the ON/OFF videofeed
        distressServicePref = new SharedPref(this, getString(R.string.distressServicePref));
        videoPref = new SharedPref(this,getString(R.string.videofeedpref));
        overlaypref = new SharedPref(this, getString(R.string.overlaypref));
        sharedfullname = new SharedPref(KidnappAlat.this,getString(R.string.sharedfullname));
        colorstatePref = new SharedPref(this,getString(R.string.colorstatePref));
        colorstatePref.setBoolean(false);

        litelayer.setVisibility(View.VISIBLE);

        getLocation();



/*
        morelite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(KidnappAlat.this,v);
                popupMenu.inflate(R.menu.kidnapp_alat);
                popupMenu.show();

                Menu menu = popupMenu.getMenu();
                final MenuItem onVid = menu.findItem(R.id.onvideofeed);
                final MenuItem offVid = menu.findItem(R.id.offvideofeed);


                // re-register the previous state of the videofeed
                if(videoPref.getBoolean()){
                    onVid.setChecked(true);
                }else{
                    offVid.setChecked(true);
                }




                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        //  if(item.getItemId() == R.string.action_settings){

                        switch (item.getItemId()){

                            case R.id.signout:

                                onBackPressed();

                                break;

                            case R.id.emcontacts:
                                startActivity(new Intent(KidnappAlat.this,EmergencyContacts.class));
                                break;

                            case R.id.onvideofeed:
                                onVid.setChecked(true);
                                videoPref.setBoolean(true);

                                if(!overlaypref.getBoolean()) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        overlaypref.setBoolean(true);
                                        startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                                        Toast.makeText(KidnappAlat.this, "Activate Draw Over Other Apps for proper working of the app", Toast.LENGTH_SHORT).show();

                                    }
                                }




                                break;
                            case R.id.offvideofeed:
                                offVid.setChecked(true);
                                videoPref.setBoolean(false);

                                break;


                        }



                        // }



                        return true;
                    }
                });

            }
        });

*/



        // implement Alat cardview long click eventlisterner

        alat. setOnLongClickListener(v ->{


            if (!distressServicePref.getBoolean()){

                //if(alertretrict.alertActivated()){

                    sendBroadcast(new Intent(KidnappAlat.this, ServiceBroadcast.class));


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        litelayer.setBackground(getDrawable(R.drawable.blueblackgradientred));
                        layglow.setBackground(getDrawable(R.drawable.circlegradientred));
                        alertback.setBackground(getDrawable(R.drawable.circleringgradientred));

                        if (videoPref.getBoolean())
                            videofeedbutton.setBackground(getDrawable(R.drawable.circlegradientbuttonred));



                        colorstatePref.setBoolean(true);
                    }


               // }



            }else {

                distressServicePref.setBoolean(false);
                stopService(new Intent(KidnappAlat.this, SMSDistressService.class));
                stopService(new Intent(KidnappAlat.this, VideoFeedService.class));

                alertretrict.setActive(false);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    litelayer.setBackground(getDrawable(R.drawable.blueblackgradient));
                    layglow.setBackground(getDrawable(R.drawable.circlegradient));
                    alertback.setBackground(getDrawable(R.drawable.circleringgradient));

                    if (videoPref.getBoolean())
                        videofeedbutton.setBackground(getDrawable(R.drawable.circlegradientbutton));



                    colorstatePref.setBoolean(false);
                }




            }

            return true;
        });

        /*alat.setOnLongClickListener(v -> {

            if (distressServicePref.getBoolean()){

                distressServicePref.setBoolean(false);
                stopService(new Intent(KidnappAlat.this, SMSDistressService.class));
                stopService(new Intent(KidnappAlat.this, VideoFeedService.class));

                alertretrict.setActive(false);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    litelayer.setBackground(getDrawable(R.drawable.blueblackgradient));
                    layglow.setBackground(getDrawable(R.drawable.circlegradient));
                    alertback.setBackground(getDrawable(R.drawable.circleringgradient));

                    if (videoPref.getBoolean())
                        videofeedbutton.setBackground(getDrawable(R.drawable.circlegradientbutton));



                    colorstatePref.setBoolean(false);
                }




            }

            return true;});*/
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
       /* DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.kidnapp_alat, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void alat(View view) {


/*
        if (!distressServicePref.getBoolean()){

            if(alertretrict.alertActivated()){

                sendBroadcast(new Intent(KidnappAlat.this, ServiceBroadcast.class));


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    litelayer.setBackground(getDrawable(R.drawable.blueblackgradientred));
                    layglow.setBackground(getDrawable(R.drawable.circlegradientred));
                    alertback.setBackground(getDrawable(R.drawable.circleringgradientred));

                    if (videoPref.getBoolean())
                        videofeedbutton.setBackground(getDrawable(R.drawable.circlegradientbuttonred));



                    colorstatePref.setBoolean(true);
                }


            }



        }
*/


    }

    public void emergencycontact(View view) {
        startActivity(new Intent(KidnappAlat.this,EmergencyContacts.class));
    }

    public void profile(View view) {
        startActivity(new Intent(KidnappAlat.this, UserInfo.class));
    }

    public void videofeed(View view) {

        if(videoPref.getBoolean()){

            // video feed is on
            videoPref.setBoolean(false);
            videofeedbutton.setBackgroundColor(Color.TRANSPARENT);

            Toast.makeText(this, "Backgroud video recording disabled.", Toast.LENGTH_LONG).show();




        }else{

            // video feed is off

            videoPref.setBoolean(true);

            Toast.makeText(this, "Backgroud video recording enabled.", Toast.LENGTH_LONG).show();

            //TODO: On the blue or red glow
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if (colorstatePref.getBoolean()) {


                    videofeedbutton.setBackground(getDrawable(R.drawable.circlegradientbuttonred));

                } else {

                    videofeedbutton.setBackground(getDrawable(R.drawable.circlegradientbutton));
                }
            }

            if(!overlaypref.getBoolean()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    overlaypref.setBoolean(true);
                    startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                    Toast.makeText(KidnappAlat.this, "Activate Draw Over Other Apps for proper working of the app", Toast.LENGTH_SHORT).show();

                }
            }




        }




    }


    public void getLocation(){

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

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
                            resolvable.startResolutionForResult(KidnappAlat.this,REQUEST_CHECK_SETTINGS);
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

                if((result[0] <= 5500) || true ){

                    // if it is less that 5500 meter , then enable all the buttons


                    // start service
                    startService(new Intent(KidnappAlat.this, VolumeService.class));
                    setAlarm();


                }else{

                    Toast.makeText(KidnappAlat.this, "Sorry, You are not within University of Ibadan, so you can not use this app", Toast.LENGTH_LONG).show();


                    finishAffinity();



                }

            }
        }, Looper.myLooper());





    }



    public void setAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, RestarterBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(KidnappAlat.this,0,alarmIntent,0);


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
