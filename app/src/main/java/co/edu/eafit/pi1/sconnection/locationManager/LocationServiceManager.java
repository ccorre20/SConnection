package co.edu.eafit.pi1.sconnection.locationManager;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class LocationServiceManager implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public GoogleApiClient mGoogleApiClient;
    private Location lastKnownLocation;
    private String latitude, longitude;
    private AppCompatActivity appCompatActivity;

    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    // Request code to use when launching the resolution activity
    private final int REQUEST_RESOLVE_ERROR = 1001;

    public LocationServiceManager(AppCompatActivity appCompatActivity){
        this.appCompatActivity = appCompatActivity;
    }

    public void onStop(){
        mGoogleApiClient.disconnect();
    }

    /**************************** Google API connection *******************************************/

    @Override
    public void onConnected(Bundle connectionHint){
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastKnownLocation != null) {
            latitude = String.valueOf(lastKnownLocation.getLatitude());
            longitude = String.valueOf(lastKnownLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int cause){
        Log.d("API", "stop");
        return;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result){
        if (mResolvingError) {return;}
        else if (result.hasResolution()){
            try{
                mResolvingError = true;
                result.startResolutionForResult(appCompatActivity, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException sie){
                mGoogleApiClient.connect();
            }
        } else {
            mResolvingError = true;
        }
        Log.d("API", "stop");
    }

    /**************************** /Google API connection ******************************************/

    public void googleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(appCompatActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        while(!mGoogleApiClient.isConnected()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d("connected", "connected");
    }

    public void connect(){
        if(!mResolvingError){
            mGoogleApiClient.connect();
            while (!mGoogleApiClient.isConnected()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d("connected", "connected");
        }
    }

    public String getCoordinates(){
        if(longitude != null && latitude != null) {
            return longitude + 's' + latitude + "sss";
        }else{
            return "ERROR";
        }
    }

}



