package com.manojdas.admin.talko;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.manojdas.admin.talko.adapters.MessageCustomAdapter;
import com.manojdas.admin.talko.misc.BackgroundService;
import com.manojdas.admin.talko.misc.Contact;
import com.manojdas.admin.talko.misc.PrefManager;
import com.manojdas.admin.talko.misc.User;
import com.manojdas.admin.talko.sqlitedb.DatabaseHelperContact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listView;
    //List<Contact> contacts;

    private TextToSpeech tts;
    private ArrayList<String> questions;
    String[] name = new String[0];
    String[] phnumber = new String[0];
    String[] nid= new String[0];

    Cursor phones;

    int i=0;
    DatabaseHelperContact dbc;

    List<User> alluser;

    ContentResolver resolver;
    MessageCustomAdapter adapter;

    ArrayList<Contact> contactArrayList = new ArrayList<>();

    Bundle bundle=new Bundle();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Loading Contacts", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                ActivityCompat.requestPermissions(ChatHomeActivity.this,new String[]{Manifest.permission.READ_CONTACTS},100);

                startActivity(new Intent(getApplicationContext(),ContactActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dbc=new DatabaseHelperContact(getApplicationContext());



        i=0;
        alluser=dbc.getAllContact();
        //contacts.addAll(alluser);
        int a=alluser.size();

        name = new String[a];
        phnumber = new String[a];
        nid= new String[a];


        for(User userlist: alluser){

            name[i]=userlist.getName();
            phnumber[i]=userlist.getPhone();
            nid[i]=userlist.getId();
            i++;

            Contact contact = new Contact(userlist.getName(),userlist.getPhone(),userlist.getId());
            contactArrayList.add(contact);

            Log.d("Display All",userlist.getName()+"  "+userlist.getPhone()+"  "+userlist.getId());
        }


        adapter = new MessageCustomAdapter(getApplicationContext(),contactArrayList);
        listView = (ListView) findViewById(R.id.lvMessage);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),MessageActivity.class);

                bundle.putString("name",contactArrayList.get(position).getName());
                bundle.putString("number",contactArrayList.get(position).getNumber());
                bundle.putString("id",contactArrayList.get(position).getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });



        FloatingActionButton fabMic = (FloatingActionButton) findViewById(R.id.fabmic);
        fabMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Speak Something", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                listen();

            }
        });

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    //speak("Hello");

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

        ReceiveContact receiveContact=new ReceiveContact();
        receiveContact.execute();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_home, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)){
                    adapter.filter("");
                    listView.clearTextFilter();
                }else {
                    adapter.filter(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)){
                    adapter.filter("");
                    listView.clearTextFilter();
                }else {
                    adapter.filter(newText);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
        }else if (id == R.id.logout) {
            //return true;

            sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
            editor=sharedPreferences.edit();
            editor.putString("user_id",null);
            editor.putString("name",null);
            editor.putString("email",null);
            editor.putString("contact",null);
            editor.putString("gender",null);
            editor.putString("dob",null);
            editor.putString("checking",null);
            editor.commit();

            PrefManager prefManager = new PrefManager(getApplicationContext());
            prefManager.setFirstTimeLaunch(true);

            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        } else if (id == R.id.nav_contact) {
            startActivity(new Intent(getApplicationContext(),ContactActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
        }  else if (id == R.id.nav_share) {
            startActivity(new Intent(getApplicationContext(),AboutActivity.class));
        } else if (id == R.id.nav_send) {

            startActivity(new Intent(getApplicationContext(),ContactUsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void loadQuestions(){
        questions = new ArrayList<>();
        questions.clear();
        questions.add("Hello, What would you like to do?");
        questions.add("What is your surname?");
        questions.add("How old are you?");
        questions.add("That's all I had, thank you ");
    }

    private void listen(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);

        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String inSpeech = res.get(0);
                recognition(inSpeech);
                //textView.setText(inSpeech);

            }
        }
    }

    private void recognition(String text){
        Log.d("Speech",""+text);
        String[] speech = text.split(" ");

        for (int i=1;i<name.length;i++) {
            if (text.toLowerCase().contains(name[i].toLowerCase())) {
                Log.d("f2",""+name[i]);
                Intent intent=new Intent(getApplicationContext(),MessageActivity.class);

                bundle.putString("name",name[i]);
                bundle.putString("number",phnumber[i]);
                bundle.putString("id",nid[i]);
                intent.putExtras(bundle);
                Log.d("Voice command",name[i]+phnumber[i]+nid[i]);
                startActivity(intent);
            }
        }
        if(text.contains("hello")){
            speak(questions.get(0));
        }
        //
    }

    class ReceiveContact extends AsyncTask<Void,Void,Void> {

        String strusercontact;
        String strusername;
        String strid;

        String[] ids;
        String[] names;
        String[] numbers;

        String[] inames;
        String[] phoneNumbers;

        String[] matchids;
        String[] matchnames;
        String[] matchphoneNumbers;


        public ReceiveContact() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("Inside","Receivecontact doInBackground");

            String result="";

            try {

                Log.d("Connecting","Receivecontact");

                URL url = new URL("http://dasmanoj1996md.000webhostapp.com/talko/contact.php");
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                os.close();
                int responseCode = conn.getResponseCode();
                String line;
                if(responseCode == 200) {
                    for(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); (line = br.readLine()) != null; result = result + line) {

                    }
                } else {
                    result = "";
                    Log.d("ReceiveContact", responseCode + "");
                }
                conn.disconnect();

            }catch (Exception e){
                Log.d("Error in Connection","",e);
            }

            Log.d("Result",result);


            try {
                JSONObject jsonObject = new JSONObject(result);
                if (result.contains("contact")){
                    JSONArray arraycontact = jsonObject.getJSONArray("contact");

                    ids= new String[arraycontact.length()];
                    names= new String[arraycontact.length()];
                    numbers= new String[arraycontact.length()];

                    for (int i=0 ; i<arraycontact.length() ; i++) {
                        JSONObject jsoncontact = arraycontact.getJSONObject(i);
                        strid = jsoncontact.getString("id");
                        strusername = jsoncontact.getString("name");
                        strusercontact = jsoncontact.getString("contact");

                        ids[i] = strid;
                        names[i] = strusername;
                        numbers[i] = strusercontact;
                        Log.d("contact",ids[i]+names[i]+" "+numbers[i]);


                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {
                    Toast.makeText(getApplicationContext(), "No contacts in your contact list.", Toast.LENGTH_LONG).show();
                }

                i=0;
                inames = new String[phones.getCount()];
                phoneNumbers = new String[phones.getCount()];

                while (phones.moveToNext()) {
                    inames[i] = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    phoneNumbers[i] = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    i++;
                }

            } else {
                Log.e("Cursor close 1", "----------------");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            /*MessageCustomAdapter adapter;
            adapter = new MessageCustomAdapter(getApplicationContext(),names,numbers);
            listView = (ListView) findViewById(R.id.lvMessage);
            listView.setAdapter(adapter);

            final Bundle bundle=new Bundle();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=new Intent(getApplicationContext(),MessageActivity.class);
                    bundle.putString("name",names[position]);
                    bundle.putString("number",numbers[position]);
                    bundle.putString("id",ids[position]);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });*/



            matchids =new String[inames.length];
            matchnames =new String[inames.length];
            matchphoneNumbers =new String[inames.length];
            /*int x=0;
            for (int i=0;i<=inames.length;i++){
                for (int j=0;j<=names.length;j++){
                    if (phoneNumbers[i].contains(numbers[i])){

                        //matchnames[x]=names[j];
                        //matchphoneNumbers[x]=numbers[j];

                        Log.d("Match",names[j]);
                        Log.d("Match",numbers[j]);
                        //x=x+1;
                    }
                }
            }*/


            dbc=new DatabaseHelperContact(getApplicationContext());
            try {
                dbc.addContact(names,numbers,ids);
            }catch (Exception e){
                Log.d("add Contact","Exception",e);
            }

            new BackgroundService(ChatHomeActivity.this);
            startService(new Intent(getApplicationContext(), BackgroundService.class));
        }
    }
}
