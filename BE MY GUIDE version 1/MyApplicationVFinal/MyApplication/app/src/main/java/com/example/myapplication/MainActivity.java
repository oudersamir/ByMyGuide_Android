package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;

import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.myapplication.MapsActivity.NAME_SYNCED_WITH_SERVER;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private LocationsDB dbHelper;
    private PlaceAdapter adapter;
    private List<place> places;
    BroadcastReceiver broadcastReceiver;
    public static final String DATA_SAVED_BROADCAST = "com.example.myapplication";

    Intent   intentMyService1;
    ComponentName service;

    TextView t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t1=(TextView)findViewById(R.id.TextCount);
        places=new ArrayList<>();
        MyContext=this;

        dialog = new ProgressDialog(MyContext);
       // content();
        intentMyService1 = new Intent(this, MyService.class);

        //initialize the variables
        mRecyclerView =findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


       loadPlace();
        service=startService(intentMyService1);
        //the broadcast receiver to update sync status
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //loading the names again
                loadPlace();
                Log.e("samir", "UI Changed");

            }
        };

        //registering the broadcast receiver to update sync status
     registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.Home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.Home:
                        return true;
                    case R.id.Map:
                        startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.Synchroniser:
                        new LongOperation().execute(URL_SAVE_NAME3);

                        return true;

                }
                return false;
            }
        });



    }


    public  void loadPlace(){
        places.clear();
        PlacesrecyclerView("");

    }

    private void PlacesrecyclerView(String filter){
        dbHelper = new LocationsDB(this);
        places=dbHelper.PlacesList(filter);
        adapter = new PlaceAdapter(places, this, mRecyclerView);
        mRecyclerView.setAdapter(adapter);
      // Toast.makeText(this,dbHelper.PlacesList(filter).toString(),Toast.LENGTH_LONG).show();

    }

    public void maper(View v){
                    Intent i = new Intent(this, MapsActivity.class);
                    startActivity(i);
    }





    static public boolean isURLReachable() {

            try {
                URL url = new URL(URL_SAVE_NAME3);   // Change to "http://google.com" for www  test.
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(2 * 1000);          // 10 s.
                urlc.connect();
                if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).
                    Log.wtf("Connection", "Success !");
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
       }

    private void savePlace(final int id,final Double lat,final Double lng,final String place,final int zoom,final double notes) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                dbHelper.updatePlaceStatus(id, MapsActivity.NAME_SYNCED_WITH_SERVER);


                            }else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("place", place);
                params.put("lat", (lat + "").trim());
                params.put("lng", (lng + "").trim());
                params.put("notes", (notes + "").trim());

                params.put("zoom", (zoom + "").trim());

                return params;
            }
        };
        VolleySingleton.getInstance(MyContext).addToRequestQueue(stringRequest);

    }
    public void synchroniser(View V){
        new LongOperation().execute(URL_SAVE_NAME3);
    }





    private Context MyContext;

    ProgressDialog dialog;


    private class LongOperation extends AsyncTask<String, Void, Void> {


        private String jsonResponse;

        protected void onPreExecute() {

            dialog.setMessage("Please wait..");
            dialog.show();

                Cursor cursor = dbHelper.getUnsyncedNames();
                if (cursor.moveToFirst()) {
                    do {
                                              //calling the method to save the unsynced name to MySQL
                        savePlace(
                                cursor.getInt(cursor.getColumnIndex(LocationsDB.FIELD_ROW_ID)),
                                cursor.getDouble(cursor.getColumnIndex(LocationsDB.FIELD_LAT)),
                                cursor.getDouble(cursor.getColumnIndex(LocationsDB.FIELD_LNG)),
                                cursor.getString(cursor.getColumnIndex(LocationsDB.NAME_PLACE)),
                                cursor.getInt(cursor.getColumnIndex(LocationsDB.FIELD_ZOOM)),
                                cursor.getDouble(cursor.getColumnIndex(LocationsDB.NOTES))


                        );
                    } while (cursor.moveToNext());

            }


        }

        protected Void doInBackground(String... urls) {
            if (isURLReachable()) {


                try {


                    // STEP1. Create a HttpURLConnection object releasing REQUEST to given site
                    URL url = new URL(urls[0]); //argument supplied in the call to AsyncTask
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("User-Agent", "");
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.connect();

                    // STEP2. wait for incoming RESPONSE stream, place data in a buffer
                    InputStream isResponse = urlConnection.getInputStream();
                    BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));
                    // STEP3. Arriving JSON fragments are concatenate into a StringBuilder
                    //Log.e("LifeStyle", "reponse to string " + responseBuffer.toString());
                    String myLine = "";
                    StringBuilder strBuilder = new StringBuilder();
                    while ((myLine = responseBuffer.readLine()) != null) {
                        strBuilder.append(myLine);
                    }
//show response (JSON encoded data)
                    jsonResponse = strBuilder.toString();
                    //Log.e("RESPONSE", jsonResponse);
                } catch (Exception e) {
                    Log.e("RESPONSE Error", e.getMessage());
                }
            }else {
                dialog.dismiss();
//                Toast.makeText(MyContext," server unaccesible",Toast.LENGTH_LONG).show();

            }
            return null; // needed to gracefully terminate Void method
        }

        protected void onPostExecute(Void unused) {
                try {
// update GUI with JSON Response
// Step4. Convert JSON list into a Java collection of Person objects
// prepare to decode JSON response and create Java list
                    Gson gson = new Gson();
                    Log.e("PostExecute", "content: " + jsonResponse);
// set (host) Java type of encoded JSON response
                    Type listType = new TypeToken<ArrayList<place>>() {
                    }.getType();
                    Log.e("PostExecute", "arrayType: " + listType.toString());
// decode JSON string into appropriate Java container
                    ArrayList<place> personList = gson.fromJson(jsonResponse, listType);
                    Log.e("PostExecute", "OutputData: " + jsonResponse);

                    String result = "";


                    if (personList != null  &&  !personList.isEmpty()) {
                        try {
                            JsonElement jelement = new JsonParser().parse(jsonResponse);
                            JsonArray jarray = jelement.getAsJsonArray();
                            dbHelper.del();
                            for (int i = 0; i < jarray.size(); i++) {
                                JsonObject jobject = jarray.get(i).getAsJsonObject();
                                result += jobject.get("place").toString() + " --- " +
                                        jobject.get("lng").toString()
                                        + jobject.get("lat").toString() + "\n";

                               saveNameToLocalStorage(new place(jobject.get("lng").getAsDouble(), jobject.get("lat").getAsDouble(), jobject.get("place").getAsString(), jobject.get("zoom").getAsInt(), jobject.get("notes").getAsDouble()), NAME_SYNCED_WITH_SERVER);
                            }







                           // Log.e("hhh", result);
                           // Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();




                        } catch (Exception e) {
                           // Log.e("PARSING", e.getMessage());
                        }
                    }
                                        else {
                        dialog.dismiss();
                      Toast.makeText(MyContext," server unaccesible",Toast.LENGTH_LONG).show();}

                    } catch(JsonSyntaxException e){
                        Log.e("POST-Execute", e.getMessage());
                    }

            Intent intentDataForMyClient = new Intent("com.example.myapplication");
            sendBroadcast(intentDataForMyClient);


            dialog.dismiss();

        }

    }
    private  void saveNameToLocalStorage(place p, int status) {
        dbHelper.addPlace(p, status);
    }


    public final static String URL_SAVE_NAME="http://192.168.43.196/oudersamir/AddLocation.php";
    public final static String URL_SAVE_NAME2="http://192.168.43.196/oudersamir/AddLocation2.php";
    public final static String URL_SAVE_NAME3="http://192.168.43.196/oudersamir/SelectAllLocaux.php";

    public final static String URL_SAVE_NAME33="https://35a40c8a.ngrok.io/oudersamir/SelectAllLocaux.php";
    public final static String URL_SAVE_NAME22="https://35a40c8a.ngrok.io/oudersamir/AddLocation2.php";
    public final static String URL_SAVE_NAME1="https://35a40c8a.ngrok.io/oudersamir/AddLocation.php";


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
           // stopService(intentMyService3);
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            Log.e ("MAIN3-DESTROY>>>", e.getMessage() );
        }
        Log.e ("MAIN3-DESTROY>>>" , "Adios" );
    }



}
