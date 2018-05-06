package com.manojdas.admin.talko;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.manojdas.admin.talko.networkcall.LoginNetworkCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements AsyncResponse {

    EditText etName,etEmail,etContact,etDob;
    String name,email,contact,dob;

    boolean error;

    Calendar calendar=Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences=getSharedPreferences("User", Context.MODE_PRIVATE);
        String strcheck=preferences.getString("checking",null);

        if(strcheck!=null){
            Intent intent=new Intent(getApplicationContext(),ChatHomeActivity.class);
            Toast.makeText(getApplicationContext(),"Welcome",Toast.LENGTH_LONG).show();
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);

        etName = (EditText)findViewById(R.id.etName);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etContact = (EditText)findViewById(R.id.etContact);
        etDob = (EditText)findViewById(R.id.etDob);

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                Log.d("DatePickerDialog","initialised");
                Log.d("Calendar",String.valueOf(Calendar.YEAR)+String.valueOf(Calendar.MONTH)+String.valueOf(Calendar.DAY_OF_MONTH));
                updateLabel();

            }

        };

        etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(LoginActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel(){
        String myformat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myformat, Locale.US);
        Log.d("updatelabel","initialised");
        etDob.setText(sdf.format(calendar.getTime()));
    }

    public void onRegister(View view){

        name = etName.getText().toString();
        email = etEmail.getText().toString();
        contact = etContact.getText().toString();
        dob = etDob.getText().toString();

        checkError();

        if (!error){
            //LoginNetworkCall loginNetworkCall = new LoginNetworkCall(LoginActivity.this,name,email,contact,dob);
            //loginNetworkCall.execute();
            HashMap<String,String> postdata = new HashMap<String, String>();
            postdata.put("name",name);
            postdata.put("email",email);
            postdata.put("contact",contact);
            postdata.put("dob",dob);

            PostResponseAsyncTask responseAsyncTask = new PostResponseAsyncTask(this,postdata,this);
            responseAsyncTask.execute("http://dasmanoj1996md.000webhostapp.com/talko/login.php");

        }

    }

    @Override
    public void processFinish(String result){
        Log.d("result",result);
        String user_id="";
        String jsonname="",jsonemail="",jsoncontact="",jsongender="",jsondob="",jsonotp;

        if (result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (result.contains("UserExists")){
                    JSONArray userExists = jsonObject.getJSONArray("UserExists");
                    JSONObject user_details = userExists.getJSONObject(0);

                    user_id = user_details.getString("user_id");
                    jsonname = user_details.getString("name");
                    jsonemail = user_details.getString("email");
                    jsoncontact = user_details.getString("contact");
                    jsongender = user_details.getString("gender");
                    jsondob = user_details.getString("dob");
                    jsonotp = user_details.getString("otp");

                    Intent intent = new Intent(getApplicationContext(), OTPActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", user_id);
                    bundle.putString("username", jsonname);
                    bundle.putString("useremail", jsonemail);
                    bundle.putString("usercontact", jsoncontact);
                    bundle.putString("usergender", jsongender);
                    bundle.putString("userdob", jsondob);
                    bundle.putString("otp", jsonotp);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();

                    Toast.makeText(getApplicationContext(),"User Already Exists",Toast.LENGTH_LONG).show();
                }else if (result.contains("UserSuccessfullyAdded")){
                    JSONArray userSuccessfullyAdded = jsonObject.getJSONArray("UserSuccessfullyAdded");
                    JSONObject user_detailsAdded = userSuccessfullyAdded.getJSONObject(0);

                    user_id = user_detailsAdded.getString("user_id");
                    jsonname = user_detailsAdded.getString("name");
                    jsonemail = user_detailsAdded.getString("email");
                    jsoncontact = user_detailsAdded.getString("contact");
                    jsongender = user_detailsAdded.getString("gender");
                    jsondob = user_detailsAdded.getString("dob");
                    jsonotp = user_detailsAdded.getString("otp");

                    Toast.makeText(getApplicationContext(),"Successfully Connected",Toast.LENGTH_LONG).show();
                    Log.d("status","UserSuccessfullyAdded");

                    /*SharedPreferences sharedPreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("user_id",String.valueOf(user_id));
                    editor.putString("name",jsonname);
                    editor.putString("email",jsonemail);
                    editor.putString("contact",jsoncontact);
                    editor.putString("gender",jsongender);
                    editor.putString("dob",jsondob);
                    editor.putString("checking","registered");
                    editor.commit();
                    */

                    Intent intent = new Intent(getApplicationContext(), OTPActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id",user_id);
                    bundle.putString("username", jsonname);
                    bundle.putString("useremail", jsonemail);
                    bundle.putString("usercontact", jsoncontact);
                    bundle.putString("usergender", jsongender);
                    bundle.putString("userdob", jsondob);
                    bundle.putString("otp", jsonotp);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_LONG).show();
                }

                Log.d("jsonObject",user_id+jsonname+jsonemail+jsoncontact+jsongender+jsondob);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void checkError(){

        error = false;

        // Check for a valid name
        if (TextUtils.isEmpty(name)) {
            etName.setError("Enter your name");
            error = true;
        }
        // Check for a valid email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter your email");
            error = true;
        }else if(!isEmailValid(email)){
            etEmail.setError("Enter valid email");
            error = true;
        }
        // Check for a valid name
        if (TextUtils.isEmpty(contact)) {
            etContact.setError("Enter your contact");
            error = true;
        }else if(!isContactValid(contact)){
            etContact.setError("Enter valid contact");
            error = true;
        }
        // Check for a valid Dob
        if (TextUtils.isEmpty(dob)) {
            etDob.setError("Enter your DOB");
            error = true;
        }// Check for a valid Dob
        else if (Integer.parseInt(dob.substring(6))>= 2003) {
            etDob.setError("Sorry your age is too small");
            error = true;
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isContactValid(String contact) {
        return contact.length() == 10;
    }
}
