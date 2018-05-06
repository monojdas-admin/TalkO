package com.manojdas.admin.talko.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.manojdas.admin.talko.R;

/**
 * Created by dga on 20-01-2018.
 */

public class MessageDisplayAdapter extends ArrayAdapter {
    private Context context;
    private String[] message;
    private String[] sender;
    private String[] receiver;


    String[] struser_id,struser_name;

    public MessageDisplayAdapter(Context context, String[]  sender, String[]  message,String[] receiver, String[] struser_id) {
        super(context, R.layout.listmessage, message);
        this.context=context;
        this.sender=sender;
        this.message=message;
        this.receiver=receiver;
        this.struser_id=struser_id;
        //this.struser_name=struser_name;

        //Log.d("struser_id",struser_id);
        //Log.d("struser_name",struser_name);
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.listmessage,null,false);
        TextView ltextview=(TextView)row.findViewById(R.id.tvleft);
        TextView rtextview=(TextView)row.findViewById(R.id.tvright);

        ltextview.setVisibility(View.INVISIBLE);
        rtextview.setVisibility(View.INVISIBLE);

        //Log.d("getView",sender[position]);
        //Log.d("userid",struser_id);
        //Log.d("struser_name",struser_name);

        if (sender[position].equals(struser_id[position])){
            //if (struser_name.equals(struser_id)){
            rtextview.setText(message[position]);
            rtextview.setVisibility(View.VISIBLE);
        }else {
            ltextview.setText(message[position]);
            ltextview.setVisibility(View.VISIBLE);
        }
        return row;
    }
}
