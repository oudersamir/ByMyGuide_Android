package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.icu.text.Transliterator;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    final  static String TAG="lifeCycle";
    LocationsDB  dbHelper;
    Context context;
    BroadcastReceiver broadcastReceiver;
    public static final String DATA_SAVED_BROADCAST = "com.example.myapplication2";


  private static final String key="AIzaSyCyFi0XzVU_EapWGRx6W_Xd58tyDIPDt2M";
  //AIzaSyBPLovRAtnltVZ7zIQ5Zmw-BNH8dEnV8MI
  //AIzaSyCyFi0XzVU_EapWGRx6W_Xd58tyDIPDt2M
    //AIzaSyDZ5o2fj8fXhVz0T05qGDXF4E6ClpivkYA
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      return true;
  }
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = MapsActivity.this;
        dbHelper=new LocationsDB(this);

        dialog = new ProgressDialog(context);

        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.Map);

        MenuItem menuItem=bottomNavigationView.getMenu().findItem(R.id.Synchroniser);
        menuItem.setTitle(new String("All"));
        menuItem.setIcon(R.drawable.all);

        //the broadcast receiver to update sync status
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
              // new downloadAllPlaces2().execute();

                Log.e("samir", "UI Changed");

            }
        };

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));



       getLocationPermission();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Places.initialize(getApplicationContext(), key);

// Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));



        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place2) {
                // TODO: Get info about the selected place.
                Toast.makeText(getApplicationContext(), place2.getName(), Toast.LENGTH_LONG);
                Log.e("hh", "Place: " + place2.getName() + ", " + place2.getId());
                if(marker!=null) marker.remove();

                drawMarker(place2.getLatLng(),BitmapDescriptorFactory.HUE_RED);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place2.getLatLng(), 15));





            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("hh", "An error occurred: " + status);
            }
        });



    }

    ProgressDialog dialog;


    private class downloadAllPlaces  extends AsyncTask<MenuItem,Content,MenuItem> {


        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait..");
            dialog.show();

        }



        @Override
        protected MenuItem doInBackground(MenuItem... contents) {

            Cursor places = dbHelper.getAllLocations();
            if (places.moveToFirst()) {

                do {

                    LatLng position = new LatLng(places.getDouble(places.getColumnIndex(LocationsDB.FIELD_LAT)), places.getDouble(places.getColumnIndex(LocationsDB.FIELD_LNG)));
                    int sync = places.getInt(places.getColumnIndex(LocationsDB.COLUMN_STATUS));
                    if (sync == 0)
                        publishProgress(new Content(position, BitmapDescriptorFactory.HUE_AZURE));
                    else publishProgress(new Content(position, BitmapDescriptorFactory.HUE_GREEN));


                } while (places.moveToNext());


            }
            dbHelper.close();
            return contents[0];
        }


        @Override
        protected void onProgressUpdate(Content... values) {
            downloadAllPlaces2(values[0].getPosition(),values[0].getMode());
        }


        @Override
        protected void onPostExecute(MenuItem menuItem) {
            dialog.dismiss();
            marker=null;
                    menuItem.setTitle(new String("Clear"));
            menuItem.setIcon(R.drawable.clear);

        }


    }

    private class Content{
     private LatLng position;
     private Float mode;

        public Content(LatLng position, Float mode) {
            this.position = position;
            this.mode = mode;
        }

        public void setPosition(LatLng position) {
            this.position = position;
        }

        public void setMode(Float mode) {
            this.mode = mode;
        }

        public LatLng getPosition() {
            return position;
        }

        public Float getMode() {
            return mode;
        }

        public Content() {
        }

    }

    private void downloadAllPlaces2(LatLng position,float id) {

      drawMarker(position,id);


    }


    Double Latitude,Longtitude;


    public void getLocationPermission(){
            Log.d(TAG, "getLocationPermission: getting location permissions");
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};

            if(ContextCompat.checkSelfPermission(context,
                    FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(context,
                        COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionsGranted = true;
                    initMap();
                }else{
                    ActivityCompat.requestPermissions(MapsActivity.this,
                            permissions,
                            LOCATION_PERMISSION_REQUEST_CODE);
                }
            }else{
                ActivityCompat.requestPermissions(MapsActivity.this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
            public String getAddress(double lat, double lng) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = null;
                String add = "rien";
                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1);
                    Address obj = addresses.get(0);
                    add = obj.getAddressLine(0);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                return add;
            }

            public static final int NAME_SYNCED_WITH_SERVER = 1;
            public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;



    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
            private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
            private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
            private static final float DEFAULT_ZOOM = 15f;
private FusedLocationProviderClient  mFusedLocationProviderClient;
            private void getDeviceLocation(){
                Log.d(TAG, "getDeviceLocation: getting the devices current location");

                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

                try{
                    if(mLocationPermissionsGranted ){

                        final Task location = mFusedLocationProviderClient.getLastLocation();
                        location.addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if(task.isSuccessful() && isGPSEnabled(getApplicationContext())){
                                    Log.d(TAG, "onComplete: found location!");

                                    Location currentLocation = (Location) task.getResult();
                                    while(currentLocation==null){
                                        currentLocation = (Location) task.getResult();
                                    }

                                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                            15);
                                   // mMap.clear();
                                    mapMarker=new MarkerOptions();
                                    mapMarker.position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));

                                }else{
                                    Log.d(TAG, "onComplete: current location is null");
                                    Toast.makeText(context, "unable to get current location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }catch (SecurityException e){
                    Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
                }
            }
            private void initMap(){
                Log.d(TAG, "initMap: initializing map");
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

                mapFragment.getMapAsync(MapsActivity.this);
            }

            private void moveCamera(LatLng latLng, float zoom){
                Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
            }


            private Boolean mLocationPermissionsGranted = false;
           // private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
           // private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

            private MarkerOptions drawMarker(LatLng point,float id) {
                // Creating an instance of MarkerOptions
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting latitude and longitude for the marker
                markerOptions.position(point);
                markerOptions.icon(BitmapDescriptorFactory
                        .defaultMarker(id));

                // Adding marker on the Google Map
                marker=mMap.addMarker(markerOptions);
                marker.setTitle(getAddress(point.latitude,point.longitude));

                mapMarker=markerOptions;
                Toast.makeText(getBaseContext(), "Marker is added to the Map", Toast.LENGTH_SHORT).show();
                Toast.makeText(context,getAddress(point.latitude,point.longitude),Toast.LENGTH_LONG).show();
            return markerOptions;
            }


    private  void saveNameToServer(final place place) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Name...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.URL_SAVE_NAME2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //if there is a success
                                //storing the name to sqlite with status synced
                                saveNameToLocalStorage(place, NAME_SYNCED_WITH_SERVER);

                                drawMarker(new LatLng(place.getLat(),place.getLng()),BitmapDescriptorFactory.HUE_GREEN);
                                marker=null;


                            } else {
                                //if there is some error
                                //saving the name to sqlite with status unsynced
                                saveNameToLocalStorage(place, NAME_NOT_SYNCED_WITH_SERVER);
                                drawMarker(new LatLng(place.getLat(),place.getLng()),BitmapDescriptorFactory.HUE_AZURE);
                                marker=null;



                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        //on error storing the name to sqlite with status unsynced
                        saveNameToLocalStorage(place, NAME_NOT_SYNCED_WITH_SERVER);
                        progressDialog.dismiss();
                        drawMarker(new LatLng(place.getLat(),place.getLng()),BitmapDescriptorFactory.HUE_AZURE);
                        marker=null;


                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("place", place.getPlace());
                params.put("lat", place.getLat()+"");
                params.put("lng", place.getLng()+"");
                params.put("zoom", place.getZoom()+"");
                params.put("notes", place.getNotes()+"");


                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
    private  void saveNameToLocalStorage(place p, int status) {
        dbHelper.addPlace(p, status);
    }





    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private String m_Text = "";

    private MarkerOptions mapMarker;
    private Marker marker;
    ArrayList<MarkerOptions> Markers;
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        Markers=new ArrayList<MarkerOptions>();
        mMap.getUiSettings().setZoomControlsEnabled(true);


      /* try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.blue));
            if (!success) {
                Log.e("color111", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("color22", "Can't find style. Error: ", e);
        }*/
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.Home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.Map:
                     return true;
                    case R.id.Synchroniser:
                     if(menuItem.getTitle().equals("All"))
                       {   mMap.clear();
                        new downloadAllPlaces().execute(menuItem);
                       }else {
                           mMap.clear();
                           menuItem.setTitle(new String("All"));
                           menuItem.setIcon(R.drawable.all);

                       }
               return true;

                }
                return false;
            }
        });

       Marker marker2;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                if(marker!=null) marker.remove();
                drawMarker(point,BitmapDescriptorFactory.HUE_RED);




            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                                         @Override
                                         public void onMarkerDragStart(Marker marker) {

                                             Toast.makeText(context,getAddress(marker.getPosition().latitude,marker.getPosition().longitude),Toast.LENGTH_LONG).show();
                                         }

            @Override
            public void onMarkerDrag(Marker marker) {
                Toast.makeText(context,getAddress(marker.getPosition().latitude,marker.getPosition().longitude),Toast.LENGTH_LONG).show();

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Toast.makeText(context,getAddress(marker.getPosition().latitude,marker.getPosition().longitude),Toast.LENGTH_LONG).show();
            }

        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng point) {




               if (mapMarker != null) {

                    Toast.makeText(context, getAddress(mapMarker.getPosition().latitude, mapMarker.getPosition().longitude), Toast.LENGTH_LONG).show();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("Hello")
                            .setMessage("voulez vous ajoutez cet location")
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {

                                    dialoginterface.cancel();
                                }
                            })
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    if (mapMarker != null) {
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                        builder2.setTitle("Note sur 10");

// Set up the input
                                        final EditText input = new EditText(getApplicationContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                        input.setInputType( InputType.TYPE_CLASS_NUMBER);
                                        input.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "10")});



                                        builder2.setView(input);

// Set up the buttons
                                        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                m_Text = input.getText().toString();
                                                dialog.cancel();
                                                Toast.makeText(getApplicationContext(), m_Text, Toast.LENGTH_SHORT).show();
                                              if( !m_Text.equals("")) {

                                                    String aa = getAddress(mapMarker.getPosition().latitude, mapMarker.getPosition().longitude);
                                                    saveNameToServer(new place(mapMarker.getPosition().longitude, mapMarker.getPosition().latitude, aa, (int) mMap.getCameraPosition().zoom,Double.parseDouble(m_Text)));


                                                  //  Toast.makeText(getBaseContext(), "  added to local " + point.latitude + "", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

                                        builder2.show();


                                    }
                                }
                            }).show();

                } else {
                    Toast.makeText(getBaseContext(), "  No Marker Selected" ,Toast.LENGTH_SHORT).show();


                }


            }
        });
        if (mLocationPermissionsGranted) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            Intent myLocalIntent = getIntent();
// look into the bundle sent to Activity2 for data items
           if(getIntent().getDoubleExtra("lat",0.0)!=0.0 &&
                    getIntent().getDoubleExtra("lng",0.0)!=0.0){
                Bundle myBundle = myLocalIntent.getExtras();
                Latitude = myBundle.getDouble("lat");
                Longtitude = myBundle.getDouble("lng");
                int status=myBundle.getInt("status");

                LatLng  position=new LatLng(Latitude,Longtitude);
                if(status==0)
                drawMarker(position,BitmapDescriptorFactory.HUE_AZURE);
                else
                drawMarker(position,BitmapDescriptorFactory.HUE_GREEN);

               //  mMap.addMarker(new MarkerOptions().position(place2.getLatLng()));
             //   Toast.makeText(getApplicationContext(), Latitude+" lat "+Longtitude+" lng ", Toast.LENGTH_SHORT).show();

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom((position), 15));

            }




        }
    }
    public class InputFilterMinMax implements InputFilter {
        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }


    public boolean isGPSEnabled (Context mContext){
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
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


    private class downloadAllPlaces2  extends AsyncTask<Void,Content,Void> {


        @Override
        protected void onPreExecute() {

        }



        @Override
        protected Void doInBackground(Void... contents) {

            Cursor places = dbHelper.getAllLocations();
            if (places.moveToFirst()) {

                do {

                    LatLng position = new LatLng(places.getDouble(places.getColumnIndex(LocationsDB.FIELD_LAT)), places.getDouble(places.getColumnIndex(LocationsDB.FIELD_LNG)));
                    int sync = places.getInt(places.getColumnIndex(LocationsDB.COLUMN_STATUS));
                    if (sync == 0)
                        publishProgress(new Content(position, BitmapDescriptorFactory.HUE_AZURE));
                    else publishProgress(new Content(position, BitmapDescriptorFactory.HUE_GREEN));


                } while (places.moveToNext());


            }
            dbHelper.close();
            return null;
        }


        @Override
        protected void onProgressUpdate(Content... values) {
            downloadAllPlaces2(values[0].getPosition(),values[0].getMode());
        }
        @Override
        protected void onPostExecute(Void menuItem) {
            marker=null;


        }


    }


}
