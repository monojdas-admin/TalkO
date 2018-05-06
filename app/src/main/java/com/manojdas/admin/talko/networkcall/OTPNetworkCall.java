package com.manojdas.admin.talko.networkcall;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.manojdas.admin.talko.misc.URLConnection;

import java.io.InputStream;

/**
 * Created by Manoj Das on 20-Mar-18.
 */

public class OTPNetworkCall extends AsyncTask<String,Void,String> {

    Context context;
    String strOtp;

    AlertDialog alertDialog;

    public OTPNetworkCall(Context context, String strOtp) {
        this.context = context;
        this.strOtp = strOtp;
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
        alertDialog.setMessage("Generating OTP..");
        alertDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        alertDialog.setMessage("OTP Generated");
        alertDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d("Inside","MakeNetworkCall doInBackground");
        String result="";
        try {

            Log.d("Connecting","POSTREGISTRATION");
            URLConnection urlConnection = new URLConnection();
            InputStream inputStream = urlConnection.PostOTPMethod("http://10.0.2.2/kitchen/adduser.php", strOtp);
            result = urlConnection.ConvertStreamToString(inputStream);

        }catch (Exception e){
            Log.d("Error in Connection","",e);
            //Toast.makeText(context, "Error in Connection", Toast.LENGTH_SHORT).show();
        }
        return result;
    }
}
