package com.manojdas.admin.talko;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.manojdas.admin.talko.misc.PrefManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*SharedPreferences preferences=getSharedPreferences("User", Context.MODE_PRIVATE);
            String user_id=preferences.getString("user_id",null);
            String name=preferences.getString("name",null);
            String email=preferences.getString("email",null);
            String contact=preferences.getString("contact",null);
            String gender=preferences.getString("gender",null);
            String dob=preferences.getString("dob",null);


            Intent intent = new Intent(getApplicationContext(), OTPActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("user_id",user_id);
            bundle.putString("username", name);
            bundle.putString("useremail", email);
            bundle.putString("usercontact", contact);
            bundle.putString("usergender", gender);
            bundle.putString("userdob", dob);
            bundle.putString("otp", "55555");
            intent.putExtras(bundle);
            startActivity(intent);
            */
    }

    public void onIntro(View view){
        PrefManager prefManager = new PrefManager(getApplicationContext());
        prefManager.setFirstTimeLaunch(true);
        startActivity(new Intent(getApplicationContext(),WelcomeActivity.class));
    }
}
