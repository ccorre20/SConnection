package co.edu.eafit.pi1.sconnection;

import co.edu.eafit.pi1.sconnection.LocationManager.LocationServiceManager;

import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Provider extends AppCompatActivity /*implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener*/ {

    private GoogleApiClient mGoogleApiClient;
    private Location lastKnownLocation;
    private String latitude, longitude;
    private LocationServiceManager locationServiceManager;

    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    // Request code to use when launching the resolution activity
    private final int REQUEST_RESOLVE_ERROR = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        /*
        if(!mResolvingError){
            mGoogleApiClient.connect();
        }
        */
    }

    @Override
    protected void onStart(){
        locationServiceManager = new LocationServiceManager(this);
        locationServiceManager.googleApiClient();
        locationServiceManager.connect();
        super.onStart();
    }


    public void execute(View view){
        ClientSender clientSender = new ClientSender("10.0.2.2",8880);
        clientSender.setMessage(getCoordinates());
        clientSender.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_, menu);
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

    @Override
    protected void onStop(){
        locationServiceManager.onStop();
        /*
        mGoogleApiClient.disconnect();
        */
        super.onStop();
    }

    /**************************** Google API connection *******************************************/
    /*
    @Override
    public void onConnected(Bundle connectionHint){
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastKnownLocation != null) {
            latitude = String.valueOf(lastKnownLocation.getLatitude());
            longitude = String.valueOf(lastKnownLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int cause){}

    @Override
    public void onConnectionFailed(ConnectionResult result){
        if (mResolvingError) {return;}
        else if (result.hasResolution()){
            try{
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException sie){mGoogleApiClient.connect();}
        } else {
            mResolvingError = true;
        }
    }
    */
    /**************************** Google API connection *******************************************/
    /*
    private synchronized void googleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    */

    private String getCoordinates(){
        while(!locationServiceManager.mGoogleApiClient.isConnected()){
            locationServiceManager.connect();
        }
        return locationServiceManager.getCoordinates();
        /*
        return longitude + '-' + latitude + "---";
        */
    }

    class ClientSender extends AsyncTask<Void, Void, Void>{

        private String serverAddress;
        private int serverPort;
        private String message;
        private String response;

        public ClientSender(String serverAddress, int serverPort){
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
            this.response = "";
        }

        public void setMessage(String message){this.message = message;}

        private String getLocation(){
            return null;
        }

        @Override
        public void onPreExecute(){super.onPreExecute();}

        @Override
        protected Void doInBackground(Void... v){
            Socket s = null;
            DataOutputStream dos = null;
            PrintWriter out = null;

            try{
                s = new Socket(serverAddress, serverPort);
                dos = new DataOutputStream(s.getOutputStream());
                dos.writeUTF(message);
            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                if(s != null){
                    try{s.close();}
                    catch (IOException ioe){ioe.printStackTrace(); response = ioe.toString();}
                }
                if (dos != null){
                    try{dos.close();}
                    catch (IOException ioe){ioe.printStackTrace(); response = ioe.toString();}
                }
            }
            return null;
        }
    }
}
