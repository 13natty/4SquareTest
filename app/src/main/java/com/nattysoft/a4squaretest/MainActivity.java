package com.nattysoft.a4squaretest;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import android.Manifest;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends ListActivity implements LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    static ArrayList<String> imagesURLArray;
    static ArrayList<String> imagesFullSizes;
    ArrayList<FoursquareVenue> venuesList;

    // the foursquare client_id and the client_secret
    final String CLIENT_ID = "EI43XH351ACCQMZMPWKPO0G5QPVWFMDA40JZPJMKG3IQW5SQ";
    final String CLIENT_SECRET = "A01VL1MGZVF545RSOTKAY34GNXM3RYYAQOWX0J4Q4C5QP0NL";

    // we will need to take the latitude and the logntitude from a certain point
    // this is the center of New York
    String latitude = "40.7463956";
    String longtitude = "-73.9852992";

    ArrayAdapter myAdapter;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private Location oldLocation;
    private List<RowItem> rowItems;
    private ArrayList<String> listTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //show error dialog if Google Play Services not available
        if (!isGooglePlayServicesAvailable()) {
            Log.d("onCreate", "Google Play Services not available. Ending Test case.");
            finish();
        }
        else {
            Log.d("onCreate", "Google Play Services available. Continuing.");
        }

        imagesURLArray = new ArrayList<>();
        imagesFullSizes = new ArrayList<>();
        buildGoogleApiClient();


    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        longtitude = Double.toString(location.getLongitude());
        latitude = Double.toString(location.getLatitude());

        if(oldLocation != null){
            Log.d("Distance is >>>>> "," : "+oldLocation.distanceTo(location));
        }

        if(oldLocation == null || oldLocation.distanceTo(location)>10) {
            // start the AsyncTask that makes the call for the venus search.
            new fourquare().execute();
        }
        oldLocation = location;

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class fourquare extends AsyncTask {

        String temp;

        @Override
        protected Object doInBackground(Object[] objects) {
            // make Call to the url
            // temp = makeCall("https://api.foursquare.com/v2/venues/search?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&v=20161209&venuePhotos=1&ll="+latitude+","+longtitude);
            temp = makeCall("https://api.foursquare.com/v2/venues/explore?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&v=20161209&venuePhotos=1&ll="+latitude+","+longtitude);
            return null;
        }

        @Override
        protected void onPreExecute() {
            // we can start a progress bar here
        }

        @Override
        protected void onPostExecute(Object result) {
            if (temp == null) {
                // we have an error to the call
                // we can also stop the progress bar
            } else {
                // all things went right

                // parseFoursquare venues search result
                venuesList = parseFoursquare(temp);

                rowItems = new ArrayList<RowItem>();
                listTitle = new ArrayList();
                for (int i = 0; i < venuesList.size(); i++) {
                    // make a list of the venus that are loaded in the list.
                    // show the name, the category and the city
                    listTitle.add(i, venuesList.get(i).getName() + ", " + venuesList.get(i).getCategory() + "" + venuesList.get(i).getCity());
                    RowItem item = new RowItem(imagesURLArray.get(i), listTitle.get(i));
                    rowItems.add(item);
                }

                // set the results to the list
                // and show them in the xml
                myAdapter = new CustomListViewAdapter(MainActivity.this, R.layout.row_layout, rowItems);
                setListAdapter(myAdapter);
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast toast = Toast.makeText(getApplicationContext(),
                "Item " + (position + 1) + ": " + rowItems.get(position),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        Intent i =new Intent(this,DetailsActivity.class);
        String url = imagesURLArray.get(position);
        i.putExtra("details", listTitle.get(position));
        i.putExtra("url", url.replaceAll("500x500", imagesFullSizes.get(position)));
        this.startActivity(i);
    }

    public static String makeCall(String url) {

        // string buffers the url
        StringBuffer buffer_string = new StringBuffer(url);
        String replyString = "";

        // instanciate an HttpClient
        HttpClient httpclient = new DefaultHttpClient();
        // instanciate an HttpGet
        HttpGet httpget = new HttpGet(buffer_string.toString());

        try {
            // get the responce of the httpclient execution of the url
            HttpResponse response = httpclient.execute(httpget);
            InputStream is = response.getEntity().getContent();

            // buffer input stream the result
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(20);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            // the result as a string is ready for parsing
            replyString = new String(baf.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // trim the whitespaces
        return replyString.trim();
    }

    private static ArrayList<FoursquareVenue> parseFoursquare(final String response) {

        ArrayList<FoursquareVenue> temp = new ArrayList<FoursquareVenue>();
        try {

            // make an jsonObject in order to parse the response
            JSONObject jsonObject = new JSONObject(response);

            Log.d("parseFoursquare : ", jsonObject.toString());
            // make an jsonObject in order to parse the response
            if (jsonObject.has("response")) {
                if (jsonObject.getJSONObject("response").has("groups")) {
                    JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("groups").getJSONObject(0).getJSONArray("items");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        FoursquareVenue poi = new FoursquareVenue();
                        if (jsonArray.getJSONObject(i).has("venue")) {
                            JSONObject venue = jsonArray.getJSONObject(i).getJSONObject("venue");
                            poi.setName(venue.getString("name"));
                            if(venue.has("id"))
                                poi.setID(venue.getString("id"));

                            if (venue.has("location")) {
                                JSONObject location = venue.getJSONObject("location");
                                if (location.has("address")) {
                                    if (location.has("city")) {
                                        poi.setCity(venue.getJSONObject("location").getString("city"));
                                    }
                                    if (venue.has("categories")) {
                                        if (venue.getJSONArray("categories").length() > 0) {
                                            if (venue.getJSONArray("categories").getJSONObject(0).has("icon")) {
                                                poi.setCategory(venue.getJSONArray("categories").getJSONObject(0).getString("name"));
                                            }
                                        }
                                    }
                                    temp.add(poi);
                                }
                            }

                            if (venue.has("photos")) {
                                JSONObject photos = venue.getJSONObject("photos");
                                int photoCount = Integer.parseInt(photos.getString("count"));
                                if(photoCount>0) {
                                    JSONArray groups = photos.getJSONArray("groups");
                                    JSONArray photoItems = groups.getJSONObject(0).getJSONArray("items");
                                    String photoURL = photoItems.getJSONObject(0).getString("prefix");
                                    photoURL += "500x500";
                                    photoURL += photoItems.getJSONObject(0).getString("suffix");
                                    imagesURLArray.add(photoURL);
                                    imagesFullSizes.add(photoItems.getJSONObject(0).getString("width")+"x"+photoItems.getJSONObject(0).getString("height"));
                                    Log.d("the URL ", "" + photoURL);
                                }else{
                                    if (venue.has("featuredPhotos")) {
                                        JSONObject featuredPhotos = venue.getJSONObject("featuredPhotos");
                                        JSONArray photoItems = featuredPhotos.getJSONArray("items");
                                        String photoURL = photoItems.getJSONObject(0).getString("prefix");
                                        photoURL += "200x200";
                                        photoURL += photoItems.getJSONObject(0).getString("suffix");
                                        imagesURLArray.add(photoURL);
                                        imagesFullSizes.add(photoItems.getJSONObject(0).getString("width")+"x"+photoItems.getJSONObject(0).getString("height"));
                                        Log.d("the URL ", ""+photoURL);

                                    }
                                }

                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<FoursquareVenue>();
        }
        return temp;

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
}
