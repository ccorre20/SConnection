package co.edu.eafit.pi1.sconnection;

import co.edu.eafit.pi1.sconnection.Connection.Services.GetLocationConnectionService;
import co.edu.eafit.pi1.sconnection.Connection.Services.LoginConnectionService;
import co.edu.eafit.pi1.sconnection.Connection.Services.SetLocationConnectionService;
import co.edu.eafit.pi1.sconnection.Connection.Utils.CSResultReceiver;
import co.edu.eafit.pi1.sconnection.Connection.Utils.Receiver;
import co.edu.eafit.pi1.sconnection.Extras.ActivityExtra;
import co.edu.eafit.pi1.sconnection.LocationManager.LocationServiceManager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Provider extends AppCompatActivity implements Receiver,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    String username;
    Bundle extra;
    CSResultReceiver mReceiver;
    public GoogleApiClient mGoogleApiClient;
    private Location lastKnownLocation;
    private Location previous;
    private String latitude, longitude;
    private boolean mResolvingError = false;
    private final int REQUEST_RESOLVE_ERROR = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);

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

            final Handler handler = new Handler();
            final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SetLocationConnectionService.class);
            intent.putExtra("username", username);
            intent.putExtra("mReceiver", mReceiver);

            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    String[] location = getCoordinates()
                            .substring(0, getCoordinates().length() - 3)
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

    @Override
    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
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
