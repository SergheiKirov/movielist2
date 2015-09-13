package com.kirov.movielist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class MainActivity extends Activity {
    public ListView listfilm;
    public EditText search;
    public TextView info;
    public JSONObject jsonobject;
    public ArrayAdapter<?> arrayAdapter;
    public WebView pict;
    public String[] title;
    public String[] year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listfilm = (ListView) findViewById(R.id.listFilm);
        listfilm.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View v,
                                    int index, long arg3) {

                showotherMovie(index);

            }
        });


        pict = (WebView) findViewById(R.id.webView1);
        pict.setInitialScale(100);

        info = (TextView) findViewById(R.id.INFO);
        //info.getBackground().setAlpha(200);

        search = (EditText) findViewById(R.id.Search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                if (isNetworkConnected())
                    try {
                        OMBDrequest();
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        if (!isNetworkConnected())
            internetallert();

    }


//Function for Searce via JSON movies

    public void OMBDrequest() throws UnsupportedEncodingException {

        String selectedItem = search.getText().toString().replace("\\s+", "+");

        String url;
        //make JSON request
        url = "http://www.omdbapi.com/?s=" + URLEncoder.encode(selectedItem, "UTF-8");
        if (selectedItem != "") {
            jsonobject = JSONfunctions.getJSONfromURL(url);

            JSONArray temp = null;
            String[] f = null;
            JSONObject[] JJ = null;
            try {
                if (jsonobject != null)
                    temp = jsonobject.getJSONArray("Search"); //looking for JSONobjects
                else
                    temp = null;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (temp != null) {
                f = new String[temp.length()];
                JJ = new JSONObject[temp.length()];
                for (int i = 0; i < f.length; i++)
                    try {
                        JJ[i] = temp.getJSONObject(i);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                title = new String[JJ.length];
                year = new String[JJ.length];


                for (int i = 0; i < JJ.length; i++) {
                    try {
                        title[i] = JJ[i].getString("Title");

                        year[i] = JJ[i].getString("Year");


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }


                Toast.makeText(this, "I have some MOVIE for you",
                        Toast.LENGTH_LONG).show();

                showresultsofsearch(title);
                getinfoMovie(title[0], year[0]);

            }
        }
    }

// Get info for MOVIE from list

    public void getinfoMovie(String title, String Year)

    {
        String url;
        //make JSON request
        if (Year.length() > 4)
            Year = Year.substring(0, 3);
        url = "http://www.omdbapi.com/?t=" + title + "&y" + Year + "&plot=full&r=json";
        url = url.replace(" ", "+");
        JSONObject JJ = JSONfunctions.getJSONfromURL(url);
        if (JJ != null) {
            String released;
            try {
                released = JJ.getString("Released");

                String runtime = JJ.getString("Runtime");
                String genre = JJ.getString("Genre");
                String actors = JJ.getString("Actors");
                String plot = JJ.getString("Plot");
                String imdbRating = JJ.getString("imdbRating");
                String poster = JJ.getString("Poster");

                showinfoMovie(Year, poster, released, runtime, genre, actors, plot);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }

    //Check if network is working
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    public void internetallert() {

        final Context context = this;
   /* Alert Dialog Code Start*/
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("INTERNET ALLERT");
        alert.setMessage("TO WARK WITH THIS APP YOU NEED WORKING INTERNET"); //Message here
        alert.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                dialog.cancel();
            }
        });
        alert.setPositiveButton("TURN ON INTERNET", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                internetON();
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
   /* Alert Dialog Code End*/


    }

    public void internetON() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
    }


    // Controller to show list on a screen
    public void showresultsofsearch(String[] input) {
        arrayAdapter = new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, input);

        listfilm.setAdapter(arrayAdapter);


    }

    // show poster and info on screen
    public void showinfoMovie(String year, String poster, String released, String runtime, String genre, String actors, String Plot) {

        pict.loadUrl(poster);
        info.setText("Year: " + year + "\nDate of release: " + released + "\nRun time: " + runtime + "\nGenre: " + genre + "\nActors: " + actors);


    }

    public void showotherMovie(int index) {
        getinfoMovie(title[index], year[index]);

    }
}
