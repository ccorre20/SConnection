package co.edu.eafit.pi1.sconnection.Connection.Services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import co.edu.eafit.pi1.sconnection.LocationManager.LocationServiceManager;

/**
 * Created by tflr on 10/11/15.
 */
public class LocationConnectionService extends IntentService {

    public static final int STATUS_RUNNING          = 0;
    public static final int STATUS_FINISHED         = 1;

    String uname;
    LocationServiceManager locationServiceManager;

    public LocationConnectionService(AppCompatActivity appCompatActivity){
        super(LocationConnectionService.class.getName());
        uname = new String();

        locationServiceManager = new LocationServiceManager(appCompatActivity);
    }

    @Override
    public void onHandleIntent(Intent intent){
        final ResultReceiver receiver = intent.getParcelableExtra("mReceiver");
        uname = intent.getStringExtra("username");

        locationServiceManager.googleApiClient();
        while(!locationServiceManager.mGoogleApiClient.isConnected()){
            locationServiceManager.connect();
        }

        Bundle bundle = new Bundle();

        if (uname != null){
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            String [] location = locationServiceManager
                    .getCoordinates()
                    .substring(0,locationServiceManager.getCoordinates().length()-3)
                    .split("s");
            if (location.length == 2){
                bundle.putString("Location", location.toString());
            }
        }

        receiver.send(STATUS_FINISHED, bundle);
    }
}
