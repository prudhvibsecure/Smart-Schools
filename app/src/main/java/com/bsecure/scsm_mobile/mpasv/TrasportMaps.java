package com.bsecure.scsm_mobile.mpasv;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrasportMaps extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, HttpHandler {
    GoogleMap googleMap;
    MarkerOptions markerOptions;
    LatLng latLng;
    private double latitude = 0, longitude = 0;
    private String addressText = "",school_id,student_id,transport_id;
    List<Address> addresses = null;
    Geocoder geocoder;
    IntentFilter myIntentFilter;
    LocationManager mLocationManager;
    private Intent intentData;
    private Handler habs;
    private Runnable myRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usermaps);


        myIntentFilter = new IntentFilter("com.scm.mapsview");

        intentData=getIntent();
        if (intentData!=null){
            String condition=intentData.getStringExtra("p_con");
            if (condition.startsWith("0")){
                transport_id=intentData.getStringExtra("transport_id");
               // student_id=intentData.getStringExtra("student_id");
                school_id=intentData.getStringExtra("school_id");
                getRoutes();

            }

        }


        findViewById(R.id.get_loc).setOnClickListener(this);
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        // Getting a reference to the map
        supportMapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(mBroadcatNotification, myIntentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getRoutes() {

        habs = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                getEventShowTrasport();
                    habs.postDelayed(myRunnable, 30000);

            }
        };
        habs.postDelayed(myRunnable, 3000);


    }

    @Override
    public void onBackPressed() {

        if (myRunnable != null) {
            habs.removeCallbacks(myRunnable);
        }
        super.onBackPressed();
    }

    private void getEventShowTrasport() {
        try {
            JSONObject object = new JSONObject();
            object.put("transport_id", transport_id);
           // object.put("student_id", student_id);
            object.put("school_id", school_id);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.disableProgress();
            task.userRequest("Please Wait...", 10, Paths.get_transport_location, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcatNotification);
    }

    private BroadcastReceiver mBroadcatNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equalsIgnoreCase("com.scm.mapsview")) {
                boolean permissionGranted = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

                if (permissionGranted) {
                    latitude = Double.valueOf(intent.getStringExtra("lat"));
                    longitude = Double.valueOf(intent.getStringExtra("lang"));
                    googleMap.clear();
                    googleMap.setTrafficEnabled(true);
                    googleMap.setIndoorEnabled(true);
                    googleMap.setBuildingsEnabled(true);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    String provider = locationManager.getBestProvider(criteria, true);
                    Location location = getLastKnownLocation();

                    if (location != null) {
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 5);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (addresses != null && addresses.size() > 0) {
                            // Address address = addresses.get(0);
                            String address1 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();
                            addressText = address1;
                            // +","+city+","+state+","+country+","+postalCode+","+knownName;


                        }
                        latLng = new LatLng(latitude, longitude);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        markerOptions = new MarkerOptions();
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(addressText).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_schoolbus)));
                        // Setting the position for the marker
//                        markerOptions.position(latLng);
//                        markerOptions.title(addressText);
//                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_schoolbus));
//
//                        // Placing a marker on the touched position
//                        googleMap.addMarker(markerOptions);
                        ((TextView) findViewById(R.id.loc_txt)).setText(addressText);
                    }
                }

            }

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_loc:

                if (addressText.toString().equals("")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Tasmanian")
                            .setMessage("Sorry,We do not serve this location.Please select another area.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });

                    builder.create().show();

                }
                break;

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMaps) {
        googleMap = googleMaps;
        setUpMap();
    }

    private void setUpMap() {
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (permissionGranted) {
            googleMap.setMyLocationEnabled(true);
            googleMap.setTrafficEnabled(true);
            googleMap.setIndoorEnabled(true);
            googleMap.setBuildingsEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = getLastKnownLocation();


            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 5);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addresses != null && addresses.size() > 0) {
                    // Address address = addresses.get(0);
                    String address1 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    addressText = address1;
                    // +","+city+","+state+","+country+","+postalCode+","+knownName;


                }
                //latLng = new LatLng(latitude, longitude);
                // latLng = new LatLng(latitude, longitude);


                LatLng coordinate = new LatLng(latitude, longitude);
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 19);
                googleMap.animateCamera(yourLocation);
//                markerOptions = new MarkerOptions();
//
//                // Setting the position for the marker
//                markerOptions.position(coordinate);
//                markerOptions.title(addressText);
//
//                // Placing a marker on the touched position
//                googleMap.addMarker(markerOptions);
                googleMap.addMarker(new MarkerOptions().position(coordinate).title(addressText).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                ((TextView) findViewById(R.id.loc_txt)).setText(addressText);
            }
            // Setting a click event handler for the map
//            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//
//                @Override
//                public void onMapClick(LatLng arg0) {
//
//                    // Getting the Latitude and Longitude of the touched location
//                    latLng = arg0;
//
//                    // Clears the previously touched position
//                    googleMap.clear();
//
//                    // Animating to the touched position
//                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//
//                    // Creating a marker
////                    markerOptions = new MarkerOptions();
////
////                    // Setting the position for the marker
////                    markerOptions.position(latLng);
////
////                    // Placing a marker on the touched position
////                    googleMap.addMarker(markerOptions);
//                    googleMap.addMarker(new MarkerOptions().position(latLng).title(addressText).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
//
//                    // Adding Marker on the touched location with address
//                    new ReverseGeocodingTask(getBaseContext()).execute(latLng);
//
//                }
//            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return bestLocation;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onResponse(Object results, int requestType) {

            switch (requestType) {

                    case 10:
                        try {
                        JSONObject oo = new JSONObject(results.toString());
                        if (oo.optString("statuscode").equalsIgnoreCase("200")) {
                            latitude = Double.valueOf(oo.optString("lat"));//change lat from response
                            longitude = Double.valueOf(oo.optString("lang"));//change lat from response
                            googleMap.clear();
                            googleMap.setTrafficEnabled(true);
                            googleMap.setIndoorEnabled(true);
                            googleMap.setBuildingsEnabled(true);
                            googleMap.getUiSettings().setZoomControlsEnabled(true);
                            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                            Criteria criteria = new Criteria();
                            String provider = locationManager.getBestProvider(criteria, true);
                            Location location = getLastKnownLocation();


                                try {
                                    addresses = geocoder.getFromLocation(latitude, longitude, 5);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (addresses != null && addresses.size() > 0) {
                                    // Address address = addresses.get(0);
                                    String address1 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    String city = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName();
                                    addressText = address1;
                                    // +","+city+","+state+","+country+","+postalCode+","+knownName;


                                }
                                latLng = new LatLng(latitude, longitude);
                                CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngZoom(latLng,25);
                                googleMap.animateCamera(cameraUpdate);
                                markerOptions = new MarkerOptions();
                                googleMap.addMarker(new MarkerOptions().position(latLng).title(addressText).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_schoolbus)));
                                ((TextView) findViewById(R.id.loc_txt)).setText(addressText);

                            break;
                        }else{
                            Toast.makeText(this, oo.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }

    private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String> {
        Context mContext;

        public ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
        }

        // Finding address using reverse geocoding
        @Override
        protected String doInBackground(LatLng... params) {

            latitude = params[0].latitude;
            longitude = params[0].longitude;

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 5);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {
                String address1 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                addressText = address1;
                //+","+city+","+state+","+country+","+postalCode+","+knownName;


            }

            return addressText;
        }

        @Override
        protected void onPostExecute(String addressText) {
            // Setting the title for the marker.
            // This will be displayed on taping the marker
           // markerOptions.title(addressText);
            ((TextView) findViewById(R.id.loc_txt)).setText(addressText);
            // Placing a marker on the touched position

            googleMap.addMarker(new MarkerOptions().position(latLng).title(addressText).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // {Some Code}
                    setUpMap();

                }
            }
        }
    }
}
