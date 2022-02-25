package com.example.kidnappalat.Model;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.example.kidnappalat.R;

public class AlertRestrictClass {


    private Context context;
    private SharedPref clickcount;
    private boolean activated = false;
    private int count = 0;

    public AlertRestrictClass(Context context) {
        this.context = context;
        clickcount = new SharedPref(context, context.getString(R.string.clickcount));
    }


    public boolean alertActivated(){

        count = clickcount.getInt();
        count ++;

        if(count == 1){
            new Handler().postDelayed(() -> {

                // reset the clickcount shared preference to 0 once the time counts down from 10seconds
                clickcount.setInt(0);
                Toast.makeText(context, "Click reset to 0", Toast.LENGTH_LONG).show();

            }, 10000);
        }else if (count == 4){

            activated = true;
            clickcount.setInt(0);

        }


        clickcount.setInt(count);
        Toast.makeText(context, "Click : "+count, Toast.LENGTH_SHORT).show();



        return activated;
    }

    public void setActive(boolean active){
        activated = active;
    }

    public boolean getActive(){
        return activated;
    }


}
