package com.manojdas.admin.talko.misc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.manojdas.admin.talko.ChatHomeActivity;
import com.manojdas.admin.talko.MessageActivity;
import com.manojdas.admin.talko.R;
import com.manojdas.admin.talko.sqlitedb.DatabaseHelper;
import com.manojdas.admin.talko.sqlitedb.DatabaseHelperContact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Manoj Das on 30-Mar-18.
 */

public class BackgroundService extends Service {

    private Timer timer = new Timer();

    Context context;

    public BackgroundService() {
    }

    public BackgroundService(Context context) {
        this.context=context;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences=getSharedPreferences("User", Context.MODE_PRIVATE);
        final String struser_id=preferences.getString("user_id",null);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                RMessage loginNetworkCall = new RMessage(context,struser_id);
                loginNetworkCall.execute();

            }
        },2000,10000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class RMessage extends AsyncTask<String,Void,Chat> {

        Context context;
        String strMid;
        String strMessage;
        String strusername;
        String receivername;
        String strRid;
        String strTimestand;
        DatabaseHelper db;
        DatabaseHelperContact dbc;

        public RMessage(Context context, String strRid) {
            this.context = context;
            this.strRid = strRid;
        }

        @Override
        protected Chat doInBackground(String... params) {

            Log.d("Inside","ReceiveMessage doInBackground");
            String result="";

            String query="rid="+strRid;

            try {

                Log.d("Connecting","ReceiveMessage");

                URL url = new URL("http://dasmanoj1996md.000webhostapp.com/talko/receive.php");
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                String line;
                if(responseCode == 200) {
                    for(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        (line = br.readLine()) != null; result = result + line) {

                    }
                } else {
                    result = "";
                    Log.d("Backgrd Service Bckgrnd", responseCode + "");
                }

                conn.disconnect();

            }catch (Exception e){
                Log.d("Error in Connection","",e);
            }

            Log.d("Result",result);


            try {
                JSONObject jsonObject = new JSONObject(result);
                if (result.contains("message")){
                    JSONArray arraymessage = jsonObject.getJSONArray("message");

                    for (int i=0 ; i<arraymessage.length() ; i++) {

                        JSONObject user_message = arraymessage.getJSONObject(i);
                        strMid = user_message.getString("id");
                        strMessage = user_message.getString("content");
                        strusername = user_message.getString("sid");
                        receivername = user_message.getString("rid");
                        strTimestand = user_message.getString("ts");


                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new Chat(strMid,strMessage,strusername,receivername,strTimestand);
        }

        @Override
        protected void onPostExecute(Chat chat) {
            super.onPostExecute(chat);

            db=new DatabaseHelper(getBaseContext());

            db.addMessageServer(chat);


            if(strMid!= null && strMessage!= null && strusername!= null && receivername!= null){

                dbc=new DatabaseHelperContact(getBaseContext());
                Log.d("user name",strusername);
                User user = dbc.getContactById(strusername);

                NotificationManager notificationManager =(NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

                int importance = NotificationManager.IMPORTANCE_HIGH;

                String channelid="talko";

                int channel=1996;


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                    NotificationChannel notificationChannel = new NotificationChannel(channelid,getBaseContext().getString(R.string.app_name),importance);

                    notificationChannel.setDescription("Message received");
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.GREEN);
                    notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(new  long[]{100,200,300,400,500,400,300,200,400});
                    notificationChannel.setShowBadge(false);
                    notificationManager.createNotificationChannel(notificationChannel);
                }
                NotificationCompat.Builder builder = null;
                try {
                    builder = new NotificationCompat.Builder(getBaseContext(), channelid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                builder.setSmallIcon(R.drawable.ic_notification);
                //builder.setLargeIcon()
                builder.setContentTitle(user.getName());
                builder.setContentText(strMessage);

                Intent intent=new Intent(getBaseContext(),MessageActivity.class);

                Bundle bundle= new Bundle();
                bundle.putString("name",user.getName());
                bundle.putString("number",user.getPhone());
                bundle.putString("id",strusername);
                intent.putExtras(bundle);

                PendingIntent pendingIntent=PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_ONE_SHOT);

                builder.setContentIntent(pendingIntent);
                builder.addAction(R.mipmap.ic_launcher,"Reply",pendingIntent);
                builder.setPriority(importance);
                builder.setColor(ContextCompat.getColor(getBaseContext(),R.color.colorPrimary));
                builder.setVibrate(new long[]{100,200,300,400,500,400,300,200,400});
                builder.setLights(Color.GREEN,500,5000);
                builder.setAutoCancel(true);
                notificationManager.notify(channel,builder.build());

                Toast.makeText(getBaseContext(),"message received",Toast.LENGTH_LONG).show();
            }
        }
    }
}
