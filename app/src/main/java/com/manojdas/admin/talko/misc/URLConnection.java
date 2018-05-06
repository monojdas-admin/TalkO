package com.manojdas.admin.talko.misc;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Manoj Das on 20-Mar-18.
 */

public class URLConnection {

    InputStream inputStream=null;

    HttpURLConnection httpURLConnection;
    URL url;
    int responseCode;

    String query;

    public InputStream GetMethod(String strUrl){
        try {
            url=new URL(strUrl);
            httpURLConnection=(HttpURLConnection)url.openConnection();

            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(50000);
            httpURLConnection.setReadTimeout(50000);
            httpURLConnection.setDoInput(true);
            responseCode=httpURLConnection.getResponseCode();

            Log.d("responseCode",String.valueOf(responseCode));

            if(responseCode==HttpURLConnection.HTTP_OK){
                inputStream=httpURLConnection.getInputStream();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ByGetMethod","Working",e);
        }
        Log.d("inputStream",String.valueOf(inputStream));
        return inputStream;
    }

    public InputStream PostLoginMethod(String strUrl,String strName, String strEmail, String strContact, String strDob){

        query="name="+strName+"&email="+strEmail+"&contact="+strContact+"&dob="+strDob;

        try {
            url=new URL(strUrl);
            httpURLConnection=(HttpURLConnection)url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(50000);
            httpURLConnection.setReadTimeout(50000);
            httpURLConnection.setDoInput(true);
            responseCode=httpURLConnection.getResponseCode();
            Log.d("responseCode",String.valueOf(responseCode));

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            bufferedWriter.write(query);

            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            if(responseCode==HttpURLConnection.HTTP_OK){
                inputStream=httpURLConnection.getInputStream();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("PostLoginMethod","Working",e);
        }
        return inputStream;
    }

    public InputStream PostReceiveMessageMethod(String strUrl,String strRid){

        query="name="+strRid;

        try {
            url=new URL(strUrl);
            httpURLConnection=(HttpURLConnection)url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(50000);
            httpURLConnection.setReadTimeout(50000);
            httpURLConnection.setDoInput(true);
            responseCode=httpURLConnection.getResponseCode();
            Log.d("responseCode",String.valueOf(responseCode));

            if(responseCode==HttpURLConnection.HTTP_OK){
                inputStream=httpURLConnection.getInputStream();
            }

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            bufferedWriter.write(query);

            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
            Log.d("PostLoginMethod","Working",e);
        }
        return inputStream;
    }

    public InputStream PostOTPMethod(String strUrl,String strOtp){

        query="otp="+strOtp;

        try {
            url=new URL(strUrl);
            httpURLConnection=(HttpURLConnection)url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(50000);
            httpURLConnection.setReadTimeout(50000);
            httpURLConnection.setDoInput(true);
            responseCode=httpURLConnection.getResponseCode();

            DataOutputStream outputStream=new DataOutputStream(httpURLConnection.getOutputStream());
            outputStream.writeBytes(query);

            outputStream.flush();
            outputStream.close();

            if(responseCode==HttpURLConnection.HTTP_OK){
                inputStream=httpURLConnection.getInputStream();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("PostOTPMethod","Working",e);
        }
        return inputStream;
    }

    public String ConvertStreamToString(InputStream inputStream){
        String str=null;

        InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
        BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder=new StringBuilder();

        Log.d("bufferedReader",String.valueOf(bufferedReader));

        try {
            while ((str=bufferedReader.readLine())!=null){
                stringBuilder.append(str);
                Log.d("stringBuilder",str);
            }
        }catch (Exception e) {
            e.printStackTrace();
            Log.d("ConvertStreamToString","Working",e);
        }
        return str;
    }
}

