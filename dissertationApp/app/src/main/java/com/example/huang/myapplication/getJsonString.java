package com.example.huang.myapplication;

/**
 * Created by huang on 2018/3/21.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class getJsonString {
    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet request = new HttpGet(url);
            //request.setHeader("key", "AIzaSyAt8JYd8R5t3c7Z5Ee657u2MkOMUyhUlJg");

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(request);

            // receive response as inputStream
            HttpEntity entity = httpResponse.getEntity();

            if ( entity != null ) {
                InputStream instream = entity.getContent();
                result = convertInputStreamToString( instream );
                instream.close();

                return result;
            }

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return null;
    }
    public static String GETheader(String url,String key,String value){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet request = new HttpGet(url);
            request.setHeader(key, value);

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(request);

            // receive response as inputStream
            HttpEntity entity = httpResponse.getEntity();

            if ( entity != null ) {
                InputStream instream = entity.getContent();
                result = convertInputStreamToString( instream );
                instream.close();

                return result;
            }

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return null;
    }

    // convert inputstream to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public static String convertInputStreamToString2(String a) throws IOException{

        return a;

    }
}




