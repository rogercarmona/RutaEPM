package com.racdiseno.rutaepm;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.kml.KmlLayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.util.Timer;
import java.util.TimerTask;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private SupportMapFragment mapFragment;
    public GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    public Marker[] marker = new Marker[40];
    public Marker[] markerP = new Marker[4];
    private Timer timer;

    Button boton;



    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boton = (Button) findViewById(R.id.btn_orig);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }
        }, 0, 1000);


        boton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Float zoom = Float.valueOf("16.3");
                LatLng latLng = new LatLng(6.244699, -75.573641);

                map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(zoom)
                        .bearing(290)
                        .build()));

            }
        });

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);

                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

    }

    private StringBuilder inputStreamToString(InputStream is)
    {
        String rLine = "";
        StringBuilder answer = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        try
        {
            while ((rLine = rd.readLine()) != null)
            {
                answer.append(rLine);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return answer;
    }


    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready

            //Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();

            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            //Dibujar mapa

            try {
                KmlLayer kmlLayer = new KmlLayer(map, R.raw.prueba2, getApplicationContext());
                kmlLayer.addLayerToMap();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            // Polylines are useful for marking paths and routes on the map.
            //map.addPolyline(new PolylineOptions().geodesic(true).color(20).width(1)
            //        .add(new LatLng(-33.866, 151.195))  // Sydney
            //        .add(new LatLng(-18.142, 178.431))  // Fiji
            //        .add(new LatLng(21.291, -157.821))  // Hawaii
            //        .add(new LatLng(37.423, -122.091))  // Mountain View
            //);



            Float zoom = Float.valueOf("16.3");
            //LatLng latLng = new LatLng(6.244299, -75.573941);
            LatLng latLng = new LatLng(6.244699, -75.573641);
            map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(zoom)
                    .bearing(290)
                    .build()));

            //Agregar paraderos
            for(int i = 0; i< 4; i++){ //40
                markerP[i] = map.addMarker(new MarkerOptions().position(latLng).title("Paradero").visible(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.paradero)));
            }

            latLng = new LatLng(6.245244, -75.577247);
            markerP[0].setPosition(latLng);
            latLng = new LatLng(6.246762, -75.572896);
            markerP[1].setPosition(latLng);
            latLng = new LatLng(6.246271, -75.571545);
            markerP[2].setPosition(latLng);
            latLng = new LatLng(6.242808, -75.571594);
            markerP[3].setPosition(latLng);





            //Agregar marcadores
            for(int i = 0; i< 4; i++){ //40
                //marker[i] = map.addMarker(new MarkerOptions().position(latLng).title("Marcador").visible(false).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                marker[i] = map.addMarker(new MarkerOptions().position(latLng).title("Marcador").visible(false).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_vehiculo2_48)));


            }


            latLng = new LatLng(6.244699, -75.573641);
            marker[2].setPosition(latLng);
            marker[2].setVisible(true);


            //map.addMarker(new MarkerOptions().position(latLng).title("Marcador"));

            MainActivityPermissionsDispatcher.getMyLocationWithCheck(this);
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressWarnings("all")
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        if (map != null) {
            // Now that map has loaded, let's get our location!
            map.setMyLocationEnabled(true);

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            connectClient();
        }
    }

    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /*
     * Called when the Activity becomes visible.
    */
    @Override
    protected void onStart() {
        super.onStart();
        connectClient();
    }

    /*
	 * Called when the Activity is no longer visible.
	 */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    /*
     * Handle results returned to the FragmentActivity by Google Play services
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mGoogleApiClient.connect();
                        break;
                }

        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    /*
     * Called by Location Services when the request to connect the client
     * finishes successfully. At this point, you can request the current
     * location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            //map.animateCamera(cameraUpdate);
        } else {
            //Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }


    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }


    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            //This method runs in the same thread as the UI.
            //Do something to the UI thread here

            //Toast.makeText(getApplicationContext(),"Temporizador 5seg",Toast.LENGTH_SHORT).show();
            String serverURL = "http://52.43.89.123/Rutas/rutas";
            new WebService2().execute(serverURL);
        }
    };

    public class WebService2 extends AsyncTask <String, Void, Void> {

        String IMEI;
        String Latitud;
        String Longitud;
        String Fecha;

        public JSONArray respJSON = new JSONArray();



        @Override
        protected Void doInBackground(String... params) {
            Log.i("WebService","doInBackground");

            HttpClient httpClient = new DefaultHttpClient();

            String Url = params[0];
            HttpGet get = new HttpGet(Url);
            get.setHeader("Content-type","application/json");

            try {
                HttpResponse resp = httpClient.execute(get);
                String respString = EntityUtils.toString(resp.getEntity());
                respJSON = new JSONArray(respString);

                //Log.i("IMEI","bien recibido");
                //Log.i("IMEI",respString);


                for (int i = 0;i < respJSON.length();i++){
                    JSONObject c = respJSON.getJSONObject(i);
                    //Log.i("IMEI","IMEI: " + c.getString("IMEI") + ",LAT: " + c.get("Latitud") + ",LON: " + c.get("Longitud"));
                }

            }
            catch (Exception ex){
                Log.e("ServicioRest", "error", ex);
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            String imei;
            String lat;
            String lon;
            LatLng latLng;
            Float temp;
            Float temp2;
            Float latf;
            Float lonf;

            try {
                for (int i = 0;i < respJSON.length();i++){
                    JSONObject c = respJSON.getJSONObject(i);

                    imei = c.getString("IMEI");
                    lat = c.getString("Latitud");
                    lon = c.getString("Longitud");
                    //Log.i("IMEI","IMEI:" + imei + " LAT: " + lat + " LON: " + lon);

                    if(imei.equals("359710043534610") ) {
                        lat = lat.substring(0, lat.length() - 1);
                        lon = lon.substring(0, lon.length() - 1);
                        //Log.i("IMEI","IMEI: " + imei + " LAT: " + lat + " LON: " + lon);

                        temp2 = Float.valueOf(lat.substring(0, lat.indexOf(".") - 2));
                        temp = Float.valueOf(lat.substring(lat.indexOf(".") - 2, lat.length()));
                        temp = (temp / 60);
                        latf = temp2 + temp;
                        lat = String.valueOf(latf);

                        temp2 = Float.valueOf(lon.substring(0, lon.indexOf(".") - 2));
                        temp = Float.valueOf(lon.substring(lon.indexOf(".") - 2, lon.length()));
                        temp = (temp / 60);
                        lonf = temp2 + temp;
                        lonf = (-1) * lonf;
                        lon = String.valueOf(lonf);


                        //Log.i("IMEI","IMEI: " + imei + " LAT: " + lat + " LON: " + lon);

                        latLng = new LatLng(latf, lonf);
                        marker[i].setPosition(latLng);
                        marker[i].setVisible(true);
                        marker[i].setTitle(c.getString("IMEI"));
                    }

                }
            }
            catch (Exception ex){
                Log.e("ServicioRest", "error", ex);
                ex.printStackTrace();
            }

        }
    }

}




