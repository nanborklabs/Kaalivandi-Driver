package com.kaalivandi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity  {


    public static String SP_FILE="Kaalivandi1.1";
    public static String TAG="kaalivandi";
    EditText sub;
    Button b1;



    public void pop(String sm){
        Log.i("Flow",sm);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // mViewPager=(ViewPager)findViewById(R.id.pager);
        setTitle("KaaliVandi");
        setTitle("Login");
        sub=(EditText)findViewById(R.id.editText);
        b1=(Button)findViewById(R.id.button);
        pop("Activity Main");


        boolean sawlogin=getSharedPreferences(SP_FILE,Context.MODE_PRIVATE).getBoolean("saw_login",false);
        if (sawlogin){
            //saw Login
            Intent i=new Intent(this,HomeActivity.class);

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            pop("Starting Deafult Activity");
            startActivity(i);
        }

        Showdialog();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences(SP_FILE, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("saw_login", true)
                        .commit();


                pop("sub to string" + sub.toString());
                getSharedPreferences(SP_FILE,Context.MODE_PRIVATE)
                        .edit()
                        .putString("id", sub.getText().toString()).commit();
                pop("id saved===" + sub.getText().toString());

                Intent in = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(in);
            }
        });

    }

    private void Showdialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setTitle("Terms and Conditions")
                .setMessage("KaaliVandi is Free platform for drivers and CUstomers to Connect \n 1.I ,the Vehicle Owner,am accepting to share my Location to KaaliVandi and abide by its terms and conditions to share location data")
                .setInverseBackgroundForced(true)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        getSharedPreferences(SP_FILE, Context.MODE_PRIVATE).edit()
                                .putBoolean("Accepted", true)
                                .commit();

                    }
                });
        builder.show();
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "On Back Pressed");
        super.onBackPressed();
    }
}





