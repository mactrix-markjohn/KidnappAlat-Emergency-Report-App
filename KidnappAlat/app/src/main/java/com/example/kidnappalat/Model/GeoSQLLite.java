package com.example.kidnappalat.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
//import android.support.annotation.Nullable;

public class GeoSQLLite extends SQLiteOpenHelper implements Parcelable {

    Cursor cursor;

    public static final Creator<GeoSQLLite> CREATOR = null;


    public static final String TABLENAME = "GeoFenceBase";
    public static final String ID = "_id";
    public static final String GEOID = "geoid";
    public static final String LATITUDE = "latitude";
    public static final String LONGITIUDE = "longitude";
    public static final String MESSAGE = "message";
    public static final String STILL = "still";
    public static final String EXIT = "exit";


    private static final String DATABASENAME = "kidnapp.db";
    String[] columns = new String[]{ID,GEOID,LATITUDE,LONGITIUDE,MESSAGE,STILL,EXIT};

    public static final int VERSION = 1;






    public GeoSQLLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, VERSION);
    }





    @Override
    public void onCreate(SQLiteDatabase db) {




        db.execSQL("CREATE TABLE "+TABLENAME+"("+ID+" integer primary key, "+GEOID+" text, "+LATITUDE+" integer, "+LONGITIUDE+" integer, "+MESSAGE+" text, "+STILL+" text, "+EXIT+" text)");

        // db.execSQL("create table geofencebase (_id integer primary key, goeid text, latitude integer, longitude integer, message text, still text, exit text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public long addGeofence(String geoid, double latitude, double longitude, String message, String still, String exit ){

        SQLiteDatabase dbase = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(GEOID,geoid);
        value.put(LATITUDE,latitude);
        value.put(LONGITIUDE,longitude);
        value.put(MESSAGE,message);
        value.put(STILL,still);
        value.put(EXIT,exit);
        return dbase.insert(TABLENAME,null,value);
    }


    public Cursor getAllGeofence(){
        SQLiteDatabase dbase = this.getWritableDatabase();
        return dbase.query(TABLENAME,columns,null,null,null,null,null);
    }

    public boolean deleteGeofence(long id){
        SQLiteDatabase dbase = this.getWritableDatabase();
        return dbase.delete(TABLENAME,ID+"=?",new String[]{Long.toString(id)})>0;
    }


    public boolean updateGeofence(long id, String geoid, double latitude, double longitude, String message, String still, String exit){
        SQLiteDatabase dbase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GEOID,geoid);
        values.put(LATITUDE,latitude);
        values.put(LONGITIUDE,longitude);
        values.put(MESSAGE,message);
        values.put(STILL,still);
        values.put(EXIT,exit);
        return dbase.update(TABLENAME,values,ID+"="+id,null)>0;

    }


    public Cursor getGeofence(Long id){

        Cursor z=null;
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            z = db.rawQuery("select * from " + TABLENAME + " where _id=" + id
                    + "", null);


           /* cursor = dbase.query(true,TABLENAME,column,ID+"="+id,null,null,null,null,null);
            if(cursor!=null){cursor.moveToFirst();}*/

        }catch (SQLException e){ e.printStackTrace();}
        return z;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(columns);
    }
}
