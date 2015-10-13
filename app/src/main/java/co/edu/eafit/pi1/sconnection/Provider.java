package co.edu.eafit.pi1.sconnection;

import co.edu.eafit.pi1.sconnection.Connection.Services.GetLocationConnectionService;
import co.edu.eafit.pi1.sconnection.Connection.Services.SetLocationConnectionService;
import co.edu.eafit.pi1.sconnection.Connection.Utils.CSResultReceiver;
import co.edu.eafit.pi1.sconnection.Connection.Utils.Receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class Provider extends AppCompatActivity implements Receiver,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private String              username;
    private Bundle              extra;
    private CSResultReceiver    mReceiver;
    private Location            lastKnownLocation;
    private LocationRequest     mLocationRequest;
    private Location            previous;
    private String              latitude;
    private String              longitude;
    private Handler             handler;
    private Button              arrived;
    private boolean             mResolvingError = false;
    private final int           REQUEST_RESOLVE_ERROR = 1001;
    public GoogleApiClient      mGoogleApiClient;

    /**************************** Android Activity methods ***************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);

        createLocationRequest();
        handler = new Handler();
        arrived = (Button)findViewById(R.id.provider_arrived_button);

        extra = getIntent().getExtras();
        if (extra != null) {
            username = extra.getString("username");
        }

        previous = null;
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
    public void onReceiveResult(int resultCode, Bundle resultData){
        switch (resultCode){
            case 1:  //STATUS_FINISHED
                Context context = getApplicationContext();
                CharSequence text = "Longitude: " + resultData.getString("longitude")
                                    + " Latitude: " + resultData.getString("latitude");
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_provider, menu);
        return super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.menu_, menu);
        //return true;
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
    /**************************** /Android Activity methods **************************************/

    /**************************** Location Listener method ***************************************/
    @Override
    public void onLocationChanged(Location location) {
        lastKnownLocation = location;
        arrived.performClick();
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

    public void profileClickListener(View view){
        Intent i = new Intent(this, ProviderProfile.class);
        i.putExtra("username", username);
        startActivity(i);
    }

    public void arrivedClickListener(View view){

        Context context = getApplicationContext();
        CharSequence text = "Confirmando llegada...";
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

            if(handler != null){
                handler.removeCallbacksAndMessages(null);
                handler = null;
            }

            final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SetLocationConnectionService.class);
            intent.putExtra("username", username);
            intent.putExtra("mReceiver", mReceiver);

            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    String coordinates = Provider.this.getCoordinates();
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

    public void servicesClickListener(View view){
        mReceiver = new CSResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        final Handler handler = new Handler();
        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, GetLocationConnectionService.class);
        intent.putExtra("username", username);
        intent.putExtra("mReceiver", mReceiver);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startService(intent);
                handler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    @Override
    protected void onStop(){
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
        previous = lastKnownLocation;
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastKnownLocation != null) {
            latitude = String.valueOf(lastKnownLocation.getLatitude());
            longitude = String.valueOf(lastKnownLocation.getLongitude());
        }
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

    public String getCoordinates(){
        if(longitude != null && latitude != null) {
            return longitude + 's' + latitude + "sss";
        }else{
            return "ERROR";
        }
    }

}
