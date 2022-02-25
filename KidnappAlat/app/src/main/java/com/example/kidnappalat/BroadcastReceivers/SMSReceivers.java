package com.example.kidnappalat.BroadcastReceivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.kidnappalat.Model.GeoSQLLite;
import com.example.kidnappalat.Model.SharedPref;
import com.example.kidnappalat.R;

public class SMSReceivers extends BroadcastReceiver {


    private StringBuilder stringBuilder;
    private String strMessage;
    private SharedPref sendphone;
    private String keyword;
    private static final String TAG =SMSReceivers.class.getSimpleName();
    public static final String pdu_type = "pdus";
    private boolean isVersionM;
    private String smsPhone;
    private String chainkey;
    private String keyalt;
    private GeoSQLLite emergencycontact;
    private Cursor cursor;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        getSMS(context, intent);
    }

    private void getSMS(Context context, Intent intent) {
        // Get the SMS message.
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        stringBuilder = new StringBuilder();
        strMessage  = "";
        String format = bundle.getString("format");

        sendphone = new SharedPref(context,context.getString(R.string.sendphone));
        keyword = "KidnappAlat";
        chainkey = "http://maps.google.com/maps?q=";
        keyalt = "ALAT";



        // Retrieve the SMS message received.
        Object[] pdus = (Object[]) bundle.get(pdu_type);

        if (pdus != null) {
            // Check the Android version.
            isVersionM  = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
        }


        // Fill the msgs array.
        msgs = new SmsMessage[pdus.length];
        for (int i = 0; i < msgs.length; i++) {
            // Check Android version and use appropriate createFromPdu.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // If Android version M or newer:
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
            } else {
                // If Android version L or older:
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            // Build the message to show.
            try {
                String string = msgs[i].getDisplayOriginatingAddress();
                if(!string.contains("null")){

                    stringBuilder.append(msgs[i].getOriginatingAddress());
                }


            }catch (Exception e){
                smsPhone = msgs[0].getOriginatingAddress();
            }

            strMessage += " :" + msgs[i].getMessageBody() + "\n";



        }

        smsPhone = stringBuilder.toString();

        if(smsPhone.contains("null")){

            smsPhone = smsPhone.replace("null","");
        }

        // Log and display the SMS message.
        Log.d(TAG, "onReceive: " + strMessage);
        Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();

        if(strMessage.toLowerCase().contains(keyword.toLowerCase())){

            sendphone.setText(smsPhone);
            context.sendBroadcast(new Intent(context, ServiceBroadcast.class));

            // TODO: Stop the broadcast from being sent to other Apps
            abortBroadcast();
            clearAbortBroadcast();

        }else if (strMessage.toLowerCase().contains(chainkey.toLowerCase())){


            chaindistress(context,strMessage);

        }else if (strMessage.toLowerCase().contains(keyalt.toLowerCase())){

            chaindistress(context,strMessage);

        }




    }


    public void chaindistress(Context context, String message){

        emergencycontact = new GeoSQLLite(context,context.getString(R.string.emergencycontactbase),null,1);
        cursor = emergencycontact.getAllGeofence();


        if(cursor != null){

            for(int i = 0; i< cursor.getCount(); i++){
                cursor.moveToPosition(i);

                String phone = cursor.getString(cursor.getColumnIndex(GeoSQLLite.MESSAGE));
                sendChainSMS(message,phone);



            }

        }

        // send Distress Sms to central Emergency number
        sendChainSMS(message,context.getString(R.string.centreemergencyno));



    }

    public void sendChainSMS(String chainSMS, String senderTel){

        String scAddress = null;
        PendingIntent pendingIntent = null, deliveryIntent = null;


        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(senderTel, scAddress,chainSMS,pendingIntent,deliveryIntent);


    }






}
