package com.kirov.movielist;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class JSONfunctions {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    public static JSONObject getJSONfromURL(String url) {

        {
            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                    System.out.println(line);
                }
                is.close();
                json = sb.toString();

            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }

            // try parse the string to a JSON object
            try {
                //json="403 access forhibidden";
                jObj = new JSONObject(json);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
                System.out.println("error on parse data in jsonparser.java");
            }

            // return JSON String
            return jObj;

        }
    }

    public static void senddata(String roid, String data, String numbers, String bonus, String encore, String url) {


        try {
            JSONObject json = new JSONObject();
            json.put("roid", roid);
            json.put("date", data);
            json.put("numbers", numbers);
            json.put("bonus", bonus);
            json.put("encore", encore);
            String result = postData(json, url);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static String postData(JSONObject json, String url) throws JSONException {
        HttpClient httpclient = new DefaultHttpClient();
        String ret = "";
        InputStream is;
        try {
            HttpPost httppost = new HttpPost(url);

            List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);
            nvp.add(new BasicNameValuePair("json", json.toString()));
//httppost.setHeader("Content-type", "application/json");  
            httppost.setEntity(new UrlEncodedFormEntity(nvp));
            HttpResponse response = httpclient.execute(httppost);

            if (response != null) {
                is = response.getEntity().getContent();
                ret = response.getStatusLine().toString();
                //input stream is response that can be shown back on android
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;


    }

}




