package co.edu.eafit.pi1.sconnection;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.edu.eafit.pi1.sconnection.connection.services.SetLocationConnectionService;
import co.edu.eafit.pi1.sconnection.connection.utils.CSResultReceiver;
import co.edu.eafit.pi1.sconnection.connection.utils.Receiver;
import co.edu.eafit.pi1.sconnection.dialogs.ConfirmArrival;

public class User extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        Receiver{

    private Button              b, b2, b3, b4;
    private GoogleMap           map;
    private String              username;
    private Bundle              extra;
    private CSResultReceiver    mReceiver;
    private Location            lastKnownLocation;
    private LocationRequest     mLocationRequest;
    private Location            previous;
    private String              latitude;
    private String              longitude;
    private Handler             handler;
    private boolean             mResolvingError = false;
    private final int           REQUEST_RESOLVE_ERROR = 1001;
    public GoogleApiClient      mGoogleApiClient;
    CSResultReceiver receiver;
    LatLng service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        b = (Button) findViewById(R.id.user_search_button);
        b2 = (Button) findViewById(R.id.user_view_services_button);
        b3 = (Button) findViewById(R.id.user_create_service_button);
        b4 = (Button) findViewById(R.id.user_confirm);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(this);
        username = getIntent().getStringExtra("name");
        createLocationRequest();
        handler = new Handler();
        receiver = new CSResultReceiver(new Handler());
        receiver.setReceiver(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop(){
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void confirmArrival(View view){
        Intent intent = new Intent(this, ConfirmArrival.class);
        intent.putExtra("mReceiver", receiver);
        startActivity(intent);
    }

    private void beginLocationShow(){
        Context context = getApplicationContext();
        CharSequence text = "Cambio de ubicacion detectada...";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        if(mGoogleApiClient.isConnected() ){

            while (lastKnownLocation == null
                    || (previous != null
                    && !longitude.equals(String.valueOf(previous.getLongitude()))
                    && !latitude.equals(String.valueOf(previous.getLatitude())))
                    ){
                this.onConnected(Bundle.EMPTY);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            mReceiver = new CSResultReceiver(new Handler());
            mReceiver.setReceiver(this);

            final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SetLocationConnectionService.class);
            intent.putExtra("username", username);
            intent.putExtra("mReceiver", mReceiver);

            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    String coordinates = User.this.getCoordinates();
                    String[] location = coordinates
                            .substring(0, coordinates.length() - 3)
                            .split("s");
                    intent.putExtra("longitude", location[0]);
                    intent.putExtra("latitude", location[1]);
                    startService(intent);
                    handler.postDelayed(this, 5000);
                }
            }, 5000);

        } else {
            context = getApplicationContext();
            text = "Conexion no disponible";
            duration = Toast.LENGTH_LONG;

            toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData){
        switch (resultCode){
            case 0:

                break;
            case 1:  //STATUS_FINISHED
                Context context = getApplicationContext();
                CharSequence text = "Longitude: " + resultData.getString("longitude")
                        + " Latitude: " + resultData.getString("latitude");
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                break;
            case -1://username
                Context ctext = getApplicationContext();
                CharSequence cstext = "Cambio de ubicacion detectada...";

                Toast ctoast = Toast.makeText(ctext, cstext, Toast.LENGTH_SHORT);
                ctoast.show();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(resultData.getString("providers"));
                    JSONArray jsonArray = jsonObject.getJSONArray("services");
                    jsonObject = jsonArray.getJSONObject(0);
                    jsonObject = jsonObject.getJSONObject("provider");
                    jsonObject = jsonObject.getJSONObject("location");
                    if(jsonObject != null) {
                        service = new LatLng(
                                Double.parseDouble(jsonObject.getString("latitude")),
                                Double.parseDouble(jsonObject.getString("longitude"))
                        );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(service != null){
                    displayDialog();
                }
                break;
        }
    }

    private void displayDialog(){
        float [] a = new float[1];
        Location.distanceBetween(
                Double.parseDouble(latitude),
                Double.parseDouble(longitude),
                service.latitude,
                service.longitude,
                a
        );
        String ab = new String(a[0] + "");
        if(a[0] < 500.0f) {
            new AlertDialog.Builder(this)
                    .setTitle("Enhorabuena")
                    .setMessage("Estas a "+ab+" metros de tu proveedor")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Malas Noticias")
                    .setMessage("Estas a "+ab+" metros de tu proveedor")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    /**************************** Location Listener method ***************************************/
    @Override
    public void onLocationChanged(Location location) {
        lastKnownLocation = location;
        getLocation();
        LatLng loc = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
        if(loc != null && map != null){
            map.clear();
            map.addMarker(new MarkerOptions()
                            .position(loc)
                            .title("Lugar del Servicio")
            );
            map.animateCamera(CameraUpdateFactory.newLatLng(loc));
        }
    }
    /**************************** /Location Listener method **************************************/

    /**************************** Location Requests **********************************************/
    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
    /**************************** /Location Requests *********************************************/

    /**************************** Google Play services *******************************************/
    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
        previous = lastKnownLocation;
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastKnownLocation != null) {
            getLocation();
        }
        beginLocationShow();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("API", "stop");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingError) {return;}
        else if (connectionResult.hasResolution()){
            try{
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException sie){
                mGoogleApiClient.connect();
            }
        } else {
            mResolvingError = true;
        }
        Log.d("API", "stop");
    }
    /**************************** /Google Play services ******************************************/

    private void getLocation(){
        latitude = String.valueOf(lastKnownLocation.getLatitude());
        longitude = String.valueOf(lastKnownLocation.getLongitude());
    }

    public String getCoordinates(){
        if(longitude != null && latitude != null) {
            return longitude + 's' + latitude + "sss";
        }else{
            return "ERROR";
        }
    }

    public void onProviderSearchClick(View view){
        Intent i = new Intent(this, UserProviderSearch.class);
        startActivity(i);
    }

    public void onServiceListClick(View view){
        Intent i = new Intent(this, UserServiceList.class);
        i.putExtra("username", username);
        startActivity(i);
    }

    public void onServiceCreateClick(View view){
        Intent i = new Intent(this, UserCreateService.class);
        i.putExtra("username", username);
        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
