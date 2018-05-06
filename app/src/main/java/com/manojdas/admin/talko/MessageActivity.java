package com.manojdas.admin.talko;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.manojdas.admin.talko.adapters.MessageDisplayAdapter;
import com.manojdas.admin.talko.misc.Chat;
import com.manojdas.admin.talko.misc.PrefManager;
import com.manojdas.admin.talko.sqlitedb.DatabaseHelper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MessageActivity extends AppCompatActivity implements AsyncResponse {

    ListView listView;

    EditText etmessage;
    String strMessage, receivername, receivernumber, strusername, mydate;
    String struser_id,struser_name,strRid;
    int i=0;
    int a=0;

    DatabaseHelper db;

    MessageDisplayAdapter adapter;
    List<Chat> allchats;

    private TextToSpeech tts;
    private SharedPreferences preferences;

    private final int REQ_CODE_SPEECH_INPUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etmessage = (EditText) findViewById(R.id.etMessage);

        Bundle bundle=getIntent().getExtras();
        strRid=bundle.getString("id");
        receivername=bundle.getString("name");
        receivernumber=bundle.getString("number");

        if (bundle.getString("name")!=null) {
            this.setTitle(Html.fromHtml("<font color='#21ef8b'>"+bundle.getString("name")+"</font>"));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db=new DatabaseHelper(this);

        SharedPreferences preferences=getSharedPreferences("User", Context.MODE_PRIVATE);
        struser_id=preferences.getString("user_id",null);
        struser_name=preferences.getString("name",null);


        Log.d("struser_id",struser_id);
        Log.d("struser_name",struser_name);
        Log.d("strRid",strRid);

        //display();
        displayByid();

    }

    private void display(){
        db=new DatabaseHelper(this);

        i=0;
        allchats=db.getAllMessage();
        a=allchats.size();

        String[] mid = new String[a];
        String[] content = new String[a];
        String[] sender= new String[a];
        String[] receiver= new String[a];
        String[] dateTime= new String[a];
        String[] user_id= new String[a];


        for(Chat chatlist: allchats){

            mid[i]=chatlist.getMid();
            content[i]=chatlist.getContent();
            sender[i]=chatlist.getSender();
            receiver[i]=chatlist.getReceiver();
            dateTime[i]=chatlist.getDateTime();
            user_id[i]=struser_id;
            //Log.d("user_id[i]",user_id[0]);
            i++;

            Log.d("Display All",chatlist.getMid()+"  "+chatlist.getContent()+"  "+chatlist.getSender()+"  "+chatlist.getReceiver()+"  "+chatlist.getDateTime());
        }

        adapter = new MessageDisplayAdapter(getApplicationContext(),sender,content,receiver,user_id);
        listView = (ListView) findViewById(R.id.lvmsg);
        listView.setAdapter(adapter);
        listView.setSelection(a-1);
    }

    private void displayByid(){
        db=new DatabaseHelper(this);

        i=0;
        allchats=db.getAllMessageOfContact(struser_id,strRid);
        a=allchats.size();

        String[] mid = new String[a];
        final String[] content = new String[a];
        String[] sender= new String[a];
        String[] receiver= new String[a];
        String[] dateTime= new String[a];
        String[] user_id= new String[a];


        for(Chat chatlist: allchats){

            //if ( chatlist.getSender().equals(struser_id) || chatlist.getReceiver().equals(strRid) ){
                //if ( chatlist.getSender().equals(strRid) || chatlist.getReceiver().equals(struser_id) ){

                    mid[i]=chatlist.getMid();
                    content[i]=chatlist.getContent();
                    sender[i]=chatlist.getSender();
                    receiver[i]=chatlist.getReceiver();
                    dateTime[i]=chatlist.getDateTime();
                    user_id[i]=struser_id;

                    Log.d("displayByid",chatlist.getMid()+"  "+chatlist.getContent()+"  "+chatlist.getSender()+"  "+chatlist.getReceiver()+"  "+chatlist.getDateTime());
                //}
            //}
            //user_id[i]=struser_id;
            //Log.d("user_id[i]",user_id[0]);
            i++;
        }

        adapter = new MessageDisplayAdapter(getApplicationContext(),sender,content,receiver,user_id);
        listView = (ListView) findViewById(R.id.lvmsg);
        listView.setAdapter(adapter);
        listView.setSelection(a-1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                strMessage = content[position];
                ttsengineclick();
            }
        });

    }

    public void onSend(View view){
        strMessage=etmessage.getText().toString();

        preferences=getSharedPreferences("User", Context.MODE_PRIVATE);
        strusername=preferences.getString("name",null);

        mydate = DateFormat.getDateTimeInstance().format(new Date());



        HashMap<String,String> postdata = new HashMap<String, String>();
        postdata.put("content",strMessage);
        postdata.put("sender",struser_id);
        postdata.put("receiver",receivernumber);

        PostResponseAsyncTask responseAsyncTask = new PostResponseAsyncTask(this,postdata,this);
        responseAsyncTask.execute("http://dasmanoj1996md.000webhostapp.com/talko/send.php");

        //display();
        displayByid();
        etmessage.setText("");

        ttsengine();
    }

    private void ttsengine(){

        try {
            tts =new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {

                        int result = tts.setLanguage(Locale.ENGLISH);

                        tts.setPitch(0); // set pitch level

                        tts.setSpeechRate(0); // set speech speed rate

                        if (result == TextToSpeech.LANG_MISSING_DATA
                                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("TTS", "Language is not supported");
                            Toast.makeText(getApplicationContext(),"Language is not supported", Toast.LENGTH_SHORT).show();
                        } else {
                            speakOut();
                        }

                    } else {
                        Log.e("TTS", "Initilization Failed");
                        Toast.makeText(getApplicationContext(),"Initilization Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ttsengineclick(){

        try {
            tts =new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {

                        int result = tts.setLanguage(Locale.ENGLISH);

                        tts.setPitch(0); // set pitch level

                        tts.setSpeechRate(0); // set speech speed rate

                        if (result == TextToSpeech.LANG_MISSING_DATA
                                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("TTS", "Language is not supported");
                            Toast.makeText(getApplicationContext(),"Language is not supported", Toast.LENGTH_SHORT).show();
                        } else {

                            tts.speak(strMessage, TextToSpeech.QUEUE_FLUSH, null);
                        }

                    } else {
                        Log.e("TTS", "Initilization Failed");
                        Toast.makeText(getApplicationContext(),"Initilization Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSpeak(View view){
        listen();

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    speak("Hello "+strusername);

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });
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

    private void speakOut() {

        String text = etmessage.getText().toString();

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String inSpeech = res.get(0);
                //recognition(inSpeech);
                etmessage.setText(inSpeech);
            }
        }
    }


    @Override
    public void processFinish(String s) {
        Log.d("send result ",s);

        db.addMessageClient(new Chat(null,strMessage,struser_id,strRid,mydate));
        Log.d("Add Message","Successfully Added : "+receivername+mydate);

        //display();
        displayByid();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_message, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clearChat) {
            return true;
        }else if (id == R.id.action_copy) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
