package com.manojdas.admin.talko;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class OTPActivity extends AppCompatActivity {

    EditText etOtp;
    TextView textView;
    String otp,sotp;

    boolean error = false;
    String struser_id,username,useremail,usercontact,usergender,userdob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        etOtp = (EditText)findViewById(R.id.etotp);

        Bundle bundle=getIntent().getExtras();
        struser_id=bundle.getString("user_id");
        username=bundle.getString("username");
        useremail=bundle.getString("useremail");
        usercontact=bundle.getString("usercontact");
        usergender=bundle.getString("usergender");
        userdob=bundle.getString("userdob");
        sotp=bundle.getString("otp");

        Log.d("Bundle",struser_id+" "+username+" "+useremail+" "+usercontact+" "+usergender+" "+userdob+" "+sotp);

        textView= (TextView) findViewById(R.id.tvName);
        textView.setText("Welcome "+username);

        if (Build.VERSION.SDK_INT >=23){
            if (!hasPermissions(OTPActivity.this, Manifest.permission.READ_CONTACTS)){
                ActivityCompat.requestPermissions(OTPActivity.this,new String[]{Manifest.permission.READ_CONTACTS},100);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==100){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }
        }
    }
    private static boolean hasPermissions(Context context,String... permissions ){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions !=null){
            for (String permission : permissions){
                if (ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    public void onOTP(View view){
        otp = etOtp.getText().toString();
        Log.d("onOTP",otp+"="+sotp);

        checkError();

        if (!error){
            //OTPNetworkCall otpNetworkCall = new OTPNetworkCall(OTPActivity.this,otp);
            //otpNetworkCall.execute();

            if (otp.equals(sotp)){
                SharedPreferences sharedPreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("user_id",struser_id);
                editor.putString("name",username);
                editor.putString("email",useremail);
                editor.putString("contact",usercontact);
                editor.putString("gender",usergender);
                editor.putString("dob",userdob);
                editor.putString("checking","registered");
                editor.commit();

                Intent intent = new Intent(getApplicationContext(),ChatHomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public void checkError(){

        error = false;

        // Check for a valid otp
        if (TextUtils.isEmpty(otp)) {
            etOtp.setError("Enter valid otp");
            error = true;
        }else if(otp.length() != 5){
            etOtp.setError("Enter valid otp");
            error = true;
        }else if (!(otp.equals(sotp))){
            etOtp.setError("Wrong otp");
            error = true;
        }

    }
}
