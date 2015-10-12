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

public class Provider extends AppCompatActivity implements Receiver {

    String username;
    Bundle extra;
    CSResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);

        extra = getIntent().getExtras();
        if (extra != null)
            username = extra.getString("username");
    }

    @Override
    protected void onStart(){
        super.onStart();
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
        /*
        Context context = getApplicationContext();
        CharSequence text = "Confirmando llegada...";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        */

        mReceiver = new CSResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        /*
        LocationServiceManager locationServiceManager = new LocationServiceManager(this);
        locationServiceManager.googleApiClient();
        locationServiceManager.connect();
        while(!locationServiceManager.mGoogleApiClient.isConnected()){
            locationServiceManager.connect();
        }*/

        LocationServiceManager locationServiceManager = new LocationServiceManager(this);
        locationServiceManager.execute();

        String [] location = locationServiceManager
                .getCoordinates()
                .substring(0,locationServiceManager.getCoordinates().length()-3)
                .split("s");

        final Handler handler = new Handler();
        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SetLocationConnectionService.class);
        intent.putExtra("username", username);
        intent.putExtra("mReceiver", mReceiver);
        intent.putExtra("longitude", location[0]);
        intent.putExtra("latitude", location[1]);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startService(intent);
                handler.postDelayed(this, 5000);
            }
        }, 5000);

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
        super.onStop();
    }
}
