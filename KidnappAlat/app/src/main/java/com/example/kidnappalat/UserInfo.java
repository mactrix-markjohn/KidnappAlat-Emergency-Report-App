package com.example.kidnappalat;

import android.os.Bundle;

import com.example.kidnappalat.Model.SharedPref;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class UserInfo extends AppCompatActivity {


    private SharedPref usermatric;
    private SharedPref userdepartment;
    private SharedPref userwork;
    private SharedPref username;
    private EditText matric;
    private EditText name;
    private EditText work;
    private EditText department;
    private String m;
    private String d;
    private String w;
    private String n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = new SharedPref(this, getString(R.string.sharedfullname));
        userwork = new SharedPref(this,getString(R.string.userwork));
        userdepartment = new SharedPref(this,getString(R.string.userdepartment));
        usermatric = new SharedPref(this,getString(R.string.usermatric));

        name  = (EditText)findViewById(R.id.username);
        work = (EditText)findViewById(R.id.userwork);
        department = (EditText)findViewById(R.id.userdepartment);
        matric = (EditText)findViewById(R.id.usermatric);


        n = username.getText();
        w = userwork.getText();
        d = userdepartment.getText();
        m = usermatric.getText();

        if(!username.getText().equalsIgnoreCase("null")) {
            name.setText(n);
            work.setText(w);
            department.setText(d);
            matric.setText(m);
        }




        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void back(View view) {

        onBackPressed();


    }

    public void save(View view) {

        username.setText(name.getText().toString());
        userwork.setText(work.getText().toString());
        userdepartment.setText(department.getText().toString());
        usermatric.setText(matric.getText().toString());

        Toast.makeText(this, "Sucessfully Saved", Toast.LENGTH_SHORT).show();
    }
}
