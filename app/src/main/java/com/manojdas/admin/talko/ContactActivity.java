package com.manojdas.admin.talko;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.manojdas.admin.talko.adapters.DisplayContactAdapter;
import com.manojdas.admin.talko.misc.Chat;
import com.manojdas.admin.talko.misc.User;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {


    List<User> users;
    ListView listView;
    Cursor  phones;

    int i=0;

    ContentResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d("Contact Activity","Started");

        users = new ArrayList<User>();
        resolver = this.getContentResolver();
        listView = (ListView) findViewById(R.id.lvcontacts_list);

        if (Build.VERSION.SDK_INT >=23){
            if (!hasPermissions(ContactActivity.this, Manifest.permission.READ_CONTACTS)){
                ActivityCompat.requestPermissions(ContactActivity.this,new String[]{Manifest.permission.READ_CONTACTS},100);
            }else {
                phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                LoadContact loadContact = new LoadContact();
                loadContact.execute();
            }
        }else {
            phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            LoadContact loadContact = new LoadContact();
            loadContact.execute();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==100){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                LoadContact loadContact = new LoadContact();
                loadContact.execute();
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

    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void> {

        String[] names;
        String[] phoneNumbers;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {
                    Toast.makeText(getApplicationContext(), "No contacts in your contact list.", Toast.LENGTH_LONG).show();
                }

                while (phones.moveToNext()) {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    User selectUser = new User();
                    selectUser.setName(name);
                    selectUser.setPhone(phoneNumber);
                    users.add(selectUser);
                }

                int a = users.size();

                names = new String[a];
                phoneNumbers= new String[a];

                for(User userlist: users){

                    names[i]=/*"Name: "+*/userlist.getName();
                    phoneNumbers[i]=/*"Number: "+*/userlist.getPhone();
                    i++;

                    Log.d("Display All Contact ",userlist.getName()+"  "+userlist.getPhone());
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            DisplayContactAdapter adapter = new DisplayContactAdapter(getApplicationContext(),names,phoneNumbers);
            listView = (ListView) findViewById(R.id.lvcontacts_list);
            listView.setAdapter(adapter);
            listView.setFastScrollEnabled(true);
        }
    }

}

