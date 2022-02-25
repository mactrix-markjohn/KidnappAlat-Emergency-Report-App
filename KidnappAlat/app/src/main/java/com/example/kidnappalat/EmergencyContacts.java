package com.example.kidnappalat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import com.example.kidnappalat.Adapters.ContactAdapter;
import com.example.kidnappalat.Model.GeoSQLLite;
import com.example.kidnappalat.Model.SharedPref;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.ContactsContract;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EmergencyContacts extends AppCompatActivity {


    Button back;
    EditText contactname;
    EditText contactphone;
    Button savecontact;
    ListView contactlist;
    TextView emergency;
    TextView listcontact;
    TextView contactdetails;

    private Typeface ralelightfont;
    private Typeface exofont;
    private Typeface ralesemifont;



    GeoSQLLite emergencybase;

    final int RESULT_PICK_CONTACT = 0;
    private ContactAdapter contactAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        // Initialize the Database for storing the contacts
        emergencybase = new GeoSQLLite(this,getString(R.string.emergencycontactbase),null,1);

        registerRefresh();

        // Initialize the various views
        back = (Button)findViewById(R.id.back);
        contactname = (EditText)findViewById(R.id.contactname);

        contactphone = (EditText)findViewById(R.id.contactphone);

        savecontact = (Button)findViewById(R.id.savecontact);

        contactlist = (ListView)findViewById(R.id.contactlist);
        emergency = (TextView)findViewById(R.id.emergency);

        listcontact = (TextView)findViewById(R.id.listcontact);

        contactdetails = (TextView)findViewById(R.id.contactdetails);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Save thr contact in the Database
        savecontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emergencybase.addGeofence(contactname.getText().toString(),0,0,contactphone.getText().toString()," "," ");

                sendBroadcast(new Intent(getString(R.string.refresh)));
                Toast.makeText(EmergencyContacts.this, "Contact Saved", Toast.LENGTH_SHORT).show();
            }
        });


        contactAdapter = new ContactAdapter(this,emergencybase);
        contactlist.setAdapter(contactAdapter);

        setDefaultEmergencyNo();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    // Broadcast called to Refresh the list for any Updates
    BroadcastReceiver refresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ContactAdapter contactAdapter = new ContactAdapter(EmergencyContacts.this,emergencybase);
            contactlist.setAdapter(contactAdapter);

            //contactAdapter.notifyDataSetChanged();

            contactname.setText("");
            contactphone.setText("");

        }
    };

    public void registerRefresh(){

        IntentFilter intentFilter = new IntentFilter(getString(R.string.refresh));
        registerReceiver(refresh,intentFilter);

    }

    public void addcontact(View view) {


        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent,RESULT_PICK_CONTACT);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode == RESULT_OK){


            if(requestCode == RESULT_PICK_CONTACT){

                contactPicked(data);

            }


        }else{

            Toast.makeText(this, "Failed to Pick Contact", Toast.LENGTH_SHORT).show();
        }



    }

    private void contactPicked(Intent data) {

        try {

            Uri uri = data.getData();

            Cursor cursor = getContentResolver().query(uri,null,null,null,null);


            if(cursor != null){
                for (int i =0; i< cursor.getCount(); i++){

                    cursor.moveToPosition(i);

                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String contacname = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));


                    emergencybase.addGeofence(contacname,0,0,phoneNumber," "," ");


                }

                sendBroadcast(new Intent(getString(R.string.refresh)));
                Toast.makeText(EmergencyContacts.this, "Contact Saved", Toast.LENGTH_SHORT).show();

            }





        }catch (Exception e){



        }



    }


    private void setDefaultEmergencyNo(){
        SharedPref defaultEmergence = new SharedPref(this,getString(R.string.defaultEmergency));

        if(defaultEmergence.getInt() == 0){

            emergencybase.addGeofence("Central Emergency Number 1",0,0,getString(R.string.phoneone)," "," ");
            emergencybase.addGeofence("Central Emergency Number 2",0,0,getString(R.string.phonetwo)," "," ");
            emergencybase.addGeofence("Central Emergency Number 3",0,0,getString(R.string.phonethree)," "," ");
            sendBroadcast(new Intent(getString(R.string.refresh)));

            defaultEmergence.setInt(1);
        }





    }
}
