package com.example.kidnappalat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kidnappalat.Model.GeoSQLLite;
import com.example.kidnappalat.R;

public class ContactAdapter extends BaseAdapter {


    Context context;
    GeoSQLLite contactbase;
    Cursor cursor;
    LayoutInflater inflater;

    public ContactAdapter(Context context, GeoSQLLite contactbase) {

        this.context = context;
        this.contactbase = contactbase;
        cursor = contactbase.getAllGeofence();
        inflater = LayoutInflater.from(context);

    }


    class ViewHolder{


        TextView contactname;
        TextView contactphone;
        Button contactmenu;


        public ViewHolder(View v, final int p) {

            contactname = (TextView) v.findViewById(R.id.contactname);
            contactphone = (TextView) v.findViewById(R.id.contactphone);
            contactmenu = (Button) v.findViewById(R.id.contactmenu);


            if (cursor != null) {

                cursor.moveToPosition(p);
                String name = cursor.getString(cursor.getColumnIndex(GeoSQLLite.GEOID));
                String phone = cursor.getString(cursor.getColumnIndex(GeoSQLLite.MESSAGE));

                contactname.setText(name);
                contactphone.setText(phone);


            contactmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.inflate(R.menu.contactmenu);
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {

                                case R.id.edit:
                                    edit(cursor, p);
                                    break;
                                case R.id.delete:
                                    delete(cursor, p);
                                    break;

                            }


                            return true;
                        }
                    });


                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.inflate(R.menu.contactmenu);
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {

                                case R.id.edit:
                                    edit(cursor, p);
                                    break;
                                case R.id.delete:
                                    delete(cursor, p);
                                    break;

                            }


                            return true;
                        }
                    });


                    return true;
                }
            });


        }



        }


    }


    public void delete(Cursor cursor, int p){
        long id;

        if(cursor != null){

            cursor.moveToPosition(p);

            id = cursor.getLong(cursor.getColumnIndex(GeoSQLLite.ID));

            contactbase.deleteGeofence(id);

            context.sendBroadcast(new Intent(context.getString(R.string.refresh)));
        }

    }

    public void edit(Cursor cursor, int p){


        final long id;

        if(cursor != null){

            cursor.moveToPosition(p);

            id = cursor.getLong(cursor.getColumnIndex(GeoSQLLite.ID));




            final EditText name = new EditText(context);
            name.setHint("Contact name");
            final EditText phone = new EditText(context);
            phone.setHint("Phone Number");

            new AlertDialog.Builder(context)
                    .setTitle("Enter Contact name")
                    .setView(name)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            new AlertDialog.Builder(context)
                                    .setTitle("Enter Phone Number")
                                    .setView(phone)
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            contactbase.updateGeofence(id,name.getText().toString(),0,0,phone.getText().toString()," "," ");

                                            Toast.makeText(context, "Updated Contact Details", Toast.LENGTH_SHORT).show();
                                            context.sendBroadcast(new Intent(context.getString(R.string.refresh)));



                                        }
                                    })
                                    .create().show();


                        }
                    })
                    .create().show();





        }

    }

    @Override
    public int getCount() {
        int count = 0;
        if(cursor != null){

            count = cursor.getCount();


        }

        return count;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if(convertView == null){
            convertView = inflater.inflate(R.layout.contactlist,parent,false);
        }
        new ViewHolder(convertView,position);

        return convertView;
    }
}
