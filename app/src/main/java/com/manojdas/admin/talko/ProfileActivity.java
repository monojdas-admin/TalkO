package com.manojdas.admin.talko;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity implements AsyncResponse {

    EditText etid,etName,etEmail,etContact,etPDob;
    String id,name,email,contact,gender,dob;

    Integer REQUEST_CAMERA=1, SELECT_FILE=0;

    Bitmap bmp;
    ImageView ivImage;

    boolean error;

    Calendar calendar=Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectImage();

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences=getSharedPreferences("User", Context.MODE_PRIVATE);
        id=preferences.getString("user_id",null);
        name=preferences.getString("name",null);
        email=preferences.getString("email",null);
        contact=preferences.getString("contact",null);
        gender=preferences.getString("gender",null);
        dob=preferences.getString("dob",null);


        etName = (EditText)findViewById(R.id.etPName);
        etEmail = (EditText)findViewById(R.id.etPEmail);
        etContact = (EditText)findViewById(R.id.etPContact);
        etPDob = (EditText)findViewById(R.id.etPDob);

        ivImage = (ImageView) findViewById(R.id.ivProfile);


        etName.setText(name);
        etEmail.setText(email);
        etContact.setText(contact);
        etPDob.setText(dob);

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

        etPDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ProfileActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void SelectImage(){

        final CharSequence[] items={"Camera","Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else if (items[i].equals("Gallery")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_FILE);

                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();

    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);

        if(resultCode== Activity.RESULT_OK){

            if(requestCode==REQUEST_CAMERA){

                Bundle bundle = data.getExtras();
                bmp = (Bitmap) bundle.get("data");
                ivImage.setImageBitmap(bmp);

            }else if(requestCode==SELECT_FILE){

                Uri selectedImageUri = data.getData();
                ivImage.setImageURI(selectedImageUri);
            }

        }
    }

    private void updateLabel(){
        String myformat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myformat, Locale.US);
        Log.d("updatelabel","initialised");
        etPDob.setText(sdf.format(calendar.getTime()));
    }

    public void onProfileUpdate(View view){

        name = etName.getText().toString();
        email = etEmail.getText().toString();
        contact = etContact.getText().toString();
        dob = etPDob.getText().toString();

        BitmapDrawable drawable = (BitmapDrawable) ivImage.getDrawable();
        bmp = drawable.getBitmap();

        checkError();

        if (!error){
            ByteArrayOutputStream byteArrayOutputStreamObject = new ByteArrayOutputStream();

            bmp.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStreamObject);

            byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

            final String imagedata = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

            HashMap<String,String> postdata = new HashMap<>();
            postdata.put("id",id);
            postdata.put("name",name);
            postdata.put("email",email);
            postdata.put("contact",contact);
            postdata.put("dob",dob);
            postdata.put("imagedata",imagedata);

            PostResponseAsyncTask responseAsyncTask = new PostResponseAsyncTask(this,postdata,this);
            responseAsyncTask.execute("http://dasmanoj1996md.000webhostapp.com/talko/update.php");
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
            etPDob.setError("Enter your DOB");
            error = true;
        }else if (Integer.parseInt(dob.substring(6))>= 2003) {
            etPDob.setError("Sorry your age is too small");
            error = true;
        }
        // Check for a valid bmp
        if (bmp==null) {
            error = true;
            Toast.makeText(getApplicationContext(),"Please upload a image",Toast.LENGTH_LONG).show();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isContactValid(String contact) {
        return contact.length() == 10;
    }

    @Override
    public void processFinish(String result){
        Log.d("result",result);
        String user_id="";
        String jsonname="",jsonemail="",jsoncontact="",jsongender="",jsondob="",jsonotp;

        if (result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (result.contains("updated")){
                    JSONArray userSuccessfullyAdded = jsonObject.getJSONArray("updated");
                    JSONObject user_detailsAdded = userSuccessfullyAdded.getJSONObject(0);

                    user_id = user_detailsAdded.getString("user_id");
                    jsonname = user_detailsAdded.getString("name");
                    jsonemail = user_detailsAdded.getString("email");
                    jsoncontact = user_detailsAdded.getString("contact");
                    jsongender = user_detailsAdded.getString("gender");
                    jsondob = user_detailsAdded.getString("dob");


                    Toast.makeText(getApplicationContext(),"Successfully updated",Toast.LENGTH_LONG).show();
                    Log.d("status","updated details");

                    SharedPreferences sharedPreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("user_id",String.valueOf(user_id));
                    editor.putString("name",jsonname);
                    editor.putString("email",jsonemail);
                    editor.putString("contact",jsoncontact);
                    editor.putString("gender",jsongender);
                    editor.putString("dob",jsondob);
                    editor.commit();
                }
                else {
                    Toast.makeText(getApplicationContext(),"update failed",Toast.LENGTH_LONG).show();
                }

                Log.d("jsonObject",user_id+jsonname+jsonemail+jsoncontact+jsongender+jsondob);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"update failed",Toast.LENGTH_LONG).show();
            }
        }
    }
}
