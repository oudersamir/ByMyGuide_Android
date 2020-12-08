package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import java.util.Map;

import static com.example.myapplication.MainActivity.URL_SAVE_NAME3;


public class LongOperation  extends AsyncTask<String, Void, Void> {
    LocationsDB dbHelper;
    Context MyContext;

public LongOperation(Context context) {


    MyContext=context;
    dbHelper=new LocationsDB(MyContext);



}

    private String jsonResponse;
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

    protected void onPreExecute() {


            Cursor cursor = dbHelper.getUnsyncedNames();
            if (cursor.moveToFirst()) {
                do {

                    Log.e("synchroniser ", "content:!!!!!!!!!!!!!!!!!!!!!!!!!!!11   " + cursor.getString(cursor.getColumnIndex(LocationsDB.NAME_PLACE)));


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
        if(isURLReachable()) {
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
                Log.e("LifeStyle", "reponse to string " + responseBuffer.toString());
                String myLine = "";
                StringBuilder strBuilder = new StringBuilder();
                while ((myLine = responseBuffer.readLine()) != null) {
                    strBuilder.append(myLine);
                }

//show response (JSON encoded data)
                jsonResponse = strBuilder.toString();
                Log.e("RESPONSE", jsonResponse);
            } catch (Exception e) {
                Toast.makeText(MyContext, " server unaccesible", Toast.LENGTH_LONG).show();
                Log.e("RESPONSE Error", e.getMessage());
            }
        }
        else {
//            Toast.makeText(MyContext," server unaccesible",Toast.LENGTH_LONG).show();

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
           // Log.e("PostExecute", "OutputData: " + jsonResponse);

            String result = "";


            if (personList != null &&  !personList.isEmpty()) {
                try {
                    JsonElement jelement = new JsonParser().parse(jsonResponse);
                    JsonArray jarray = jelement.getAsJsonArray();
                    dbHelper.del();
                    for (int i = 0; i < jarray.size(); i++) {
                        JsonObject jobject = jarray.get(i).getAsJsonObject();
                        result += jobject.get("place").toString() + " --- " +
                                jobject.get("lng").toString()
                                + jobject.get("lat").toString() + "\n";

                        saveNameToLocalStorage(new place(jobject.get("lng").getAsDouble(), jobject.get("lat").getAsDouble(), jobject.get("place").getAsString(), jobject.get("zoom").getAsInt(),jobject.get("notes").getAsDouble()), MapsActivity.NAME_SYNCED_WITH_SERVER);
                    }
                    this.onProgressUpdate();
                  //  Log.e("hhh", result);
                 //  Toast.makeText(MyContext, result, Toast.LENGTH_LONG).show();
                    Log.e("samir", "Asyncktasc Finsh Update");



                } catch (Exception e) {
                    Log.e("PARSING", e.getMessage());
                }
            }
            else { Toast.makeText(MyContext," server unaccesible",Toast.LENGTH_LONG).show();}

        } catch(JsonSyntaxException e){
            Log.e("POST-Execute", e.getMessage());
        }

         }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

          Intent intentDataForMyClient = new Intent("com.example.myapplication");
          MyContext.sendBroadcast(intentDataForMyClient);

        Intent intentDataForMyClient2 = new Intent("com.example.myapplication2");
        MyContext.sendBroadcast(intentDataForMyClient2);
    }

    private void savePlace(final int id, final Double lat, final Double lng, final String place, final int zoom, final double notes) {
        Log.e("Update", place+ " 00000000000000000000000000");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.URL_SAVE_NAME2,
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
    private  void saveNameToLocalStorage(place p, int status) {
        dbHelper.addPlace(p, status);
    }
}
