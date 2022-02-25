package com.example.kidnappalat.Services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.example.kidnappalat.Model.Constants;
import com.google.android.gms.maps.model.LatLng;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeoCodeIntentService extends IntentService {

    LatLng latLng;
    ResultReceiver resultReceiver;
    Geocoder geocoder;

    List<Address> addresses;

    String errorMessage="";
    String strin = "";


    public GeoCodeIntentService() {
        super("GeoCodeIntentService");
    }




    @Override
    protected void onHandleIntent(Intent intent) {





        if(intent != null){

            try {


                latLng = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
                resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
                geocoder = new Geocoder(this, Locale.getDefault());
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);


            }catch (NullPointerException e){


                errorMessage = "Null Pointer";

            } catch (IOException e) {
               // e.printStackTrace();

                errorMessage = "Service not Available";


            }catch (IllegalArgumentException e){
                //e.printStackTrace();
                errorMessage = "Invalid Latitude and Longitude";
            }


            // Handle the Address got from the geocode
            if(addresses == null || addresses.size() == 0){

                if(errorMessage.isEmpty()){
                    errorMessage = "No address found";
                }
                sendResult(Constants.FAILURE_RESULT,errorMessage);

            }else{

                Address address = addresses.get(0);




                String stf = address.getSubThoroughfare() != null ? address.getSubThoroughfare() :"";
                String tf = address.getThoroughfare() != null ? address.getThoroughfare() : "";
                String sl = address.getSubLocality() != null ? address.getSubLocality() : "";
                String l = address.getLocality() != null ? address.getLocality() : "";
                String saa = address.getSubAdminArea() != null ? address.getSubAdminArea() : "";
                String aa = address.getAdminArea() != null ? address.getAdminArea() : "";
                String c = address.getCountryName() != null ? address.getCountryName() : "";
                String p = address.getPremises() != null ? address.getPremises() : "";

                strin = stf +" "+tf+" "+sl+" "+p+" "+l+" "+saa+" "+aa+" "+c;


               /* strin = ""+address.getSubThoroughfare()+" "+address.getThoroughfare()+"\n"+address.getSubLocality()+" "+address.getPremises()+" "
                +address.getLocality()+"\n"+address.getSubAdminArea()+" "+address.getAdminArea()+"\n"+address.getCountryName();
*/
              /* // sendResult(Constants.SUCCESS_RESULT,address.getCountryName(),address.getAdminArea(),address.getSubLocality(),string);
*/
                sendResult(Constants.SUCCESS_RESULT,strin);

            }




        }


    }

    protected void sendResult(int resultcode, String country, String state, String locality, String fulladdress){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.COUNTRYNAME,country);
        bundle.putString(Constants.STATENAME,state);
        bundle.putString(Constants.CITYNAME,locality);
        bundle.putString(Constants.FULLADDRESS,fulladdress);

        resultReceiver.send(resultcode,bundle);






    }
    protected void sendResult(int resultcode, String locationaddress){

        try {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SENTLOCATION, locationaddress);
            resultReceiver.send(resultcode, bundle);

        }catch (Exception e){
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SENTLOCATION, "Unknown");
            resultReceiver.send(resultcode, bundle);
        }

    }

    protected void sendResult(int resultcode, String errormessage, int i){

        try {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.COUNTRYNAME, errormessage);
            bundle.putString(Constants.STATENAME, errormessage);
            bundle.putString(Constants.CITYNAME, errormessage);

            resultReceiver.send(resultcode, bundle);
        }catch (Exception e){
            errormessage = "Cannot send";
        }

    }


}
