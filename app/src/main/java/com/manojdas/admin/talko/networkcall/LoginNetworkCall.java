package com.manojdas.admin.talko.networkcall;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.manojdas.admin.talko.misc.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Manoj Das on 20-Mar-18.
 */
/*
public class LoginNetworkCall extends AsyncTask<String,Void,String> {

    Context context;
    String strName,strEmail,strContact,strDob;

    AlertDialog alertDialog;

    public LoginNetworkCall(Context context, String strName, String strEmail, String strContact, String strDob) {
            this.context = context;
            this.strName = strName;
            this.strEmail = strEmail;
            this.strContact = strContact;
            this.strDob = strDob;
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
            String result=null;
            try {
                Log.d("Connecting","POSTLOGIN");
                URLConnection urlConnection = new URLConnection();
                InputStream inputStream = urlConnection.PostLoginMethod("http://192.168.42.215:8888/talko/contact.php", strName,strEmail,strContact,strDob);
                //InputStream inputStream = urlConnection.GetMethod("http://talko.epizy.com/talko/send.php");
                result = urlConnection.ConvertStreamToString(inputStream);

                Log.d("Connected","POSTLOGIN");
                Log.d("result",result);

            }catch (Exception e){
                Log.d("Error in Connection","",e);
                //Toast.makeText(context, "Error in Connection", Toast.LENGTH_SHORT).show();
            }


            if (result != null){
                try {
                    JSONObject jsonObject = new JSONObject();

                    JSONObject user_details = jsonObject.getJSONObject(result);

                    int id = user_details.getInt("id");
                    String name = jsonObject.getString("name");
                    String email = jsonObject.getString("email");

                    Log.d("jsonObject",String.valueOf(id)+name+email);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                //Log.d("result",result);
            }catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }
}
*/

public class LoginNetworkCall extends AsyncTask<String,Void,String> {

    Context context;
    String strName,strEmail,strContact,strDob;

    AlertDialog alertDialog;

    public LoginNetworkCall(Context context, String strName, String strEmail, String strContact, String strDob) {
        this.context = context;
        this.strName = strName;
        this.strEmail = strEmail;
        this.strContact = strContact;
        this.strDob = strDob;
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
        alertDialog.setMessage("Generating OTP..");
       // alertDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        alertDialog.setMessage("OTP Generated");
        alertDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        String login_url = "http://192.168.42.215:8888/talko/contact.php";
        try {
            URL url = new URL(login_url);

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setReadTimeout(60000);
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.getResponseMessage();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

            String post_data = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(strName,"UTF-8")+"&"+
                    URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(strEmail,"UTF-8")+"&"+
                    URLEncoder.encode("contact","UTF-8")+"="+URLEncoder.encode(strContact,"UTF-8")+"&"+
                    URLEncoder.encode("dob","UTF-8")+"="+URLEncoder.encode(strDob,"UTF-8");

            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

            String line = "";

            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            Log.d("result",result);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}

