package com.manojdas.admin.talko.sqlitedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.manojdas.admin.talko.misc.Chat;
import com.manojdas.admin.talko.misc.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manoj Das on 23-Mar-18.
 */

public class DatabaseHelperContact extends SQLiteOpenHelper {

    private final static String DATABASE_NAME="contactdb";
    private final static int DATABASE_VERSION=1;

    private final static String TABLE_NAME_CONTACT="tblcontact";


    private final static String KEY_ID_CONTACT="id";
    private final static String KEY_NAME_CONTACT="name";
    private final static String KEY_NUMBER_CONTACT="number";
    private final static String KEY_NUMBERID_CONTACT="uid";

    public DatabaseHelperContact(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_CONTACT=" CREATE TABLE "+TABLE_NAME_CONTACT+" ( "
                +KEY_ID_CONTACT+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +KEY_NAME_CONTACT+" TEXT UNIQUE, "
                +KEY_NUMBER_CONTACT+" TEXT, "
                +KEY_NUMBERID_CONTACT+" TEXT "
                +" ); ";

        db.execSQL(CREATE_TABLE_CONTACT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_CONTACT);
        onCreate(db);
    }


    public void addContact(String[] name,String[] number, String[] contactid){
        SQLiteDatabase db=this.getWritableDatabase();

        for (int i=0;i<name.length;i++){
            ContentValues values=new ContentValues();
            values.put(KEY_NAME_CONTACT,name[i]);
            values.put(KEY_NUMBER_CONTACT,number[i]);
            values.put(KEY_NUMBERID_CONTACT,contactid[i]);

            Log.d("Add Contact"," "+name[i]+" "+number[i]+" "+contactid[i]);

            if (name[i]!=null && number[i]!=null) {
                db.replace(TABLE_NAME_CONTACT,   null, values);
            }
        }

        db.close();
    }

    public User getContactById(String id){
        SQLiteDatabase db=this.getWritableDatabase();

        Log.d("ID ",id);

        Cursor cursor=db.query(TABLE_NAME_CONTACT,new String[]{KEY_ID_CONTACT,KEY_NAME_CONTACT,KEY_NUMBER_CONTACT,KEY_NUMBERID_CONTACT},KEY_NUMBERID_CONTACT+"=?",new String[]{id},null,null,null,null);
        User user = new User();
        if(cursor!=null){
            cursor.moveToFirst();

        Log.d("cursor count",String.valueOf(cursor.getColumnCount()));
        user=new User(cursor.getString(1),cursor.getString(2),cursor.getString(3));
        Log.d("getbyid",cursor.getString(1)+cursor.getString(2)+cursor.getString(3));
        }
        return user;
    }

    public List<User> getAllContact(){

        List<User> messageList=new ArrayList<>();
        String sql="SELECT * FROM "+TABLE_NAME_CONTACT;

        SQLiteDatabase db=this.getWritableDatabase();

        Cursor cursor=db.rawQuery(sql,null);

        if(cursor.moveToFirst()){
            do{
                User user=new User(cursor.getString(1),cursor.getString(2),cursor.getString(3));
                messageList.add(user);

            }while (cursor.moveToNext());

        }
        db.close();
        return messageList;
    }
}
