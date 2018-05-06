package com.manojdas.admin.talko.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.manojdas.admin.talko.R;

/**
 * Created by Manoj Das on 25-Mar-18.
 */

public class DisplayContactAdapter extends ArrayAdapter {

    Context context;

    String[] name;
    String[] phone;

    public DisplayContactAdapter(Context context, String[] name, String[] phone) {
        super(context, R.layout.displaycontactlayout,name);
        this.context = context;
        this.name = name;
        this.phone = phone;
    }


    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.displaycontactlayout,null,false);
        TextView iname=(TextView)row.findViewById(R.id.tvContactName);
        TextView iphone=(TextView)row.findViewById(R.id.tvContactPhoneNumber);
        iname.setText(name[position]);
        iphone.setText(phone[position]);
        return row;
    }


}
