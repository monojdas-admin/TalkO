package com.manojdas.admin.talko.adapters;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.manojdas.admin.talko.R;
import com.manojdas.admin.talko.misc.Contact;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by dga on 20-01-2018.
 */

public class MessageCustomAdapter extends ArrayAdapter {
    private Context context;
    //private String[] name;
    //private String[] message;
    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private ArrayList<Contact> matchedcontacts = new ArrayList<>();

    public MessageCustomAdapter( Context context, /*String[]  name,String[] message*/ArrayList<Contact> contactArrayList) {
        super(context, R.layout.homemessage, contactArrayList);
        this.context=context;
        //this.name=name;
        //this.message=message;
        this.contactArrayList = contactArrayList;
        this.matchedcontacts = new ArrayList<>();
        this.matchedcontacts.addAll(contactArrayList);
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View row=inflater.inflate(R.layout.homemessage,null,false);
        TextView mname=(TextView)row.findViewById(R.id.tvName);
        TextView mmessage=(TextView)row.findViewById(R.id.tvMessage);
        mname.setText(contactArrayList.get(position).getName());
        mmessage.setText(contactArrayList.get(position).getNumber());

        return row;
    }


    public void filter(String chartext){
        chartext = chartext.toLowerCase(Locale.getDefault());
        contactArrayList.clear();

        Log.d("Search text1",chartext);
        if (chartext.length() == 0){
            contactArrayList.addAll(matchedcontacts);
        } else{
            for (Contact contact : matchedcontacts){
                Log.d("Search text2",contact.getName().toLowerCase(Locale.getDefault()));
                if (contact.getName().toLowerCase(Locale.getDefault()).contains(chartext)){

                    Log.d("Search text3",contact.getName().toLowerCase(Locale.getDefault()));
                    contactArrayList.add(contact);
                }
            }
        }
        notifyDataSetChanged();
    }

}
