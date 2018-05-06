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

public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME="messagedb";
    private final static int DATABASE_VERSION=1;

    private final static String TABLE_NAME="tblmessage";

    private final static String KEY_ID="id";
    private final static String KEY_MESSAGEID="messageid";
    private final static String KEY_CONTENT="content";
    private final static String KEY_SENDER="sender";
    private final static String KEY_RECEIVER="receiver";
    private final static String KEY_TIME="time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE=" CREATE TABLE "+TABLE_NAME+" ( "
                +KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +KEY_MESSAGEID+" TEXT UNIQUE, "
                +KEY_CONTENT+" TEXT, "
                +KEY_SENDER+" TEXT, "
                +KEY_RECEIVER+" TEXT, "
                +KEY_TIME+" DATETIME "
                +" ); ";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public void addMessageServer(Chat chat){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(KEY_MESSAGEID,chat.getMid());
        values.put(KEY_CONTENT,chat.getContent());
        values.put(KEY_SENDER,chat.getSender());
        values.put(KEY_RECEIVER,chat.getReceiver());
        values.put(KEY_TIME,chat.getDateTime());

        Log.d("Add Message"," "+chat.getMid()+" "+chat.getContent()+" "+chat.getSender()+" "+chat.getReceiver()+" "+chat.getDateTime());

        if (chat.getContent()!=null) {
            db.replace(TABLE_NAME, null, values);
        }
        db.close();
    }
    public void addMessageClient(Chat chat){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(KEY_MESSAGEID,chat.getMid());
        values.put(KEY_CONTENT,chat.getContent());
        values.put(KEY_SENDER,chat.getSender());
        values.put(KEY_RECEIVER,chat.getReceiver());
        values.put(KEY_TIME,chat.getDateTime());

        Log.d("Add Message"," "+chat.getMid()+" "+chat.getContent()+" "+chat.getSender()+" "+chat.getReceiver()+" "+chat.getDateTime());

        if (chat.getContent()!=null) {
            db.insert(TABLE_NAME, null, values);
        }
        db.close();
    }


    public Chat getMessageByContact(String contact){
        SQLiteDatabase db=this.getWritableDatabase();

        Cursor cursor=db.query(TABLE_NAME,new String[]{KEY_ID,KEY_CONTENT,KEY_SENDER,KEY_RECEIVER,KEY_TIME},KEY_ID+"=?",new String[]{contact},null,null,null,null);

        if(cursor!=null)
            cursor.moveToFirst();
        Chat chat=new Chat(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
        return chat;
    }


    public List<Chat> getAllMessage(){

        List<Chat> messageList=new ArrayList<>();
        String sql="SELECT * FROM "+TABLE_NAME;

        SQLiteDatabase db=this.getWritableDatabase();

        Cursor cursor=db.rawQuery(sql,null);

        if(cursor.moveToFirst()){
            do{
                Chat chat=new Chat(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
                messageList.add(chat);

            }while (cursor.moveToNext());

        }
        db.close();
        return messageList;
    }

    public List<Chat> getAllMessageOfContact(String uid,String rid){

        List<Chat> messageList=new ArrayList<>();
        String sql="SELECT * FROM "+TABLE_NAME+" WHERE ("+KEY_SENDER+" LIKE "+uid+" OR "+KEY_SENDER+" LIKE "+rid+") AND ("+KEY_RECEIVER+" LIKE "+rid+" OR "+KEY_RECEIVER+" LIKE "+uid+")";

        SQLiteDatabase db=this.getWritableDatabase();

        Cursor cursor=db.rawQuery(sql,null);

        if(cursor.moveToFirst()){
            do{
                Chat chat=new Chat(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
                messageList.add(chat);

            }while (cursor.moveToNext());
            db.close();
        }
        db.close();
        return messageList;
    }

    public  int deleteChat(Chat chat){

        SQLiteDatabase db=this.getWritableDatabase();
        int a=db.delete(TABLE_NAME,KEY_ID+"=?",new String[]{String.valueOf(chat.getId())});

        Log.d("Delete Stock"," "+chat.getContent()+" "+chat.getSender()+" "+chat.getReceiver()+" "+chat.getDateTime());
        db.close();
        return a;
    }
}
